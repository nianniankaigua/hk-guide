<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import GuideCard from '../components/GuideCard.vue'
import CategoryGlyph from '../components/CategoryGlyph.vue'
import { findGuide, GUIDES } from '../data/guides'
import { getCategory } from '../data/categories'
import { backdropFor } from '../data/backdrops'
import { clearBackdropPreview, setBaseBackdrop } from '../composables/useBackdrop'
import { renderGuide } from '../utils/markdown'
import { formatCount, formatDate } from '../utils/format'

const route = useRoute()

const guide = computed(() => findGuide(String(route.params.slug)))
const category = computed(() => (guide.value ? getCategory(guide.value.category) : null))
const rendered = computed(() =>
  guide.value ? renderGuide(guide.value.content) : { html: '', toc: [] },
)

const related = computed(() => {
  const current = guide.value
  if (!current) return []
  const sameCategory = GUIDES.filter((item) => item.category === current.category && item.id !== current.id)
  const others = GUIDES.filter((item) => item.category !== current.category && item.id !== current.id)
  return [...sameCategory, ...others].slice(0, 3)
})

const themeStyle = computed(() =>
  guide.value?.theme ? { '--soul': guide.value.theme.accent } : {},
)

const backdrop = computed(() => {
  const current = guide.value
  if (!current) return undefined
  return current.backdrop ?? backdropFor(current.slug)
})

watch(
  [backdrop, () => guide.value?.theme?.accent],
  ([image, accent]) => setBaseBackdrop(image, accent),
  { immediate: true },
)

const index = computed(() => GUIDES.findIndex((item) => item.id === guide.value?.id))
const prevGuide = computed(() => (index.value > 0 ? GUIDES[index.value - 1] : null))
const nextGuide = computed(() => (index.value >= 0 && index.value < GUIDES.length - 1 ? GUIDES[index.value + 1] : null))

const progress = ref(0)
const activeHeading = ref('')

function updateScrollState() {
  const scrollable = document.documentElement.scrollHeight - window.innerHeight
  progress.value = scrollable > 0 ? Math.min(1, Math.max(0, window.scrollY / scrollable)) : 0

  const headings = Array.from(document.querySelectorAll<HTMLElement>('.prose h2, .prose h3'))
  let current = ''
  for (const heading of headings) {
    if (heading.getBoundingClientRect().top < 160) current = heading.id
  }
  activeHeading.value = current
}

function attachScroll() {
  updateScrollState()
  window.addEventListener('scroll', updateScrollState, { passive: true })
}

onMounted(attachScroll)
onBeforeUnmount(() => {
  window.removeEventListener('scroll', updateScrollState)
  clearBackdropPreview()
  setBaseBackdrop(undefined)
})

watch(guide, async () => {
  await nextTick()
  updateScrollState()
})
</script>

