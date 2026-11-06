<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCharms } from '../composables/useCharms'
import type { Charm } from '../types/charm'

const router = useRouter()
const { charms, loading, failed, loadCharms } = useCharms()

onMounted(() => loadCharms())

/** 与游戏内护符界面一致的 52 个格子（13 列 × 4 行） */
const TOTAL_SLOTS = 52

const hovered = ref<Charm | null>(null)
/** 图标缺失时的兜底，避免出现破图 */
const broken = ref<Record<string, boolean>>({})

function markBroken(charm: Charm) {
  broken.value[charm.slug] = true
}

const slots = computed<(Charm | null)[]>(() => {
  const list: (Charm | null)[] = [...charms.value]
  while (list.length < TOTAL_SLOTS) list.push(null)
  return list
})

function open(charm: Charm) {
  router.push(`/charms/${charm.slug}`)
}

function hover(charm: Charm | null) {
  hovered.value = charm
}
</script>

<template>
  <section class="charmboard">
    <header class="charmboard__head">
      <div>
        <p class="charmboard__eyebrow">CHARM ARCHIVE · 护符图鉴</p>
        <h2 class="charmboard__title">护符槽</h2>
      </div>
      <p class="charmboard__hint">悬停查看，点击进入护符详情</p>
    </header>

    <div v-if="loading" class="charmboard__status">
      <p>正在读取护符图鉴…</p>
    </div>

    <div v-else-if="failed" class="charmboard__status">
      <p>护符图鉴读取失败，确认后端已启动后重试。</p>
      <button class="btn" type="button" @click="loadCharms(true)">重试</button>
    </div>

    <template v-else>
    <div class="charmboard__board">
      <div class="charmboard__grid">
        <button
          v-for="(charm, index) in slots"
          :key="index"
          type="button"
          class="slot"
          :class="{ 'slot--empty': !charm, 'slot--active': charm && hovered?.slug === charm.slug }"
          :disabled="!charm"
          :aria-label="charm ? `${charm.name}，占用 ${charm.notches} 个护符槽` : '空护符槽'"
          @mouseenter="hover(charm)"
          @mouseleave="hover(null)"
          @focus="hover(charm)"
          @blur="hover(null)"
          @click="charm && open(charm)"
        >
          <img
            v-if="charm && !broken[charm.slug]"
            class="slot__icon"
            :src="charm.icon"
            :alt="charm.name"
            loading="lazy"
            @error="markBroken(charm)"
          />
          <span v-else-if="charm" class="slot__glyph">{{ charm.name.slice(0, 1) }}</span>
          <span v-else class="slot__hole" />
        </button>
      </div>
    </div>

    <div class="charmboard__plate">
      <template v-if="hovered">
        <div class="charmboard__plate-main">
          <h3 class="charmboard__name">{{ hovered.name }}</h3>
          <span class="charmboard__en">{{ hovered.en }}</span>
        </div>
        <div class="charmboard__notch" :aria-label="`占用 ${hovered.notches} 个护符槽`">
          <span v-if="hovered.notches === 0" class="charmboard__free">不占槽位</span>
          <span v-for="n in hovered.notches" v-else :key="n" class="pip" />
        </div>
        <p class="charmboard__effect">{{ hovered.effect }}</p>
      </template>
      <template v-else>
        <p class="charmboard__idle">
          <span class="charmboard__idle-mark">◈</span>
          共 {{ charms.length }} 枚护符 · 把光标放上去，这里会显示它的名字与效果
        </p>
      </template>
    </div>
    </template>
  </section>
</template>

<style scoped>
.charmboard {
  display: grid;
  gap: 18px;
}

.charmboard__status {
  padding: 60px 0;
  text-align: center;
  font-size: 13px;
  letter-spacing: 0.1em;
  color: var(--muted);
}

.charmboard__status .btn {
  margin-top: 24px;
}

.charmboard__head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.charmboard__eyebrow {
  margin: 0 0 6px;
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.22em;
  color: var(--dim);
}

