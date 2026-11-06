<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CategoryGlyph from '../components/CategoryGlyph.vue'
import { getCategory } from '../data/categories'
import { api } from '../api'
import { formatDate } from '../utils/format'
import type { Guide } from '../types/guide'

const route = useRoute()
const router = useRouter()

const keyword = ref(String(route.query.q ?? ''))
const submitted = computed(() => String(route.query.q ?? '').trim())

const suggestions = ['螳螂领主', '护符', '面具碎片', '神居', '钢魂', '结局', '苦痛之路']

const results = ref<Guide[]>([])
const searching = ref(false)
const failed = ref(false)

/** 后端全文检索：标题/副标题/摘要/标签/正文 */
async function runSearch() {
  const query = submitted.value
  if (!query) {
    results.value = []
    return
  }
  searching.value = true
  failed.value = false
  try {
    results.value = await api<Guide[]>(`/guides/search?q=${encodeURIComponent(query)}`)
  } catch {
    results.value = []
    failed.value = true
  } finally {
    searching.value = false
  }
}

watch(
  () => route.query.q,
  (value) => {
    keyword.value = String(value ?? '')
  },
)

watch(submitted, runSearch, { immediate: true })

function search(term?: string) {
  const value = (term ?? keyword.value).trim()
  if (term) keyword.value = term
  router.push(value ? { path: '/search', query: { q: value } } : { path: '/search' })
}
</script>

<template>
  <div class="search-page">
    <section class="container page-head">
      <p class="eyebrow reveal">SEARCH · 档案检索</p>
      <h1 class="page-title reveal reveal-delay-1">搜索</h1>
      <p class="page-desc reveal reveal-delay-2">
        输入 BOSS 名、区域、护符或关键词。整份档案都会检索，包括正文内容。
      </p>

      <form class="search-form reveal reveal-delay-3" @submit.prevent="search()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" aria-hidden="true">
          <circle cx="10.5" cy="10.5" r="6.5" />
          <path d="M15.5 15.5 21 21" />
        </svg>
        <input
          v-model="keyword"
          type="search"
          placeholder="试试「螳螂领主」或「神居」…"
          aria-label="搜索关键词"
        />
        <button type="submit" class="btn btn--solid">检索</button>
      </form>

      <div class="suggestions reveal reveal-delay-4">
        <span class="suggestions__label">常用</span>
        <button
          v-for="item in suggestions"
          :key="item"
          class="suggestion"
          type="button"
          @click="search(item)"
        >
          {{ item }}
        </button>
      </div>
    </section>

    <section class="container results">
      <p v-if="submitted" class="results__count">
        找到 <span>{{ results.length }}</span> 条与「{{ submitted }}」相关的档案
      </p>

      <div v-if="searching" class="idle">
        <p class="idle__hint">正在检索…</p>
      </div>

      <div v-else-if="failed" class="empty">
        <span class="empty__char">!</span>
        <p class="empty__text">检索失败，确认后端已启动后重试。</p>
        <button class="btn" type="button" @click="runSearch">重试</button>
      </div>

      <div v-else-if="results.length" class="result-list">
        <RouterLink
          v-for="guide in results"
          :key="guide.id"
          :to="`/guides/${guide.slug}`"
          class="result-row"
        >
          <span class="result-glyph">
            <CategoryGlyph :category="guide.category" :size="26" />
          </span>
          <span class="result-main">
            <span class="result-head">
              <span class="result-title">{{ guide.title }}</span>
              <span class="result-cat">{{ getCategory(guide.category).name }}</span>
            </span>
            <span class="result-summary">{{ guide.summary }}</span>
          </span>
          <span class="result-date">{{ formatDate(guide.updatedAt) }}</span>
          <span class="result-arrow">→</span>
        </RouterLink>
      </div>

      <div v-else-if="submitted" class="empty">
        <span class="empty__char">空</span>
        <p class="empty__text">没有找到相关档案。换个关键词，或者从分类开始逛。</p>
        <RouterLink to="/guides" class="btn">浏览全部攻略 →</RouterLink>
      </div>

      <div v-else class="idle">
        <p class="idle__quote">“知识是唯一带得走的东西。”</p>
        <p class="idle__hint">还没有输入关键词。可以从上面的常用词开始。</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
.search-page {
  padding-bottom: 100px;
}

.page-head {
  padding-top: 72px;
}

.page-title {
  margin-top: 26px;
  font-size: clamp(40px, 5vw, 58px);
  letter-spacing: 0.16em;
  color: var(--bone);
}

