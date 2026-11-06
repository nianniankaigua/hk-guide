<script setup lang="ts">
import { computed, onMounted } from 'vue'
import GuideCard from '../components/GuideCard.vue'
import KnightSigil from '../components/KnightSigil.vue'
import CategoryGlyph from '../components/CategoryGlyph.vue'
import { CATEGORIES, getCategory } from '../data/categories'
import { useGuides } from '../composables/useGuides'
import { formatCount, formatDate } from '../utils/format'

const { guides, loadGuides } = useGuides()

onMounted(() => loadGuides())

const featured = computed(() => guides.value.filter((guide) => guide.featured))
const latest = computed(() =>
  [...guides.value].sort((a, b) => b.updatedAt.localeCompare(a.updatedAt)).slice(0, 5),
)

const categories = computed(() =>
  CATEGORIES.map((cat) => ({
    ...cat,
    count: guides.value.filter((guide) => guide.category === cat.id).length,
  })),
)

const totalViews = computed(() => guides.value.reduce((sum, guide) => sum + guide.views, 0))
const lastUpdated = computed(
  () => guides.value.map((guide) => guide.updatedAt).sort().slice(-1)[0] ?? '',
)
</script>

<template>
  <div>
    <!-- Hero -->
    <section class="hero">
      <div class="hero-grid container">
        <div class="hero-copy">
          <p class="eyebrow reveal">HALLOWNEST ARCHIVE · 圣巢档案</p>

          <h1 class="hero-title reveal reveal-delay-1">
            <span class="hero-title__cn">空洞骑士</span>
            <span class="hero-title__en">HOLLOW KNIGHT</span>
          </h1>

          <p class="hero-desc reveal reveal-delay-2">
            在废墟之下，所有道路都通向同一个王座。<br />
            写给每一个还在圣巢里迷路的人：BOSS 打法、护符搭配、流程路线，以及最后的完成度。
          </p>

          <div class="hero-actions reveal reveal-delay-3">
            <RouterLink to="/guides" class="btn btn--solid">
              开始阅读
              <span>→</span>
            </RouterLink>
            <RouterLink :to="{ path: '/guides', query: { category: 'advanced' } }" class="btn">
              进阶挑战
            </RouterLink>
          </div>

          <div class="hero-stats reveal reveal-delay-4">
            <div class="hero-stat">
              <strong>{{ guides.length }}</strong>
              <span>篇档案</span>
            </div>
            <div class="hero-stat">
              <strong>{{ CATEGORIES.length }}</strong>
              <span>个分类</span>
            </div>
            <div class="hero-stat">
              <strong>{{ formatCount(totalViews) }}</strong>
              <span>次阅读</span>
            </div>
            <div class="hero-stat">
              <strong>{{ formatDate(lastUpdated) }}</strong>
              <span>最近更新</span>
            </div>
          </div>
        </div>

        <div class="hero-art reveal reveal-delay-2" aria-hidden="true">
          <span class="hero-art__frame"></span>
          <span class="hero-art__ring hero-art__ring--outer"></span>
          <span class="hero-art__ring hero-art__ring--dashed"></span>
          <span class="hero-art__ring hero-art__ring--inner"></span>
          <KnightSigil :size="248" glow class="hero-art__sigil" />
          <span class="hero-art__caption">
            <i>THE HOLLOW KNIGHT</i>
            <b>圣巢的幽灵</b>
          </span>
        </div>
      </div>
    </section>

    <!-- 精选 -->
    <section id="featured" class="section">
      <div class="container">
        <div class="section-head">
          <div class="section-head__title">
            <span class="section-head__index">01 /</span>
            <div>
              <h2 class="section-head__cn">精选档案</h2>
              <span class="section-head__en">Selected Files</span>
            </div>
          </div>
          <RouterLink to="/guides" class="section-link">查看全部 →</RouterLink>
        </div>

        <div class="featured-grid">
          <GuideCard
            v-for="(guide, index) in featured"
            :key="guide.id"
            :guide="guide"
            class="reveal"
            :class="`reveal-delay-${index + 1}`"
          />
        </div>
      </div>
    </section>

    <!-- 分类 -->
    <section class="categories-band">
      <div class="container section">
        <div class="section-head">
          <div class="section-head__title">
            <span class="section-head__index">02 /</span>
            <div>
              <h2 class="section-head__cn">档案分类</h2>
              <span class="section-head__en">Categories</span>
            </div>
          </div>
          <span class="section-note">按你的进度，找到下一步</span>
        </div>

        <div class="cat-grid">
          <RouterLink
            v-for="(cat, index) in categories"
            :key="cat.id"
            :to="{ path: '/guides', query: { category: cat.id } }"
            class="cat-tile"
          >
            <span class="cat-tile__index">0{{ index + 1 }}</span>
            <span class="cat-tile__glyph">
              <CategoryGlyph :category="cat.id" :size="30" />
            </span>
            <h3 class="cat-tile__name">{{ cat.name }}</h3>
            <p class="cat-tile__desc">{{ cat.desc }}</p>
            <span class="cat-tile__foot">
              <span>{{ cat.count }} 篇</span>
              <span class="cat-tile__arrow">→</span>
            </span>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- 最新 -->
    <section class="section">
      <div class="container">
        <div class="section-head">
          <div class="section-head__title">
            <span class="section-head__index">03 /</span>
            <div>
              <h2 class="section-head__cn">最近更新</h2>
              <span class="section-head__en">Latest Entries</span>
            </div>
          </div>
          <RouterLink to="/guides" class="section-link">全部档案 →</RouterLink>
        </div>

        <div class="latest-list">
          <RouterLink
            v-for="(guide, index) in latest"
            :key="guide.id"
            :to="`/guides/${guide.slug}`"
            class="latest-row"
          >
            <span class="latest-index">{{ String(index + 1).padStart(2, '0') }}</span>
            <span class="latest-main">
              <span class="latest-title">{{ guide.title }}</span>
              <span class="latest-sub">{{ guide.subtitle }}</span>
            </span>
            <span class="latest-cat">{{ getCategory(guide.category).name }}</span>
            <span class="latest-mins">{{ guide.readMinutes }} 分钟</span>
            <span class="latest-date">{{ formatDate(guide.updatedAt) }}</span>
            <span class="latest-arrow">→</span>
          </RouterLink>
        </div>
      </div>
    </section>

    <!-- 结尾 -->
    <section class="closing">
      <div class="container closing-inner">
        <p class="closing-quote">
          “在这片废墟之下，<br />
          每一个选择都会留下痕迹。”
        </p>
        <div class="closing-action">
          <span class="closing-label">BEGIN YOUR JOURNEY</span>
          <RouterLink to="/guides" class="btn btn--solid">进入档案库 <span>→</span></RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* ---------- Hero ---------- */

