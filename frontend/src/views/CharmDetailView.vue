<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { CHARM_BY_SLUG, CHARMS } from '../data/charms'

const route = useRoute()

const charm = computed(() => CHARM_BY_SLUG[String(route.params.slug)])
const index = computed(() => (charm.value ? CHARMS.findIndex((item) => item.slug === charm.value!.slug) : -1))

const synergies = computed(() => {
  const list = charm.value?.synergy ?? []
  return list.map((slug) => CHARM_BY_SLUG[slug]).filter(Boolean)
})

const previous = computed(() => (index.value > 0 ? CHARMS[index.value - 1] : null))
const next = computed(() => (index.value >= 0 && index.value < CHARMS.length - 1 ? CHARMS[index.value + 1] : null))

/** 图标缺失时的兜底 */
const broken = ref<Record<string, boolean>>({})
function markBroken(slug: string) {
  broken.value[slug] = true
}
</script>

<template>
  <div class="charm">
    <div class="container">
      <RouterLink to="/guides?category=charm" class="charm__back">← 返回护符图鉴</RouterLink>

      <template v-if="charm">
        <header class="charm__hero">
          <div class="charm__icon-wrap">
            <img
              v-if="!broken[charm.slug]"
              class="charm__icon"
              :src="charm.icon"
              :alt="charm.name"
              @error="markBroken(charm.slug)"
            />
            <span v-else class="charm__glyph">{{ charm.name.slice(0, 1) }}</span>
          </div>

          <div class="charm__intro">
            <p class="charm__index">CHARM · NO.{{ String(charm.id).padStart(2, '0') }}</p>
            <h1 class="charm__name">{{ charm.name }}</h1>
            <p class="charm__en">{{ charm.en }}</p>

            <div class="charm__notch">
              <span class="charm__notch-label">护符槽</span>
              <span v-if="charm.notches === 0" class="charm__free">不占槽位</span>
              <span v-for="n in charm.notches" v-else :key="n" class="pip" />
            </div>
          </div>
        </header>

        <section class="charm__blocks">
          <article class="charm__block">
            <h2>原文</h2>
            <p class="charm__lore">{{ charm.lore }}</p>
          </article>
          <article class="charm__block">
            <h2>效果</h2>
            <p>{{ charm.effect }}</p>
          </article>
          <article class="charm__block">
            <h2>获取方式</h2>
            <p>{{ charm.location }}</p>
          </article>
          <article class="charm__block">
            <h2>使用建议</h2>
            <p>{{ charm.notes }}</p>
          </article>
        </section>

        <section v-if="synergies.length" class="charm__synergy">
          <h2>搭配</h2>
          <p class="charm__synergy-note">{{ charm.synergyNote }}</p>
          <div class="charm__synergy-grid">
            <RouterLink
              v-for="item in synergies"
              :key="item.slug"
              :to="`/charms/${item.slug}`"
              class="charm__synergy-card"
            >
              <img
                v-if="!broken[item.slug]"
                :src="item.icon"
                :alt="item.name"
                loading="lazy"
                @error="markBroken(item.slug)"
              />
              <span v-else class="charm__synergy-glyph">{{ item.name.slice(0, 1) }}</span>
              <span>{{ item.name }}</span>
            </RouterLink>
          </div>
        </section>

        <nav class="charm__pager">
          <RouterLink v-if="previous" :to="`/charms/${previous.slug}`" class="charm__pager-link">
            <span class="charm__pager-label">上一枚</span>
            <span class="charm__pager-name">{{ previous.name }}</span>
          </RouterLink>
          <RouterLink v-if="next" :to="`/charms/${next.slug}`" class="charm__pager-link charm__pager-link--next">
            <span class="charm__pager-label">下一枚</span>
            <span class="charm__pager-name">{{ next.name }}</span>
          </RouterLink>
        </nav>
      </template>

      <div v-else class="charm__missing">
        <p>没有找到这枚护符。</p>
        <RouterLink to="/guides?category=charm">回到护符图鉴</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.charm {
  padding: 40px 0 96px;
}

.charm__back {
  display: inline-block;
  margin-bottom: 32px;
  font-family: var(--font-mono);
  font-size: 12px;
  letter-spacing: 0.14em;
  color: var(--muted);
  text-decoration: none;
  transition: color 0.18s ease;
}

