<script setup lang="ts">
import KnightSigil from '../components/KnightSigil.vue'
</script>

<template>
  <section class="lost">
    <span class="lost__glow" aria-hidden="true"></span>
    <span class="lost__code" aria-hidden="true">404</span>

    <div class="container lost__inner">
      <div class="lost__art reveal" aria-hidden="true">
        <span class="lost__ring"></span>
        <span class="lost__ring lost__ring--dashed"></span>
        <KnightSigil :size="118" glow />
      </div>

      <p class="eyebrow lost__eyebrow reveal reveal-delay-1">LOST IN HALLOWNEST · 404</p>
      <h1 class="lost__title reveal reveal-delay-2">这一页不在地图上</h1>
      <p class="lost__desc reveal reveal-delay-3">
        你走到了一处没有标注的岔路。可能是链接已经失效，也可能这条路本来就不存在。<br />
        回到有长椅的地方，重新选一条路吧。
      </p>

      <div class="lost__actions reveal reveal-delay-4">
        <RouterLink to="/" class="btn btn--solid">
          回到首页
          <span>→</span>
        </RouterLink>
        <RouterLink to="/guides" class="btn">浏览全部攻略</RouterLink>
      </div>

      <nav class="lost__links reveal reveal-delay-4" aria-label="常用入口">
        <RouterLink to="/search">搜索档案</RouterLink>
        <span aria-hidden="true">·</span>
        <RouterLink :to="{ path: '/guides', query: { category: 'boss' } }">BOSS 攻略</RouterLink>
        <span aria-hidden="true">·</span>
        <RouterLink :to="{ path: '/guides', query: { category: 'dream' } }">战士之梦</RouterLink>
      </nav>
    </div>
  </section>
</template>

<style scoped>
.lost {
  position: relative;
  min-height: calc(100vh - var(--header-h) - 96px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding-block: 80px;
  overflow: hidden;
}

.lost__glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(46% 46% at 50% 44%, rgba(169, 230, 218, 0.07), transparent 70%);
  pointer-events: none;
}

.lost__code {
  position: absolute;
  left: 50%;
  top: 46%;
  transform: translate(-50%, -50%);
  font-family: var(--font-display);
  font-size: clamp(180px, 28vw, 420px);
  line-height: 1;
  letter-spacing: 0.06em;
  color: transparent;
  -webkit-text-stroke: 1px rgba(233, 240, 236, 0.055);
  user-select: none;
  pointer-events: none;
  white-space: nowrap;
}

.lost__inner {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.lost__art {
  position: relative;
  display: grid;
  place-items: center;
  width: 176px;
  height: 176px;
  margin-bottom: 36px;
}

.lost__ring {
  position: absolute;
  inset: 0;
  border: 1px solid var(--line);
  border-radius: 50%;
}

.lost__ring--dashed {
  inset: -18px;
  border-style: dashed;
  border-color: rgba(233, 240, 236, 0.07);
  animation: lost-spin 44s linear infinite;
}

@keyframes lost-spin {
  to {
    transform: rotate(360deg);
  }
}

.lost__eyebrow {
  justify-content: center;
}

.lost__title {
  margin-top: 16px;
  font-size: clamp(30px, 4vw, 48px);
  letter-spacing: 0.14em;
}

.lost__desc {
  max-width: 580px;
  margin-top: 22px;
  color: var(--muted);
  font-size: 14px;
  line-height: 1.95;
}

.lost__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px;
  margin-top: 38px;
}

.lost__links {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 42px;
  font-size: 12px;
  letter-spacing: 0.12em;
  color: var(--dim);
}

.lost__links a {
  color: var(--muted);
  border-bottom: 1px solid transparent;
  padding-bottom: 2px;
  transition: color 0.3s, border-color 0.3s;
}

.lost__links a:hover {
  color: var(--bone);
  border-color: var(--soul);
}

@media (max-width: 640px) {
  .lost {
    padding-block: 56px;
  }

  .lost__art {
    width: 130px;
    height: 130px;
    margin-bottom: 26px;
  }

  .lost__actions {
    width: 100%;
    flex-direction: column;
  }

  .lost__actions .btn {
    width: 100%;
  }

  .lost__desc br {
    display: none;
  }
}

@media (prefers-reduced-motion: reduce) {
  .lost__ring--dashed {
    animation: none;
  }
}
</style>
