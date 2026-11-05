<script setup lang="ts">
import mapImage from '../assets/hallownest-regions.webp'
import { MAP_REGIONS } from '../data/hallownestMap'
</script>

<template>
  <section class="routemap">
    <div class="routemap__head">
      <div class="section-head__title">
        <span class="section-head__index">MAP /</span>
        <div>
          <h2 class="section-head__cn">圣巢地图</h2>
          <span class="section-head__en">Map of Hallownest</span>
        </div>
      </div>
      <p class="routemap__hint">完整的世界地图，包含全部区域与通路。下方图例可对照各区域名称。</p>
    </div>

    <div class="routemap__canvas">
      <img class="routemap__image" :src="mapImage" alt="圣巢区域地图" draggable="false" />
      <span class="routemap__grid" aria-hidden="true"></span>

      <div class="axis axis--x" aria-hidden="true">
        <span>0</span>
        <span>25</span>
        <span>50</span>
        <span>75</span>
        <span>100</span>
        <i>X</i>
      </div>
      <div class="axis axis--y" aria-hidden="true">
        <span>0</span>
        <span>25</span>
        <span>50</span>
        <span>75</span>
        <span>100</span>
        <i>Y</i>
      </div>
    </div>

    <div class="legend">
      <span class="legend__title">区域</span>
      <span v-for="region in MAP_REGIONS" :key="region.id" class="legend__item">
        <i :style="{ background: region.color }"></i>
        {{ region.name }}
      </span>
    </div>
  </section>
</template>

<style scoped>
.routemap__head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.routemap__hint {
  max-width: 460px;
  font-size: 11px;
  line-height: 1.8;
  letter-spacing: 0.08em;
  color: var(--dim);
  text-align: right;
}

.routemap__canvas {
  position: relative;
  width: 100%;
  aspect-ratio: 4533 / 3278;
  border: 1px solid var(--line);
  background:
    radial-gradient(circle at 50% 40%, rgba(169, 230, 218, 0.05), transparent 66%),
    #060809;
  overflow: hidden;
}

.routemap__image {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: fill;
  opacity: 0.95;
  pointer-events: none;
  user-select: none;
}

.routemap__grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(to right, rgba(233, 240, 236, 0.045) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(233, 240, 236, 0.045) 1px, transparent 1px);
  background-size: 10% 10%;
  pointer-events: none;
}

.axis {
  position: absolute;
  font-family: var(--font-mono);
  font-size: 9px;
  letter-spacing: 0.08em;
  color: rgba(233, 240, 236, 0.26);
  pointer-events: none;
}

.axis i {
  position: absolute;
  font-style: normal;
  letter-spacing: 0.2em;
  color: rgba(233, 240, 236, 0.2);
}

.axis--x {
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: space-between;
  padding: 4px 6px 5px;
  background: linear-gradient(to top, rgba(4, 5, 6, 0.6), transparent);
}

.axis--x i {
  right: 6px;
  bottom: 16px;
}

.axis--y {
  top: 0;
  bottom: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 26px;
  padding: 6px 0 18px;
  background: linear-gradient(to right, rgba(4, 5, 6, 0.45), transparent);
}

.axis--y i {
  top: 6px;
  left: 8px;
}

.legend {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px 14px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid var(--line);
}

.legend__title {
  font-family: var(--font-mono);
  font-size: 10px;
  letter-spacing: 0.16em;
  color: var(--dim);
}

.legend__item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  letter-spacing: 0.04em;
  color: var(--muted);
}

.legend__item i {
  width: 9px;
  height: 9px;
  border-radius: 2px;
  opacity: 0.9;
}

@media (max-width: 900px) {
  .routemap__head {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .routemap__hint {
    max-width: none;
    text-align: left;
  }
}
</style>