<template>
  <div v-if="guide" class="detail" :style="themeStyle">
    <div class="read-progress" :style="{ transform: `scaleX(${progress})` }"></div>

    <section class="container detail-head">
      <nav class="breadcrumb reveal" aria-label="面包屑">
        <RouterLink to="/guides">攻略档案</RouterLink>
        <span class="sep">/</span>
        <RouterLink :to="{ path: '/guides', query: { category: guide.category } }">
          {{ category?.name }}
        </RouterLink>
        <span class="sep">/</span>
        <span class="current">{{ guide.title }}</span>
      </nav>

      <div class="head-grid reveal reveal-delay-1">
        <div class="head-copy">
          <div class="head-label">
            <span class="head-glyph">
              <CategoryGlyph :category="guide.category" :size="15" />
            </span>
            <span>{{ category?.en }}</span>
            <span class="head-label__sep"></span>
            <span>{{ category?.name }}</span>
          </div>

          <h1 class="detail-title">{{ guide.title }}</h1>
          <p class="detail-sub">{{ guide.subtitle }}</p>

          <div class="detail-tags">
            <span v-for="tag in guide.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>

        <div v-if="guide.cover" class="head-art">
          <span class="head-art__glow" aria-hidden="true"></span>
          <img :src="guide.cover" :alt="`${guide.title} 立绘`" />
        </div>
        <span v-else class="head-watermark" aria-hidden="true">{{ guide.watermark }}</span>
      </div>

      <div class="detail-meta reveal reveal-delay-2">
        <span>更新于 {{ formatDate(guide.updatedAt) }}</span>
        <span class="meta-dot"></span>
        <span>{{ guide.readMinutes }} 分钟阅读</span>
        <span class="meta-dot"></span>
        <span>{{ formatCount(guide.views) }} 次阅读</span>
        <span class="meta-dot"></span>
        <span>{{ formatCount(guide.likes) }} 人收藏</span>

        <span v-if="guide.difficulty" class="meta-difficulty">
          难度
          <i v-for="n in 5" :key="n" :class="{ filled: n <= guide.difficulty }"></i>
        </span>
      </div>
    </section>

    <section class="container detail-body">
      <article class="prose article" v-html="rendered.html"></article>

      <aside v-if="rendered.toc.length" class="toc">
        <p class="toc__title">目录 / CONTENTS</p>
        <ul class="toc__list">
          <li
            v-for="item in rendered.toc"
            :key="item.id"
            class="toc__item"
            :class="[`toc__item--h${item.level}`, { active: activeHeading === item.id }]"
          >
            <a :href="`#${item.id}`">{{ item.text }}</a>
          </li>
        </ul>
        <RouterLink to="/guides" class="toc__back">← 返回档案库</RouterLink>
      </aside>
    </section>

    <section class="container related">
      <div class="section-head">
        <div class="section-head__title">
          <span class="section-head__index">REL /</span>
          <div>
            <h2 class="section-head__cn">相关档案</h2>
            <span class="section-head__en">Related Files</span>
          </div>
        </div>
      </div>

      <div class="related-grid">
        <GuideCard v-for="item in related" :key="item.id" :guide="item" />
      </div>
    </section>

    <section class="container pager">
      <RouterLink
        v-if="prevGuide"
        :to="`/guides/${prevGuide.slug}`"
        class="pager-link pager-link--prev"
      >
        <span class="pager-label">← 上一篇</span>
        <span class="pager-title">{{ prevGuide.title }}</span>
      </RouterLink>
      <RouterLink
        v-if="nextGuide"
        :to="`/guides/${nextGuide.slug}`"
        class="pager-link pager-link--next"
      >
        <span class="pager-label">下一篇 →</span>
        <span class="pager-title">{{ nextGuide.title }}</span>
      </RouterLink>
    </section>
  </div>

  <div v-else class="notfound container">
    <span class="notfound__char">失</span>
    <p class="notfound__text">这份档案不存在，或者已经在圣巢的风里散尽了。</p>
    <RouterLink to="/guides" class="btn btn--solid">返回攻略档案 →</RouterLink>
  </div>
</template>

<style scoped>
.read-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, color-mix(in srgb, var(--soul) 45%, transparent), var(--soul));
  transform-origin: left;
  transform: scaleX(0);
  z-index: 90;
  box-shadow: 0 0 12px color-mix(in srgb, var(--soul) 55%, transparent);
}

.detail-head {
  padding-top: 58px;
}

.breadcrumb {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  font-size: 11px;
  letter-spacing: 0.12em;
  color: var(--dim);
}

.breadcrumb a {
  transition: color 0.3s;
}

.breadcrumb a:hover {
  color: var(--soul);
}

.sep {
  color: var(--line-strong);
}

.current {
  color: var(--muted);
}

.head-grid {
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 32px;
  margin-top: 34px;
}

.head-copy {
  min-width: 0;
}

.head-label {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 10px;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  color: var(--dim);
}

.head-glyph {
  color: var(--soul);
  opacity: 0.85;
}

.head-label__sep {
  width: 20px;
  height: 1px;
  background: var(--line-strong);
}

.detail-title {
  margin-top: 18px;
  font-size: clamp(46px, 6.4vw, 76px);
  line-height: 1.08;
  letter-spacing: 0.12em;
  color: var(--bone);
  text-shadow: 0 0 70px color-mix(in srgb, var(--soul) 14%, transparent);
}

.detail-sub {
  margin-top: 12px;
  font-size: 13px;
  letter-spacing: 0.14em;
  color: var(--muted);
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 22px;
}

.head-watermark {
  font-family: var(--font-display);
  font-size: clamp(100px, 15vw, 190px);
  line-height: 0.8;
  color: rgba(233, 240, 236, 0.045);
  user-select: none;
  flex-shrink: 0;
}

.head-art {
  position: relative;
  flex-shrink: 0;
  width: min(320px, 34%);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 6px;
}

.head-art__glow {
  position: absolute;
  width: 260px;
  height: 260px;
  left: 50%;
  bottom: -30px;
  transform: translateX(-50%);
  background: radial-gradient(circle, color-mix(in srgb, var(--soul) 16%, transparent), transparent 66%);
  filter: blur(12px);
  pointer-events: none;
}