.charm__back:hover {
  color: var(--bone);
}

.charm__hero {
  display: flex;
  align-items: center;
  gap: 34px;
  flex-wrap: wrap;
  padding-bottom: 32px;
  border-bottom: 1px solid var(--line);
}

.charm__icon-wrap {
  position: relative;
  display: grid;
  place-items: center;
  width: 156px;
  height: 156px;
  border-radius: 50%;
  border: 1px solid var(--line-strong);
  background: radial-gradient(circle at 50% 35%, rgba(40, 50, 48, 0.7), rgba(7, 9, 9, 0.95) 70%);
  box-shadow:
    0 0 0 1px rgba(200, 180, 138, 0.12),
    0 0 46px rgba(200, 180, 138, 0.14),
    inset 0 0 28px rgba(0, 0, 0, 0.6);
}

.charm__icon {
  width: 74%;
  height: 74%;
  object-fit: contain;
  filter: drop-shadow(0 6px 18px rgba(0, 0, 0, 0.6));
}

.charm__glyph {
  font-family: var(--font-display);
  font-size: 42px;
  color: var(--gold);
}

.charm__index {
  margin: 0 0 10px;
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.24em;
  color: var(--dim);
}

.charm__name {
  margin: 0;
  font-family: var(--font-display);
  font-size: clamp(28px, 4.4vw, 42px);
  font-weight: 500;
  letter-spacing: 0.1em;
  color: var(--bone);
}

.charm__en {
  margin: 6px 0 0;
  font-family: var(--font-mono);
  font-size: 12px;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--muted);
}

.charm__notch {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 20px;
}

.charm__notch-label {
  margin-right: 4px;
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.16em;
  color: var(--dim);
}

.charm__free {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.12em;
  color: var(--soul);
}

.pip {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--gold);
  box-shadow: 0 0 10px rgba(200, 180, 138, 0.5);
}

.charm__blocks {
  display: grid;
  gap: 1px;
  margin-top: 36px;
  background: var(--line);
  border: 1px solid var(--line);
}

.charm__block {
  padding: 26px 28px;
  background: var(--ink-900);
}

.charm__block h2,
.charm__synergy h2 {
  margin: 0 0 10px;
  font-family: var(--font-mono);
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  color: var(--dim);
}

.charm__block p {
  margin: 0;
  font-size: 14.5px;
  line-height: 1.85;
  color: var(--text);
}

.charm__block .charm__lore {
  color: var(--muted);
  font-style: italic;
  border-left: 2px solid rgba(200, 180, 138, 0.35);
  padding-left: 14px;
}

.charm__synergy {
  margin-top: 46px;
}

.charm__synergy-note {
  margin: 0 0 18px;
  font-size: 13px;
  color: var(--muted);
  line-height: 1.7;
}

.charm__synergy-grid {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.charm__synergy-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px 10px 10px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--ink-850);
  color: var(--text);
  font-size: 13px;
  text-decoration: none;
  transition:
    border-color 0.18s ease,
    background 0.18s ease,
    color 0.18s ease;
}

.charm__synergy-card img {
  width: 34px;
  height: 34px;
  object-fit: contain;
}

.charm__synergy-glyph {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 1px solid var(--line-strong);
  font-family: var(--font-display);
  font-size: 13px;
  color: var(--gold);
}

.charm__synergy-card:hover {
  border-color: var(--line-strong);
  background: var(--ink-800);
  color: var(--bone);
}

.charm__pager {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 56px;
  padding-top: 24px;
  border-top: 1px solid var(--line);
}

.charm__pager-link {
  display: grid;
  gap: 6px;
  text-decoration: none;
  color: var(--text);
  transition: color 0.18s ease;
}

.charm__pager-link--next {
  text-align: right;
  margin-left: auto;
}

.charm__pager-link:hover {
  color: var(--bone);
}

.charm__pager-label {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.2em;
  color: var(--dim);
}

.charm__pager-name {
  font-family: var(--font-display);
  font-size: 16px;
  letter-spacing: 0.08em;
}

.charm__missing {
  display: grid;
  gap: 12px;
  padding: 60px 0;
  color: var(--muted);
}
</style>
