<script setup lang="ts">
import type { CategoryId } from '../types/guide'

withDefaults(
  defineProps<{
    category: CategoryId
    size?: number
  }>(),
  { size: 24 },
)
</script>

<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.2"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <template v-if="category === 'boss'">
      <path d="M8 9C6.5 6.5 5 4.5 3 3c.8 3.2 2 5.6 3.6 7.2" />
      <path d="M16 9c1.5-2.5 3-4.5 5-6-.8 3.2-2 5.6-3.6 7.2" />
      <path d="M12 5.5c4 0 6.4 3.6 6.4 8.2 0 3.9-2.6 6.8-6.4 7.8-3.8-1-6.4-3.9-6.4-7.8C5.6 9.1 8 5.5 12 5.5Z" />
      <ellipse cx="9.8" cy="12.5" rx="1.1" ry="2.6" />
      <ellipse cx="14.2" cy="12.5" rx="1.1" ry="2.6" />
    </template>

    <template v-else-if="category === 'charm'">
      <path d="M12 2.8 19.4 12 12 21.2 4.6 12Z" />
      <path d="M12 7.6 15.4 12 12 16.4 8.6 12Z" opacity="0.55" />
    </template>

    <template v-else-if="category === 'route'">
      <path d="M4 20c3.6-2.4 2-7.4 5.2-9.8C12 7.9 12.6 4.6 20 4" stroke-dasharray="3.4 3" />
      <circle cx="4" cy="20" r="1.4" />
      <circle cx="20" cy="4" r="1.4" />
    </template>

    <template v-else-if="category === 'ending'">
      <path d="M15.5 3.2a8.8 8.8 0 1 0 0 17.6 7.8 7.8 0 1 1 0-17.6Z" />
      <circle cx="18.5" cy="6" r="0.9" />
    </template>

    <template v-else-if="category === 'enemy'">
      <ellipse cx="12" cy="13.2" rx="4.4" ry="5.2" />
      <path d="M8.4 9.4 6.2 6.8M15.6 9.4l2.2-2.6M7.8 12.8H4.2M16.2 12.8h3.6M8.4 16.4l-2.2 2.4M15.6 16.4l2.2 2.4" />
      <circle cx="10.4" cy="11.8" r="0.7" />
      <circle cx="13.6" cy="11.8" r="0.7" />
    </template>

    <template v-else-if="category === 'dream'">
      <path d="M12 3c3 3.4 4.4 6 4.4 8.6A4.4 4.4 0 0 1 12 16a4.4 4.4 0 0 1-4.4-4.4C7.6 9 9 6.4 12 3Z" opacity="0.75" />
      <path d="M12 9.2c1 1.3 1.5 2.3 1.5 3.2A1.5 1.5 0 0 1 12 13.9a1.5 1.5 0 0 1-1.5-1.5c0-.9.5-1.9 1.5-3.2Z" />
    </template>

    <template v-else>
      <circle cx="12" cy="12" r="8.2" />
      <circle cx="12" cy="12" r="3.4" opacity="0.6" />
      <path d="M12 1.8v2.6M12 19.6v2.6M1.8 12h2.6M19.6 12h2.6" />
    </template>
  </svg>
</template>

<style scoped>
svg {
  display: block;
}
</style>
