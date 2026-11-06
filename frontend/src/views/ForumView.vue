<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'
import { useAuth } from '../stores/auth'

interface PostItem {
  id: number
  title: string
  excerpt: string
  author: string
  createdAt: string
  commentCount: number
}

const router = useRouter()
const { isLoggedIn } = useAuth()

const posts = ref<PostItem[]>([])
const loading = ref(true)
const failed = ref(false)

const composing = ref(false)
const title = ref('')
const content = ref('')
const error = ref('')
const submitting = ref(false)

async function load() {
  loading.value = true
  failed.value = false
  try {
    const data = await api<{ posts: PostItem[] }>('/forum/posts')
    posts.value = data.posts
  } catch {
    failed.value = true
  } finally {
    loading.value = false
  }
}

function startCompose() {
  if (!isLoggedIn.value) {
    router.push({ path: '/auth', query: { redirect: '/forum' } })
    return
  }
  composing.value = true
  error.value = ''
}

async function submitPost() {
  error.value = ''
  if (!title.value.trim() || !content.value.trim()) {
    error.value = '标题和内容都要写哦'
    return
  }
  submitting.value = true
  try {
    const post = await api<{ id: number }>('/forum/posts', {
      method: 'POST',
      body: JSON.stringify({ title: title.value.trim(), content: content.value.trim() }),
    })
    composing.value = false
    title.value = ''
    content.value = ''
    router.push(`/forum/${post.id}`)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '发布失败，请稍后再试'
  } finally {
    submitting.value = false
  }
}

function formatTime(iso: string) {
  if (!iso) return ''
  return iso.replace('T', ' ').slice(0, 16)
}

onMounted(load)
</script>

<template>
  <div class="forum">
    <section class="page-head container">
      <p class="eyebrow reveal">FORUM · 旅者篝火</p>
      <div class="page-head__row reveal reveal-delay-1">
        <div>
          <h1 class="page-title">论坛</h1>
          <p class="page-desc">卡在哪个 BOSS、找到什么彩蛋、想聊的都可以写在这里。</p>
        </div>
        <button type="button" class="btn btn--solid" @click="startCompose">发 帖</button>
      </div>
    </section>

    <section v-if="composing" class="container compose reveal">
      <label class="field">
        <span class="field-label">标题</span>
        <input v-model="title" type="text" maxlength="120" placeholder="一句话说清主题" />
      </label>
      <label class="field">
        <span class="field-label">内容</span>
        <textarea v-model="content" rows="6" placeholder="展开说说…"></textarea>
      </label>
      <p v-if="error" class="compose-error" role="alert">{{ error }}</p>
      <div class="compose-actions">
        <button type="button" class="btn btn--solid" :disabled="submitting" @click="submitPost">
          {{ submitting ? '发布中…' : '发 布' }}
        </button>
        <button type="button" class="btn btn--ghost" @click="composing = false">取消</button>
      </div>
    </section>

    <section v-if="loading" class="container empty">
      <p class="empty__text">正在读取帖子…</p>
    </section>

    <section v-else-if="failed" class="container empty">
      <p class="empty__char">!</p>
      <p class="empty__text">帖子列表读取失败，确认后端已启动后重试。</p>
      <button type="button" class="btn" @click="load">重试</button>
    </section>

    <section v-else-if="posts.length" class="container post-list">
      <RouterLink v-for="post in posts" :key="post.id" :to="`/forum/${post.id}`" class="post-item">
        <div class="post-main">
          <h2 class="post-title">{{ post.title }}</h2>
          <p class="post-excerpt">{{ post.excerpt }}</p>
        </div>
        <div class="post-meta">
          <span class="post-author">{{ post.author }}</span>
          <span class="post-time">{{ formatTime(post.createdAt) }}</span>
          <span class="post-count">{{ post.commentCount }} 条回复</span>
        </div>
      </RouterLink>
    </section>

    <section v-else class="container empty">
      <p class="empty__char">静</p>
      <p class="empty__text">还没有人说话，来发第一帖吧。</p>
    </section>
  </div>
</template>

<style scoped>
.forum {
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

.compose {
  margin-bottom: 40px;
  padding: 28px 28px 24px;
  border: 1px solid var(--line);
  background: linear-gradient(160deg, rgba(233, 240, 236, 0.03), rgba(233, 240, 236, 0.008));
  display: flex;
  flex-direction: column;
  gap: 20px;
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
.field textarea {
  border: 0;
  border-bottom: 1px solid var(--line);
  background: transparent;
  padding: 8px 2px;
  color: var(--bone);
  font-family: var(--font-body);
  font-size: 14px;
  outline: none;
  resize: vertical;
  transition: border-color 0.3s;
}

.field input:focus,
.field textarea:focus {
  border-color: rgba(169, 230, 218, 0.6);
}

.compose-error {
  font-size: 12.5px;
  color: #d98f8f;
}

.compose-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.post-list {
  display: flex;
  flex-direction: column;
}

.post-item {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 32px;
  padding: 22px 4px;
  border-bottom: 1px solid var(--line);
  transition: background 0.3s, padding 0.3s;
}

.post-item:hover {
  background: rgba(233, 240, 236, 0.02);
  padding-inline: 12px;
}

.post-title {
  font-size: 19px;
  letter-spacing: 0.06em;
  transition: color 0.3s;
}

.post-item:hover .post-title {
  color: var(--soul);
}

.post-excerpt {
  margin-top: 6px;
  font-size: 13px;
  color: var(--muted);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  flex-shrink: 0;
  display: flex;
  align-items: baseline;
  gap: 18px;
  font-size: 11.5px;
  color: var(--dim);
  white-space: nowrap;
}

.post-author {
  color: var(--muted);
}

.empty {
  padding-block: 72px;
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
  .post-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  .page-head__row {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