.hero {
  position: relative;
  padding-block: 96px 84px;
  border-bottom: 1px solid var(--line);
  overflow: hidden;
}

.hero::after {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 900px;
  height: 900px;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(169, 230, 218, 0.06), transparent 62%);
  pointer-events: none;
}

.hero-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 0.9fr);
  align-items: center;
  gap: 64px;
}

.hero-title {
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.hero-title__cn {
  font-family: var(--font-display);
  font-size: clamp(64px, 8.4vw, 112px);
  line-height: 1.02;
  letter-spacing: 0.13em;
  color: var(--bone);
  margin-left: -0.08em;
  text-shadow: 0 0 60px rgba(169, 230, 218, 0.12);
}

.hero-title__en {
  font-family: var(--font-body);
  font-size: 10px;
  letter-spacing: 0.52em;
  color: var(--dim);
  padding-left: 0.2em;
}

.hero-desc {
  margin-top: 30px;
  max-width: 500px;
  font-size: 14.5px;
  line-height: 2;
  color: var(--muted);
}

.hero-actions {
  margin-top: 40px;
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
}

.hero-stats {
  margin-top: 58px;
  display: flex;
  flex-wrap: wrap;
  gap: 44px;
  padding-top: 26px;
  border-top: 1px solid var(--line);
}

.hero-stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hero-stat strong {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 500;
  letter-spacing: 0.06em;
  color: var(--bone);
}

.hero-stat span {
  font-size: 10px;
  letter-spacing: 0.22em;
  color: var(--dim);
}

/* 徽记 */

.hero-art {
  position: relative;
  width: 100%;
  max-width: 460px;
  aspect-ratio: 1;
  margin-inline: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-art::before {
  content: '';
  position: absolute;
  inset: 8%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(169, 230, 218, 0.12), transparent 66%);
  filter: blur(10px);
}

.hero-art__frame {
  position: absolute;
  inset: 0;
  border: 1px solid var(--line);
  opacity: 0.7;
  background:
    linear-gradient(90deg, var(--soul) 0 18px, transparent 18px) top left / 100% 1px no-repeat,
    linear-gradient(180deg, var(--soul) 0 18px, transparent 18px) top left / 1px 100% no-repeat,
    linear-gradient(270deg, var(--soul) 0 18px, transparent 18px) bottom right / 100% 1px no-repeat,
    linear-gradient(0deg, var(--soul) 0 18px, transparent 18px) bottom right / 1px 100% no-repeat;
  opacity: 0.45;
}

.hero-art__ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.hero-art__ring--outer {
  width: 86%;
  height: 86%;
  border: 1px solid var(--line-strong);
}

.hero-art__ring--dashed {
  width: 70%;
  height: 70%;
  border: 1px dashed rgba(169, 230, 218, 0.22);
  animation: spin-slow 70s linear infinite;
}

.hero-art__ring--inner {
  width: 52%;
  height: 52%;
  border: 1px solid rgba(233, 240, 236, 0.08);
}

@keyframes spin-slow {
  to {
    transform: rotate(360deg);
  }
}

.hero-art__sigil {
  position: relative;
  z-index: 2;
  animation: drift 9s ease-in-out infinite;
}

.hero-art__caption {
  position: absolute;
  bottom: 2%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.hero-art__caption i {
  font-style: normal;
  font-size: 8.5px;
  letter-spacing: 0.36em;
  color: var(--dim);
}

.hero-art__caption b {
  font-family: var(--font-display);
  font-weight: 400;
  font-size: 11px;
  letter-spacing: 0.24em;
  color: var(--muted);
}

/* ---------- 精选 ---------- */

.featured-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

/* ---------- 分类 ---------- */

.categories-band {
  border-block: 1px solid var(--line);
  background: linear-gradient(180deg, rgba(233, 240, 236, 0.022), transparent);
}

.section-note {
  font-size: 11px;
  letter-spacing: 0.18em;
  color: var(--dim);
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 1px;
  border: 1px solid var(--line);
  background: var(--line);
}

.cat-tile {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-height: 240px;
  padding: 26px 22px 22px;
  background: #070909;
  overflow: hidden;
  transition: background 0.4s;
}

.cat-tile:hover {
  background: #0b0f0f;
}

.cat-tile::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  height: 2px;
  width: 0;
  background: var(--soul);
  transition: width 0.45s cubic-bezier(0.22, 1, 0.36, 1);
}

.cat-tile:hover::after {
  width: 100%;
}

.cat-tile__index {
  position: absolute;
  right: 20px;
  top: 22px;
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.1em;
  color: var(--dim);
}

.cat-tile__glyph {
  color: rgba(169, 230, 218, 0.5);
  margin-top: 26px;
  transition: color 0.4s, transform 0.4s;
}

.cat-tile:hover .cat-tile__glyph {
  color: var(--soul);
  transform: translateY(-3px);
}

.cat-tile__name {
  font-size: 18px;
  letter-spacing: 0.1em;
  color: var(--bone);
}

.cat-tile__desc {
  font-size: 12px;
  line-height: 1.8;
  color: var(--dim);
  flex: 1;
}

.cat-tile__foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 14px;
  border-top: 1px solid var(--line);
  font-size: 10.5px;
  letter-spacing: 0.16em;
  color: var(--muted);
}

