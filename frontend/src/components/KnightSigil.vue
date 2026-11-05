<script setup lang="ts">
withDefaults(
  defineProps<{
    size?: number
    glow?: boolean
  }>(),
  {
    size: 48,
    glow: false,
  },
)
</script>

<template>
  <svg
    class="knight-sigil"
    :class="{ 'knight-sigil--glow': glow }"
    :width="size"
    :height="(size * 4) / 3"
    viewBox="0 0 120 160"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <defs>
      <filter id="sigil-glow" x="-60%" y="-60%" width="220%" height="220%">
        <feGaussianBlur stdDeviation="4" result="blur" />
        <feMerge>
          <feMergeNode in="blur" />
          <feMergeNode in="SourceGraphic" />
        </feMerge>
      </filter>
      <radialGradient id="sigil-fade" cx="50%" cy="44%" r="60%">
        <stop offset="0%" stop-color="currentColor" stop-opacity="0.95" />
        <stop offset="78%" stop-color="currentColor" stop-opacity="0.82" />
        <stop offset="100%" stop-color="currentColor" stop-opacity="0.45" />
      </radialGradient>
    </defs>

    <g fill="url(#sigil-fade)">
      <path d="M46 48C37 31 25 15 11 5C15 27 24 45 38 56Z" />
      <path d="M74 48C83 31 95 15 109 5C105 27 96 45 82 56Z" />
      <path
        d="M60 34C84 34 98 58 98 86C98 116 82 138 60 146C38 138 22 116 22 86C22 58 36 34 60 34Z"
      />
    </g>

    <g fill="#040506">
      <ellipse cx="47" cy="83" rx="5.6" ry="14" />
      <ellipse cx="73" cy="83" rx="5.6" ry="14" />
    </g>

    <g class="knight-sigil__eyes" fill="var(--soul)" filter="url(#sigil-glow)">
      <ellipse cx="47" cy="83" rx="2.4" ry="7.5" />
      <ellipse cx="73" cy="83" rx="2.4" ry="7.5" />
    </g>

    <g stroke="#040506" stroke-width="1.6" stroke-linecap="round" opacity="0.35">
      <path d="M60 40V52" />
      <path d="M52 56L47 66" />
      <path d="M68 56L73 66" />
    </g>
  </svg>
</template>

<style scoped>
.knight-sigil {
  display: block;
  color: var(--bone);
  overflow: visible;
}

.knight-sigil--glow {
  filter: drop-shadow(0 0 26px rgba(169, 230, 218, 0.22));
}

.knight-sigil--glow .knight-sigil__eyes {
  animation: eye-pulse 5s ease-in-out infinite;
}

@keyframes eye-pulse {
  0%,
  100% {
    opacity: 0.85;
  }
  50% {
    opacity: 1;
  }
}

@media (prefers-reduced-motion: reduce) {
  .knight-sigil--glow .knight-sigil__eyes {
    animation: none;
  }
}
</style>