.page-desc {
  margin-top: 16px;
  max-width: 520px;
  font-size: 13.5px;
  line-height: 1.9;
  color: var(--muted);
}

.search-form {
  margin-top: 40px;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 10px 10px 22px;
  border: 1px solid var(--line-strong);
  background: rgba(233, 240, 236, 0.025);
  color: var(--dim);
  transition: border-color 0.35s, background 0.35s;
}

.search-form:focus-within {
  border-color: rgba(169, 230, 218, 0.55);
  background: rgba(169, 230, 218, 0.04);
  color: var(--soul);
}

.search-form input {
  flex: 1;
  min-width: 0;
  border: 0;
  background: transparent;
  color: var(--bone);
  font-family: var(--font-body);
  font-size: 16px;
  letter-spacing: 0.06em;
  outline: none;
  padding-block: 10px;
}

.search-form input::placeholder {
  color: var(--dim);
}

.suggestions {
  margin-top: 22px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.suggestions__label {
  font-size: 10px;
  letter-spacing: 0.2em;
  color: var(--dim);
  margin-right: 6px;
}

.suggestion {
  padding: 5px 12px;
  border: 1px solid var(--line);
  background: none;
  font-size: 11.5px;
  letter-spacing: 0.08em;
  color: var(--muted);
  transition: color 0.3s, border-color 0.3s;
}

.suggestion:hover {
  color: var(--soul);
  border-color: rgba(169, 230, 218, 0.4);
}

.results {
  margin-top: 56px;
}

.results__count {
  font-size: 12px;
  letter-spacing: 0.1em;
  color: var(--dim);
  padding-bottom: 18px;
  border-bottom: 1px solid var(--line);
}

.results__count span {
  font-family: var(--font-mono);
  color: var(--soul);
}

.result-list {
  display: flex;
  flex-direction: column;
}

.result-row {
  position: relative;
  display: grid;
  grid-template-columns: 40px minmax(0, 1fr) 94px 20px;
  align-items: center;
  gap: 20px;
  padding: 22px 12px 22px 0;
  border-bottom: 1px solid var(--line);
  transition: padding-left 0.4s, background 0.4s;
}

.result-row::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 2px;
  background: var(--soul);
  transform: scaleY(0);
  transition: transform 0.4s;
}

.result-row:hover {
  padding-left: 18px;
  background: rgba(233, 240, 236, 0.025);
}

.result-row:hover::before {
  transform: scaleY(1);
}

.result-glyph {
  color: rgba(169, 230, 218, 0.5);
  transition: color 0.35s;
}

.result-row:hover .result-glyph {
  color: var(--soul);
}

.result-main {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 0;
}

.result-head {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.result-title {
  font-family: var(--font-display);
  font-size: 20px;
  letter-spacing: 0.08em;
  color: var(--bone);
  transition: color 0.35s;
}

.result-row:hover .result-title {
  color: var(--soul);
}

.result-cat {
  font-size: 10.5px;
  letter-spacing: 0.12em;
  color: var(--dim);
}

.result-summary {
  font-size: 12px;
  line-height: 1.7;
  color: var(--muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-date {
  font-size: 11px;
  letter-spacing: 0.08em;
  color: var(--dim);
}

.result-arrow {
  color: var(--dim);
  transition: color 0.35s, transform 0.35s;
}

.result-row:hover .result-arrow {
  color: var(--soul);
  transform: translateX(4px);
}

.empty {
  padding-block: 70px;
  text-align: center;
}

.empty__char {
  font-family: var(--font-display);
  font-size: 90px;
  line-height: 1;
  color: rgba(233, 240, 236, 0.07);
}

.empty__text {
  margin-top: 18px;
  font-size: 13px;
  letter-spacing: 0.08em;
  color: var(--muted);
}

.empty .btn {
  margin-top: 28px;
}

.idle {
  padding-block: 70px 20px;
  text-align: center;
}

.idle__quote {
  font-family: var(--font-display);
  font-size: clamp(22px, 3vw, 30px);
  letter-spacing: 0.12em;
  color: var(--bone);
}

.idle__hint {
  margin-top: 14px;
  font-size: 12px;
  letter-spacing: 0.1em;
  color: var(--dim);
}

@media (max-width: 720px) {
  .result-row {
    grid-template-columns: 30px minmax(0, 1fr) 18px;
    gap: 12px;
  }
  .result-date {
    display: none;
  }
  .search-form {
    padding: 8px 8px 8px 16px;
  }
  .search-form input {
    font-size: 14px;
  }
  .search-form .btn {
    padding: 12px 16px;
    letter-spacing: 0.1em;
  }
}
</style>