.charmboard__title {
  margin: 0;
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 500;
  letter-spacing: 0.1em;
  color: var(--bone);
}

.charmboard__hint {
  margin: 0;
  font-size: 12px;
  color: var(--muted);
  letter-spacing: 0.06em;
}

.charmboard__board {
  position: relative;
  padding: 26px 22px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background:
    radial-gradient(120% 90% at 50% 0%, rgba(42, 52, 50, 0.5), transparent 62%),
    linear-gradient(180deg, #0b0f10, #070909);
  box-shadow:
    inset 0 1px 0 rgba(233, 240, 236, 0.05),
    inset 0 0 60px rgba(0, 0, 0, 0.65);
}

.charmboard__grid {
  display: grid;
  grid-template-columns: repeat(13, minmax(0, 1fr));
  gap: 12px;
}

.slot {
  position: relative;
  display: grid;
  place-items: center;
  aspect-ratio: 1;
  padding: 0;
  border: 1px solid rgba(233, 240, 236, 0.09);
  border-radius: 50%;
  background: radial-gradient(circle at 50% 38%, rgba(24, 30, 30, 0.9), rgba(9, 12, 12, 0.95));
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    border-color 0.18s ease,
    filter 0.18s ease;
}

.slot--empty {
  cursor: default;
  border-color: rgba(233, 240, 236, 0.045);
}

.slot__hole {
  width: 34%;
  height: 34%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(233, 240, 236, 0.07), transparent 70%);
}

.slot__icon {
  width: 82%;
  height: 82%;
  object-fit: contain;
  filter: saturate(0.92) brightness(0.94);
  transition: filter 0.18s ease;
  pointer-events: none;
}

.slot__glyph {
  display: grid;
  place-items: center;
  width: 62%;
  height: 62%;
  border-radius: 50%;
  border: 1px solid var(--line-strong);
  font-family: var(--font-display);
  font-size: 15px;
  color: var(--gold);
  pointer-events: none;
}

.slot--active {
  z-index: 2;
  border-color: rgba(200, 180, 138, 0.7);
  transform: scale(1.1);
  box-shadow:
    0 0 0 1px rgba(200, 180, 138, 0.35),
    0 0 22px rgba(200, 180, 138, 0.3),
    inset 0 0 14px rgba(200, 180, 138, 0.16);
}

.slot--active .slot__icon {
  filter: saturate(1.12) brightness(1.12);
}

.charmboard__plate {
  display: flex;
  align-items: center;
  gap: 14px 18px;
  flex-wrap: wrap;
  min-height: 46px;
  padding: 12px 16px;
  border-top: 1px solid var(--line);
}

.charmboard__plate-main {
  display: flex;
  align-items: baseline;
  gap: 10px;
  min-width: 150px;
}

.charmboard__name {
  margin: 0;
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 500;
  letter-spacing: 0.08em;
  color: var(--bone);
}

.charmboard__en {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.1em;
  color: var(--dim);
  text-transform: uppercase;
}

.charmboard__notch {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pip {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--gold);
  box-shadow: 0 0 8px rgba(200, 180, 138, 0.5);
}

.charmboard__free {
  font-family: var(--font-mono);
  font-size: 11px;
  letter-spacing: 0.12em;
  color: var(--soul);
}

.charmboard__effect {
  flex: 1 1 320px;
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: var(--muted);
}

.charmboard__idle {
  margin: 0;
  font-size: 12.5px;
  letter-spacing: 0.06em;
  color: var(--dim);
}

.charmboard__idle-mark {
  margin-right: 8px;
  color: var(--gold);
}

@media (max-width: 900px) {
  .charmboard__grid {
    grid-template-columns: repeat(10, minmax(0, 1fr));
    gap: 9px;
  }
}

@media (max-width: 640px) {
  .charmboard__board {
    padding: 16px 12px;
  }

  .charmboard__grid {
    grid-template-columns: repeat(7, minmax(0, 1fr));
    gap: 8px;
  }

  .charmboard__plate {
    padding: 12px 4px;
  }
}
</style>
