<script setup lang="ts">
import { activeAccent, activeBackdrop } from '../composables/useBackdrop'
</script>

<template>
  <div class="ink-field" aria-hidden="true">
    <Transition name="boss">
      <div
        v-if="activeBackdrop"
        :key="activeBackdrop"
        class="ink-field__boss"
        :style="{ backgroundImage: `url(${activeBackdrop})`, '--field-accent': activeAccent }"
      ></div>
    </Transition>
    <div class="ink-field__wash"></div>
    <div class="ink-field__mist"></div>
    <div class="ink-field__noise"></div>
    <div class="ink-field__vignette"></div>
  </div>
</template>

<style scoped>
.ink-field {
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
  background: var(--ink-950);
}

.ink-field__boss {
  position: absolute;
  inset: -8%;
  background-size: cover;
  background-position: center;
  filter: blur(9px) saturate(0.72) brightness(0.42);
  opacity: 0.8;
  transform: scale(1.03);
  will-change: opacity;
}

.ink-field__boss::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(
    70% 55% at 50% -5%,
    color-mix(in srgb, var(--field-accent) 22%, transparent),
    transparent 68%
  );
}

.ink-field__boss::after {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(72% 62% at 50% 24%, transparent 0%, rgba(4, 5, 6, 0.5) 100%),
    linear-gradient(180deg, rgba(4, 5, 6, 0.2), rgba(4, 5, 6, 0.78));
}

.boss-enter-active,
.boss-leave-active {
  transition: opacity 1s ease;
}

.boss-enter-from,
.boss-leave-to {
  opacity: 0;
}

.ink-field__wash {
  position: absolute;
  inset: -20%;
  background:
    radial-gradient(58% 46% at 50% -6%, rgba(60, 96, 88, 0.28) 0%, transparent 62%),
    radial-gradient(38% 32% at 88% 16%, rgba(169, 230, 218, 0.09) 0%, transparent 70%),
    radial-gradient(42% 36% at 8% 82%, rgba(200, 180, 138, 0.06) 0%, transparent 72%),
    radial-gradient(70% 60% at 50% 118%, rgba(23, 32, 31, 0.9) 0%, transparent 70%);
}

.ink-field__mist {
  position: absolute;
  left: -10%;
  right: -10%;
  bottom: -8%;
  height: 62%;
  background:
    radial-gradient(45% 70% at 22% 100%, rgba(120, 168, 158, 0.08) 0%, transparent 70%),
    radial-gradient(50% 80% at 74% 100%, rgba(120, 168, 158, 0.06) 0%, transparent 72%);
  filter: blur(14px);
  animation: mist-drift 26s ease-in-out infinite alternate;
}

@keyframes mist-drift {
  from {
    transform: translate3d(-2%, 1%, 0) scale(1);
  }
  to {
    transform: translate3d(2%, -2%, 0) scale(1.06);
  }
}

.ink-field__noise {
  position: absolute;
  inset: 0;
  opacity: 0.045;
  mix-blend-mode: overlay;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='260' height='260'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.82' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='260' height='260' filter='url(%23n)'/%3E%3C/svg%3E");
  background-size: 260px 260px;
}

.ink-field__vignette {
  position: absolute;
  inset: 0;
  background: radial-gradient(120% 90% at 50% 40%, transparent 42%, rgba(2, 3, 4, 0.82) 100%);
}

@media (prefers-reduced-motion: reduce) {
  .ink-field__mist {
    animation: none;
  }
}
</style>
