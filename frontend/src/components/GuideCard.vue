<script setup lang="ts">
import { computed } from 'vue'
import CategoryGlyph from './CategoryGlyph.vue'
import { getCategory } from '../data/categories'
import { backdropFor } from '../data/backdrops'
import { clearBackdropPreview, previewBackdrop } from '../composables/useBackdrop'
import { formatDate, formatCount } from '../utils/format'
import type { Guide } from '../types/guide'

const props = defineProps<{ guide: Guide }>()

const category = computed(() => getCategory(props.guide.category))
const accent = computed(() => props.guide.theme?.accent ?? '#a9e6da')
const backdrop = computed(() => props.guide.backdrop ?? backdropFor(props.guide.slug))
const badgeList = computed(() => {
  const badge = props.guide.badge
  if (!badge) return []
  return Array.isArray(badge) ? badge : [badge]
})

function handleEnter() {
  if (backdrop.value) previewBackdrop(backdrop.value, accent.value)
}

function handleLeave() {
  clearBackdropPreview()
}
</script>

<template>
  <RouterLink
    :to="`/guides/${guide.slug}`"
    class="guide-card"
    :style="{ '--card-accent': accent }"
    @mouseenter="handleEnter"
    @mouseleave="handleLeave"
    @focus="handleEnter"
    @blur="handleLeave"
  >
    <div class="card-cover">
      <img
        v-if="backdrop"
        class="card-cover__backdrop"
        :src="backdrop"
        alt=""
        aria-hidden="true"
        loading="lazy"
      />
      <span v-if="!guide.cover" class="card-cover__char" aria-hidden="true">{{ guide.watermark }}</span>
      <span class="card-cover__glow"></span>

      <img
        v-if="guide.cover"
        class="card-cover__img"
        :src="guide.cover"
        :alt="`${guide.title} 立绘`"
        loading="lazy"
      />

      <span class="card-cover__cat">{{ category.name }}</span>
      <span class="card-cover__glyph">
        <template v-if="badgeList.length">
          <img
            v-for="badge in badgeList"
            :key="badge"
            class="card-cover__badge"
            :src="badge"
            alt=""
            aria-hidden="true"
          />
        </template>
        <img
          v-else-if="guide.category === 'boss'"
          class="card-cover__badge"
          src="/badges/knight.png"
          alt=""
          aria-hidden="true"
        />
        <CategoryGlyph v-else :category="guide.category" :size="34" />
      </span>

      <span v-if="guide.difficulty" class="card-cover__difficulty" :aria-label="`难度 ${guide.difficulty} / 5`">
        <i v-for="n in 5" :key="n" :class="{ filled: n <= guide.difficulty }"></i>
      </span>
    </div>

    <div class="card-body">
      <h3 class="card-title">{{ guide.title }}</h3>
      <p class="card-subtitle">{{ guide.subtitle }}</p>
      <p class="card-summary">{{ guide.summary }}</p>

      <div class="card-meta">
        <span>{{ formatDate(guide.updatedAt) }}</span>
        <span class="dot"></span>
        <span>{{ guide.readMinutes }} 分钟</span>
        <span class="dot"></span>
        <span>{{ formatCount(guide.views) }} 次阅读</span>
        <span class="card-arrow" aria-hidden="true">→</span>
      </div>
    </div>
  </RouterLink>
</template>

<style scoped>
.guide-card {
  position: relative;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--line);
  background: linear-gradient(180deg, rgba(233, 240, 236, 0.035), rgba(233, 240, 236, 0.012));
  transition: transform 0.45s cubic-bezier(0.22, 1, 0.36, 1), border-color 0.45s, box-shadow 0.45s;
  overflow: hidden;
}

.guide-card:hover {
  transform: translateY(-5px);
  border-color: color-mix(in srgb, var(--card-accent) 42%, transparent);
  box-shadow: 0 24px 60px -34px #000, 0 0 44px -22px color-mix(in srgb, var(--card-accent) 40%, transparent);
}