.head-art img {
  position: relative;
  z-index: 1;
  max-height: 330px;
  max-width: 100%;
  object-fit: contain;
  filter: drop-shadow(0 18px 36px rgba(0, 0, 0, 0.7));
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-top: 34px;
  padding-block: 18px;
  border-top: 1px solid var(--line);
  border-bottom: 1px solid var(--line);
  font-size: 11.5px;
  letter-spacing: 0.08em;
  color: var(--muted);
}

.meta-dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--dim);
  opacity: 0.6;
}

.meta-difficulty {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;
  color: var(--dim);
}

.meta-difficulty i {
  width: 16px;
  height: 2px;
  background: rgba(233, 240, 236, 0.16);
}

.meta-difficulty i.filled {
  background: rgba(200, 180, 138, 0.85);
}

.detail-body {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 226px;
  gap: 76px;
  padding-top: 56px;
}

.article {
  max-width: 780px;
}

.detail :deep(.prose h2) {
  border-left-color: color-mix(in srgb, var(--soul) 55%, transparent);
}

.detail :deep(.prose ul li::before) {
  border-color: color-mix(in srgb, var(--soul) 60%, transparent);
}

.detail :deep(.prose ol li::before),
.detail :deep(.prose code),
.detail :deep(.prose a) {
  color: var(--soul);
}

.detail :deep(.prose a) {
  border-bottom-color: color-mix(in srgb, var(--soul) 35%, transparent);
}

.toc {
  position: sticky;
  top: calc(var(--header-h) + 36px);
  align-self: start;
  padding-top: 8px;
}

.toc__title {
  font-size: 10px;
  letter-spacing: 0.24em;
  color: var(--dim);
  padding-bottom: 14px;
}

.toc__list {
  list-style: none;
  margin: 0;
  padding: 0;
  border-left: 1px solid var(--line);
}

.toc__item a {
  display: block;
  padding: 8px 0 8px 16px;
  margin-left: -1px;
  border-left: 1px solid transparent;
  font-size: 12px;
  line-height: 1.6;
  letter-spacing: 0.04em;
  color: var(--muted);
  transition: color 0.3s, border-color 0.3s, padding-left 0.3s;
}

.toc__item--h3 a {
  padding-left: 28px;
  font-size: 11.5px;
  color: var(--dim);
}

.toc__item a:hover {
  color: var(--bone);
}

.toc__item.active a {
  color: var(--soul);
  border-left-color: var(--soul);
  padding-left: 20px;
}

.toc__item--h3.active a {
  padding-left: 32px;
}

.toc__back {
  display: inline-block;
  margin-top: 26px;
  font-size: 11px;
  letter-spacing: 0.1em;
  color: var(--dim);
  transition: color 0.3s;
}

.toc__back:hover {
  color: var(--soul);
}

.related {
  margin-top: 76px;
  padding-top: 44px;
  border-top: 1px solid var(--line);
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.pager {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-top: 64px;
}

.pager-link {
  flex: 1;
  max-width: 48%;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 24px 26px;
  border: 1px solid var(--line);
  transition: border-color 0.35s, background 0.35s;
}

.pager-link--next {
  text-align: right;
  margin-left: auto;
}

.pager-link:hover {
  border-color: color-mix(in srgb, var(--soul) 42%, transparent);
  background: rgba(233, 240, 236, 0.025);
}

.pager-label {
  font-size: 10px;
  letter-spacing: 0.2em;
  color: var(--dim);
}

.pager-title {
  font-family: var(--font-display);
  font-size: 18px;
  letter-spacing: 0.08em;
  color: var(--bone);
}

.notfound {
  padding-block: 130px;
  text-align: center;
}

.notfound__char {
  font-family: var(--font-display);
  font-size: 110px;
  line-height: 1;
  color: rgba(233, 240, 236, 0.07);
}

.notfound__text {
  margin-top: 22px;
  font-size: 13px;
  letter-spacing: 0.08em;
  color: var(--muted);
}

.notfound .btn {
  margin-top: 30px;
}

@media (max-width: 1000px) {
  .detail-body {
    grid-template-columns: 1fr;
    gap: 0;
  }
  .toc {
    display: none;
  }
  .related-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .detail-head {
    padding-top: 44px;
  }
  .head-grid {
    flex-direction: column;
    align-items: flex-start;
  }
  .head-watermark {
    display: none;
  }
  .head-art {
    display: none;
  }
  .detail-meta {
    gap: 10px;
    font-size: 10.5px;
  }
  .meta-difficulty {
    margin-left: 0;
    width: 100%;
  }
  .related-grid {
    grid-template-columns: 1fr;
  }
  .pager {
    flex-direction: column;
  }
  .pager-link {
    max-width: 100%;
  }
}
</style>
