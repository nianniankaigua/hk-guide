<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GuideCard from '../components/GuideCard.vue'
import RouteMap from '../components/RouteMap.vue'
import CharmBoard from '../components/CharmBoard.vue'
import { CATEGORIES } from '../data/categories'
import { GUIDES, guidesByCategory } from '../data/guides'
import type { Guide } from '../types/guide'

const route = useRoute()

const activeId = computed(() =>
  typeof route.query.category === 'string' ? route.query.category : '',
)
const activeCategory = computed(() => CATEGORIES.find((cat) => cat.id === activeId.value))

const guides = computed(() =>
  (activeId.value ? guidesByCategory(activeId.value) : [...GUIDES]).sort((a, b) =>
    b.updatedAt.localeCompare(a.updatedAt),
  ),
)

const filters = computed(() => [
  { id: '', name: '全部', count: GUIDES.length },
  ...CATEGORIES.map((cat) => ({
    id: cat.id,
    name: cat.name,
    count: GUIDES.filter((guide) => guide.category === cat.id).length,
  })),
])

const isGrouped = computed(() => activeId.value === 'boss' || activeId.value === 'enemy')

/** 地图推进顺序，用于分组排序 */
const REGION_ORDER = [
  '遗忘十字路',
  '苍绿之径',
  '真菌荒地',
  '水晶山峰',
  '灵魂圣所',
  '守望者尖塔',
  '皇家水道',
  '雾之峡谷',
  '深巢',
  '古老盆地',
  '王后花园',
  '蜂巢',
  '爱之塔',
  '德特茅斯',
  '格林剧团',
  '黑卵圣殿',
  '神居',
]

/** BOSS / 小怪按地图环境分组 */
const regionGroups = computed(() => {
  if (!isGrouped.value) return []
  const groups = new Map<string, Guide[]>()
  for (const guide of guides.value) {
    const region = guide.region ?? '其他区域'
    const list = groups.get(region) ?? []
    list.push(guide)
    groups.set(region, list)
  }
  return Array.from(groups, ([region, items]) => ({ region, items })).sort((a, b) => {
    const ai = REGION_ORDER.indexOf(a.region)
    const bi = REGION_ORDER.indexOf(b.region)
    return (ai === -1 ? 999 : ai) - (bi === -1 ? 999 : bi)
  })
})

function filterTo(id: string) {
  return id ? { path: '/guides', query: { category: id } } : { path: '/guides' }
}
</script>

<template>
  <div class="archive">
    <section class="page-head container">
      <div class="page-head__top reveal">
        <p class="eyebrow">ARCHIVE · 攻略档案</p>
        <span class="page-head__count">{{ guides.length }} 篇</span>
      </div>

      <div class="page-head__main reveal reveal-delay-1">
        <h1 class="page-title">
          {{ activeCategory ? activeCategory.name : '全部攻略' }}
        </h1>
        <p class="page-desc">
          {{ activeCategory ? activeCategory.desc : '从第一场大黄蜂到神居的绝对辐光，圣巢里的每一条路都记录在这里。' }}
        </p>
      </div>

      <nav class="filters reveal reveal-delay-2" aria-label="攻略分类筛选">
        <RouterLink
          v-for="item in filters"
          :key="item.id || 'all'"
          :to="filterTo(item.id)"
          class="filter"
          :class="{ active: activeId === item.id }"
        >
          {{ item.name }}
          <span class="filter__count">{{ item.count }}</span>
        </RouterLink>
      </nav>
    </section>

    <section v-if="activeId === 'route'" class="container route-map-section">
      <RouteMap />
    </section>

    <section v-if="activeId === 'charm'" class="container charm-board-section">
      <CharmBoard />
    </section>

    <section v-if="isGrouped" class="container region-archive">
      <div v-for="group in regionGroups" :key="group.region" class="region-group">
        <div class="region-group__head">
          <h2 class="region-group__title">{{ group.region }}</h2>
          <span class="region-group__count">{{ group.items.length }} 篇</span>
        </div>
        <div class="guide-grid">
          <GuideCard
            v-for="(guide, index) in group.items"
            :key="guide.id"
            :guide="guide"
            class="reveal"
            :class="`reveal-delay-${(index % 3) + 1}`"
          />
        </div>
      </div>
    </section>

    <section v-else class="container guide-grid">
      <GuideCard
        v-for="(guide, index) in guides"
        :key="guide.id"
        :guide="guide"
        class="reveal"
        :class="`reveal-delay-${(index % 3) + 1}`"
      />
    </section>

    <section v-if="!guides.length" class="container empty">
      <p class="empty__char">空</p>
      <p class="empty__text">这个分类还没有档案，先看看其他内容吧。</p>
      <RouterLink to="/guides" class="btn">返回全部攻略</RouterLink>
    </section>
  </div>
</template>

<style scoped>
.archive {
  padding-bottom: 96px;
}

.page-head {
  padding-top: 72px;
  padding-bottom: 40px;
}

.page-head__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.page-head__count {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.12em;
  color: var(--dim);
}

.page-head__main {
  margin-top: 26px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 48px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--line);
}

.page-title {
  font-size: clamp(40px, 5.4vw, 62px);
  letter-spacing: 0.12em;
  line-height: 1.1;
  color: var(--bone);
  white-space: nowrap;
}

.page-desc {
  max-width: 480px;
  font-size: 13.5px;
  line-height: 1.9;
  color: var(--muted);
  text-align: right;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 28px;
}

.filter {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 9px 16px;
  border: 1px solid var(--line);
  background: transparent;
  font-size: 12px;
  letter-spacing: 0.1em;
  color: var(--muted);
  transition: color 0.3s, border-color 0.3s, background 0.3s;
}

.filter:hover {
  color: var(--bone);
  border-color: var(--line-strong);
}

.filter.active {
  color: var(--soul);
  border-color: rgba(169, 230, 218, 0.45);
  background: var(--soul-soft);
}

.filter__count {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--dim);
}

.filter.active .filter__count {
  color: rgba(169, 230, 218, 0.7);
}

.route-map-section {
  margin-top: 48px;
}

.charm-board-section {
  margin-top: 48px;
}

.region-archive {
  margin-top: 48px;
  display: flex;
  flex-direction: column;
  gap: 56px;
}

.region-group__head {
  display: flex;
  align-items: baseline;
  gap: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line);
}

.region-group__title {
  font-size: clamp(20px, 2.2vw, 27px);
  letter-spacing: 0.1em;
}

.region-group__count {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.1em;
  color: var(--dim);
}

.region-group .guide-grid {
  margin-top: 24px;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
  margin-top: 48px;
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

@media (max-width: 1000px) {
  .guide-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .page-head__main {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  .page-desc {
    text-align: left;
  }
}

@media (max-width: 640px) {
  .page-head {
    padding-top: 52px;
  }
  .guide-grid {
    grid-template-columns: 1fr;
    margin-top: 36px;
  }
  .page-title {
    white-space: normal;
  }
}
</style>