.cat-tile__arrow {
  color: var(--dim);
  transition: color 0.4s, transform 0.4s;
}

.cat-tile:hover .cat-tile__arrow {
  color: var(--soul);
  transform: translateX(4px);
}

/* ---------- 最新 ---------- */

.latest-list {
  border-top: 1px solid var(--line);
}

.latest-row {
  position: relative;
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr) 90px 80px 100px 24px;
  align-items: center;
  gap: 20px;
  padding: 22px 12px;
  border-bottom: 1px solid var(--line);
  transition: background 0.4s, padding 0.4s;
}

.latest-row::before {
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

.latest-row:hover {
  background: rgba(233, 240, 236, 0.025);
  padding-left: 24px;
}

.latest-row:hover::before {
  transform: scaleY(1);
}

.latest-index {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--dim);
  letter-spacing: 0.08em;
}

.latest-main {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.latest-title {
  font-family: var(--font-display);
  font-size: 19px;
  letter-spacing: 0.08em;
  color: var(--bone);
  transition: color 0.35s;
}

.latest-row:hover .latest-title {
  color: var(--soul);
}

.latest-sub {
  font-size: 11px;
  letter-spacing: 0.1em;
  color: var(--dim);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.latest-cat {
  font-size: 11px;
  letter-spacing: 0.1em;
  color: var(--muted);
}

.latest-mins,
.latest-date {
  font-size: 11px;
  letter-spacing: 0.08em;
  color: var(--dim);
}

.latest-arrow {
  color: var(--dim);
  transition: color 0.35s, transform 0.35s;
}

.latest-row:hover .latest-arrow {
  color: var(--soul);
  transform: translateX(4px);
}

/* ---------- 结尾 ---------- */

.closing {
  padding-block: 40px 96px;
}

.closing-inner {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 48px;
  padding-top: 56px;
  border-top: 1px solid var(--line);
}

.closing-quote {
  font-family: var(--font-display);
  font-size: clamp(24px, 3.2vw, 38px);
  line-height: 1.6;
  letter-spacing: 0.1em;
  color: var(--bone);
}

.closing-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
  flex-shrink: 0;
}

.closing-label {
  font-size: 9px;
  letter-spacing: 0.34em;
  color: var(--dim);
}

@media (max-width: 1100px) {
  .cat-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
  .cat-tile {
    min-height: 220px;
  }
  .latest-row {
    grid-template-columns: 44px minmax(0, 1fr) 84px 90px 20px;
  }
  .latest-mins {
    display: none;
  }
}

@media (max-width: 900px) {
  .hero-grid {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  .hero-art {
    max-width: 320px;
    order: -1;
  }
  .hero-title__cn {
    font-size: clamp(56px, 12vw, 84px);
  }
  .hero-stats {
    gap: 32px;
  }
  .featured-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

@media (max-width: 760px) {
  .cat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .cat-tile {
    min-height: 200px;
  }
  .latest-row {
    grid-template-columns: 32px minmax(0, 1fr) 20px;
    gap: 12px;
  }
  .latest-cat,
  .latest-date {
    display: none;
  }
  .latest-index {
    font-size: 10px;
  }
  .closing-inner {
    flex-direction: column;
    align-items: flex-start;
  }
  .closing-action {
    align-items: flex-start;
  }
}

@media (max-width: 520px) {
  .hero {
    padding-block: 64px 56px;
  }
  .hero-art {
    max-width: 260px;
  }
  .hero-stats {
    gap: 24px 32px;
  }
  .cat-grid {
    grid-template-columns: 1fr;
  }
  .cat-tile {
    min-height: auto;
  }
}
</style>
