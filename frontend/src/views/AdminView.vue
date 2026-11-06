<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { CATEGORIES, getCategory } from '../data/categories'
import { useGuides } from '../composables/useGuides'
import { useAuth } from '../stores/auth'
import { formatDate } from '../utils/format'
import type { CategoryId, Guide } from '../types/guide'

const router = useRouter()
const { isLoggedIn } = useAuth()
const { guides, loading, failed, loadGuides, invalidate } = useGuides()

const view = ref<'list' | 'form'>('list')
/** 空字符串表示新建 */
const editingSlug = ref('')
const formLoading = ref(false)
const submitting = ref(false)
const error = ref('')
const listError = ref('')

const sortedGuides = computed(() =>
  [...guides.value].sort((a, b) => b.updatedAt.localeCompare(a.updatedAt)),
)

function blankForm() {
  return {
    slug: '',
    title: '',
    subtitle: '',
    category: 'boss' as CategoryId,
    region: '',
    summary: '',
    tagsText: '',
    watermark: '',
    cover: '',
    badge: '',
    difficulty: 0,
    views: 0,
    likes: 0,
    readMinutes: 0,
    updatedAt: new Date().toISOString().slice(0, 10),
    featured: false,
    themeAccent: '',
    content: '',
  }
}

const form = reactive(blankForm())

function startCreate() {
  Object.assign(form, blankForm())
  editingSlug.value = ''
  error.value = ''
  view.value = 'form'
}

/** 编辑前拉完整数据（列表接口不含 content） */
async function startEdit(slug: string) {
  listError.value = ''
  error.value = ''
  editingSlug.value = slug
  view.value = 'form'
  formLoading.value = true
  try {
    const guide = await api<Guide>(`/guides/${slug}`)
    Object.assign(form, {
      slug: guide.slug,
      title: guide.title,
      subtitle: guide.subtitle,
      category: guide.category,
      region: guide.region ?? '',
      summary: guide.summary,
      tagsText: guide.tags.join(', '),
      watermark: guide.watermark,
      cover: guide.cover ?? '',
      badge: Array.isArray(guide.badge) ? guide.badge.join(', ') : guide.badge ?? '',
      difficulty: guide.difficulty,
      views: guide.views,
      likes: guide.likes,
      readMinutes: guide.readMinutes,
      updatedAt: guide.updatedAt.slice(0, 10),
      featured: !!guide.featured,
      themeAccent: guide.theme?.accent ?? '',
      content: guide.content,
    })
  } catch (err) {
    view.value = 'list'
    listError.value = err instanceof Error ? err.message : '读取攻略详情失败'
  } finally {
    formLoading.value = false
  }
}

async function removeGuide(guide: Guide) {
  listError.value = ''
  if (!window.confirm(`确定删除「${guide.title}」吗？此操作不可恢复。`)) return
  try {
    await api(`/admin/guides/${guide.slug}`, { method: 'DELETE' })
    invalidate()
  } catch (err) {
    listError.value = err instanceof Error ? err.message : '删除失败'
  }
}

async function submit() {
  error.value = ''
  if (!form.slug.trim() || !form.title.trim()) {
    error.value = 'Slug 和标题必填'
    return
  }

  const payload: Record<string, unknown> = {
    slug: form.slug.trim(),
    title: form.title.trim(),
    subtitle: form.subtitle.trim(),
    category: form.category,
    summary: form.summary.trim(),
    content: form.content,
    tags: form.tagsText.split(/[,，]/).map((tag) => tag.trim()).filter(Boolean),
    watermark: form.watermark.trim(),
    difficulty: Number(form.difficulty) || 0,
    views: Number(form.views) || 0,
    likes: Number(form.likes) || 0,
    readMinutes: Number(form.readMinutes) || 0,
    updatedAt: form.updatedAt,
    featured: form.featured,
  }
  if (form.region.trim()) payload.region = form.region.trim()
  if (form.cover.trim()) payload.cover = form.cover.trim()
  if (form.badge.trim()) payload.badge = form.badge.trim()
  if (form.themeAccent.trim()) payload.theme = { accent: form.themeAccent.trim() }

  submitting.value = true
  try {
    if (editingSlug.value) {
      await api(`/admin/guides/${editingSlug.value}`, { method: 'PUT', body: JSON.stringify(payload) })
    } else {
      await api('/admin/guides', { method: 'POST', body: JSON.stringify(payload) })
    }
    view.value = 'list'
    invalidate()
  } catch (err) {
    error.value = err instanceof Error ? err.message : '保存失败，请稍后再试'
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (!isLoggedIn.value) {
    router.replace({ path: '/auth', query: { redirect: '/admin' } })
    return
  }
  loadGuides()
})
</script>