.card-cover {
  position: relative;
  height: 168px;
  border-bottom: 1px solid var(--line);
  background:
    radial-gradient(90% 120% at 78% -10%, rgba(169, 230, 218, 0.08), transparent 65%),
    linear-gradient(180deg, #101415, #0a0d0e);
  overflow: hidden;
}

.card-cover__backdrop {
  position: absolute;
  inset: -10%;
  object-fit: cover;
  object-position: center;
  filter: blur(5px) brightness(0.6) saturate(0.9);
  opacity: 0.5;
  transition: opacity 0.5s ease, transform 0.8s cubic-bezier(0.22, 1, 0.36, 1);
}

.guide-card:hover .card-cover__backdrop {
  opacity: 0.82;
  transform: scale(1.05);
}

.card-cover__char {
  position: absolute;
  right: 6px;
  bottom: -44px;
  font-family: var(--font-display);
  font-size: 148px;
  line-height: 1;
  color: rgba(233, 240, 236, 0.055);
  transition: color 0.45s, transform 0.45s;
  user-select: none;
}

.guide-card:hover .card-cover__char {
  color: rgba(233, 240, 236, 0.13);
  transform: translateY(-4px);
}

.card-cover__glow {
  position: absolute;
  width: 180px;
  height: 180px;
  left: 50%;
  top: -60px;
  border-radius: 50%;
  background: radial-gradient(circle, color-mix(in srgb, var(--card-accent) 18%, transparent), transparent 68%);
  filter: blur(8px);
  opacity: 0.55;
  transform: translateX(-50%);
  transition: opacity 0.45s;
}

.guide-card:hover .card-cover__glow {
  opacity: 1;
}

.card-cover__img {
  position: absolute;
  right: 5%;
  bottom: -4px;
  max-height: 158px;
  max-width: 62%;
  object-fit: contain;
  filter: drop-shadow(0 10px 22px rgba(0, 0, 0, 0.65));
  transition: transform 0.5s cubic-bezier(0.22, 1, 0.36, 1), filter 0.5s;
}

.guide-card:hover .card-cover__img {
  transform: translateY(-4px) scale(1.03);
  filter: drop-shadow(0 14px 28px rgba(0, 0, 0, 0.7));
}

.card-cover__cat {
  position: absolute;
  left: 22px;
  top: 20px;
  font-size: 10px;
  letter-spacing: 0.26em;
  text-transform: uppercase;
  color: var(--muted);
}

.card-cover__glyph {
  position: absolute;
  left: 20px;
  bottom: 18px;
  display: flex;
  align-items: flex-end;
  gap: 6px;
  color: color-mix(in srgb, var(--card-accent) 55%, transparent);
  transition: color 0.45s, transform 0.45s;
}

.card-cover__badge {
  width: 30px;
  height: 30px;
  object-fit: contain;
  opacity: 0.75;
  transition: opacity 0.45s, transform 0.45s;
}

.guide-card:hover .card-cover__badge {
  opacity: 1;
  transform: translateY(-2px);
}

.guide-card:hover .card-cover__glyph {
  color: var(--card-accent);
  transform: translateY(-2px);
}

.card-cover__difficulty {
  position: absolute;
  right: 20px;
  top: 24px;
  display: flex;
  gap: 4px;
}

.card-cover__difficulty i {
  width: 14px;
  height: 2px;
  background: rgba(233, 240, 236, 0.18);
  transition: background 0.4s;
}

.card-cover__difficulty i.filled {
  background: rgba(200, 180, 138, 0.85);
}

.card-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 26px 26px 22px;
}

.card-title {
  font-size: 24px;
  letter-spacing: 0.08em;
  color: var(--bone);
  transition: color 0.35s;
}

.guide-card:hover .card-title {
  color: var(--card-accent);
}

.card-subtitle {
  margin-top: 6px;
  font-size: 11px;
  letter-spacing: 0.14em;
  color: var(--dim);
}

.card-summary {
  margin-top: 16px;
  font-size: 13.5px;
  line-height: 1.85;
  color: var(--muted);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: auto;
  padding-top: 18px;
  border-top: 1px solid var(--line);
  font-size: 11px;
  letter-spacing: 0.08em;
  color: var(--dim);
}

.dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--dim);
  opacity: 0.5;
}

.card-arrow {
  margin-left: auto;
  color: var(--muted);
  transition: color 0.35s, transform 0.35s;
}

.guide-card:hover .card-arrow {
  color: var(--card-accent);
  transform: translateX(4px);
}
</style>