<template>
  <div class="admin">
    <section class="page-head container">
      <p class="eyebrow reveal">ADMIN · 档案管理</p>
      <div class="page-head__row reveal reveal-delay-1">
        <div>
          <h1 class="page-title">攻略管理</h1>
          <p class="page-desc">新建、编辑与删除攻略档案。</p>
        </div>
        <button v-if="view === 'list'" type="button" class="btn btn--solid" @click="startCreate">
          新建攻略
        </button>
        <button v-else type="button" class="btn" @click="view = 'list'">返回列表</button>
      </div>
    </section>

    <template v-if="view === 'list'">
      <section v-if="loading" class="container empty">
        <p class="empty__text">正在读取档案…</p>
      </section>

      <section v-else-if="failed" class="container empty">
        <p class="empty__char">!</p>
        <p class="empty__text">档案库读取失败，确认后端已启动后重试。</p>
        <button class="btn" type="button" @click="loadGuides(true)">重试</button>
      </section>

      <section v-else class="container">
        <p v-if="listError" class="admin-error" role="alert">{{ listError }}</p>
        <div v-if="sortedGuides.length" class="admin-table-wrap">
          <table class="admin-table">
            <thead>
              <tr>
                <th>标题</th>
                <th>Slug</th>
                <th>分类</th>
                <th>更新时间</th>
                <th class="admin-table__actions">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="guide in sortedGuides" :key="guide.id">
                <td class="admin-table__title">{{ guide.title }}</td>
                <td class="admin-table__slug">{{ guide.slug }}</td>
                <td>{{ getCategory(guide.category).name }}</td>
                <td>{{ formatDate(guide.updatedAt) }}</td>
                <td class="admin-table__actions">
                  <button type="button" class="row-btn" @click="startEdit(guide.slug)">编辑</button>
                  <button type="button" class="row-btn row-btn--danger" @click="removeGuide(guide)">
                    删除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="empty">
          <p class="empty__char">空</p>
          <p class="empty__text">还没有攻略，点右上角新建一篇吧。</p>
        </div>
      </section>
    </template>

    <section v-else class="container admin-form-section">
      <p v-if="formLoading" class="empty__text admin-form-loading">正在读取攻略详情…</p>

      <form v-else class="admin-form" @submit.prevent="submit">
        <div class="admin-form__grid">
          <label class="field">
            <span class="field-label">Slug{{ editingSlug ? '（不可修改）' : '' }}</span>
            <input
              v-model="form.slug"
              type="text"
              :disabled="!!editingSlug"
              placeholder="例如 radiance"
              required
            />
          </label>
          <label class="field">
            <span class="field-label">标题</span>
            <input v-model="form.title" type="text" placeholder="攻略标题" required />
          </label>
          <label class="field">
            <span class="field-label">副标题</span>
            <input v-model="form.subtitle" type="text" placeholder="一句话定位" />
          </label>
          <label class="field">
            <span class="field-label">分类</span>
            <select v-model="form.category">
              <option v-for="cat in CATEGORIES" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </label>
          <label class="field">
            <span class="field-label">区域（选填）</span>
            <input v-model="form.region" type="text" placeholder="例如 遗忘十字路" />
          </label>
          <label class="field">
            <span class="field-label">水印字符</span>
            <input v-model="form.watermark" type="text" placeholder="卡片上的大字符" />
          </label>
          <label class="field">
            <span class="field-label">封面（选填）</span>
            <input v-model="form.cover" type="text" placeholder="例如 /covers/xxx.webp" />
          </label>
          <label class="field">
            <span class="field-label">徽章（选填）</span>
            <input v-model="form.badge" type="text" placeholder="例如 /badges/grimm.png" />
          </label>
          <label class="field">
            <span class="field-label">难度（0-5）</span>
            <input v-model.number="form.difficulty" type="number" min="0" max="5" />
          </label>
          <label class="field">
            <span class="field-label">阅读数</span>
            <input v-model.number="form.views" type="number" min="0" />
          </label>
          <label class="field">
            <span class="field-label">收藏数</span>
            <input v-model.number="form.likes" type="number" min="0" />
          </label>
          <label class="field">
            <span class="field-label">阅读时长（分钟）</span>
            <input v-model.number="form.readMinutes" type="number" min="0" />
          </label>
          <label class="field">
            <span class="field-label">更新日期</span>
            <input v-model="form.updatedAt" type="date" />
          </label>
          <label class="field">
            <span class="field-label">主题色（选填）</span>
            <input v-model="form.themeAccent" type="text" placeholder="例如 #a9e6da" />
          </label>
          <label class="field field--checkbox">
            <input v-model="form.featured" type="checkbox" />
            <span class="field-label">推荐（featured）</span>
          </label>
        </div>

        <label class="field">
          <span class="field-label">标签（逗号分隔）</span>
          <input v-model="form.tagsText" type="text" placeholder="例如 辐光, 结局, 神居" />
        </label>

        <label class="field">
          <span class="field-label">摘要</span>
          <textarea v-model="form.summary" rows="3" placeholder="列表卡片上显示的一句话简介"></textarea>
        </label>

        <label class="field">
          <span class="field-label">正文（Markdown）</span>
          <textarea v-model="form.content" rows="16" placeholder="## 招式拆解&#10;&#10;……"></textarea>
        </label>

        <p v-if="error" class="admin-error" role="alert">{{ error }}</p>

        <div class="admin-form__actions">
          <button class="btn btn--solid" type="submit" :disabled="submitting">
            {{ submitting ? '保存中…' : editingSlug ? '保存修改' : '创建攻略' }}
          </button>
          <button class="btn" type="button" @click="view = 'list'">取消</button>
        </div>
      </form>
    </section>
  </div>
</template>

<style scoped>
.admin {
  padding-bottom: 96px;
}

.page-head {
  padding-top: 72px;
  padding-bottom: 40px;
}

.page-head__row {
  margin-top: 26px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 32px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--line);
}

.page-title {
  font-size: clamp(36px, 4.6vw, 52px);
  letter-spacing: 0.12em;
  color: var(--bone);
}

.page-desc {
  margin-top: 12px;
  font-size: 13.5px;
  color: var(--muted);
}

.admin-error {
  margin-bottom: 20px;
  font-size: 12.5px;
  color: #d98f8f;
  letter-spacing: 0.05em;
}

.admin-table-wrap {
  overflow-x: auto;
  border: 1px solid var(--line);
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  color: var(--text);
}

.admin-table th,
.admin-table td {
  padding: 13px 16px;
  text-align: left;
  border-bottom: 1px solid var(--line);
  white-space: nowrap;
}

.admin-table th {
  font-size: 11px;
  letter-spacing: 0.18em;
  color: var(--dim);
  font-weight: 500;
}

.admin-table tbody tr:last-child td {
  border-bottom: 0;
}

.admin-table tbody tr {
  transition: background 0.25s;
}

.admin-table tbody tr:hover {
  background: rgba(233, 240, 236, 0.02);
}

.admin-table__title {
  color: var(--bone);
  letter-spacing: 0.04em;
}

.admin-table__slug {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--muted);
}

.admin-table__actions {
  text-align: right;
}

.row-btn {
  border: 0;
  background: transparent;
  padding: 4px 10px;
  font-size: 12px;
  letter-spacing: 0.12em;
  color: var(--soul);
  cursor: pointer;
  transition: color 0.25s;
}

.row-btn:hover {
  color: var(--bone);
}

.row-btn--danger {
  color: #d98f8f;
}

.row-btn--danger:hover {
  color: #eab0b0;
}

.admin-form-section {
  margin-top: 8px;
}

.admin-form-loading {
  padding-block: 60px;
  text-align: center;
  font-size: 13px;
  letter-spacing: 0.1em;
  color: var(--muted);
}

.admin-form {
  display: flex;
  flex-direction: column;
  gap: 22px;
  padding: 28px 28px 24px;
  border: 1px solid var(--line);
  background: linear-gradient(160deg, rgba(233, 240, 236, 0.03), rgba(233, 240, 236, 0.008));
}

.admin-form__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px 28px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 11px;
  letter-spacing: 0.2em;
  color: var(--dim);
}

.field input,
.field select,
.field textarea {
  border: 0;
  border-bottom: 1px solid var(--line);
  border-radius: 0;
  background: transparent;
  padding: 8px 2px;
  color: var(--bone);
  font-family: var(--font-body);
  font-size: 14px;
  outline: none;
  resize: vertical;
  transition: border-color 0.3s;
}

.field select {
  cursor: pointer;
}

.field select option {
  background: #0b0f10;
  color: var(--bone);
}

.field input:focus,
.field select:focus,
.field textarea:focus {
  border-color: rgba(169, 230, 218, 0.6);
}

.field input::placeholder,
.field textarea::placeholder {
  color: var(--dim);
}

.field input:disabled {
  color: var(--dim);
  cursor: not-allowed;
}

.field input[type='date'] {
  color-scheme: dark;
}

.field--checkbox {
  flex-direction: row;
  align-items: center;
  gap: 10px;
  align-self: end;
  padding-bottom: 8px;
}

.field--checkbox input {
  width: 15px;
  height: 15px;
  accent-color: var(--soul);
  cursor: pointer;
}

.admin-form__actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.admin-form__actions .btn:disabled {
  opacity: 0.55;
  cursor: wait;
}

.empty {
  padding-block: 80px;
  text-align: center;
}

.empty__char {
  font-family: var(--font-display);
  font-size: 96px;
  line-height: 1;
  color: rgba(233, 240, 236, 0.08);
}

.empty__text {
  margin-top: 16px;
  font-size: 13px;
  letter-spacing: 0.1em;
  color: var(--muted);
}

.empty .btn {
  margin-top: 28px;
}

@media (max-width: 720px) {
  .page-head__row {
    flex-direction: column;
    align-items: flex-start;
  }
  .admin-form__grid {
    grid-template-columns: 1fr;
  }
}
</style>
