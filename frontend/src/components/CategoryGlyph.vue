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
    :style="category === 'dream' ? { color: '#e9f0ec' } : undefined"
  >
    <!-- BOSS：格林剧团面具 -->
    <template v-if="category === 'boss'">
      <path d="M7.4 6.2C5.9 4.2 4.7 2.8 3.2 1.8c.7 3 1.8 5.2 3.3 6.7" />
      <path d="M16.6 6.2c1.5-2 2.7-3.4 4.2-4.4-.8 3-1.8 5.2-3.3 6.7" />
      <path d="M12 5.2c4.1 0 6.6 3.4 6.6 7.6 0 3.6-2.4 6.6-6.6 8-4.2-1.4-6.6-4.4-6.6-8 0-4.2 2.5-7.6 6.6-7.6Z" />
      <ellipse cx="9.7" cy="11.8" rx="1" ry="2.3" />
      <ellipse cx="14.3" cy="11.8" rx="1" ry="2.3" />
      <path d="M9.2 19.6 8.4 22M12 20.2v2M14.8 19.6l.8 2.4" opacity="0.7" />
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

    <!-- 小怪：生命血茧 -->
    <template v-else-if="category === 'enemy'">
      <path d="M12 3.2c3.2 3.7 4.9 6.7 5.1 9.4.2 3.8-2 6.9-5.1 6.9s-5.1-3.1-5.1-6.9c.2-2.7 1.9-5.7 5.1-9.4Z" />
      <circle cx="10.1" cy="12.4" r="0.8" />
      <circle cx="13.9" cy="12.4" r="0.8" />
      <path d="M8.6 17.6 7 19.6M15.4 17.6 17 19.6" opacity="0.6" />
    </template>

    <!-- 战士之梦：隐藏的梦（白色） -->
    <template v-else-if="category === 'dream'">
      <circle cx="12" cy="12" r="2.1" />
      <path d="M12 4.6c1.5 1.6 1.5 3.6 0 5-1.5-1.4-1.5-3.4 0-5Z" />
      <path d="M12 4.6c1.5 1.6 1.5 3.6 0 5-1.5-1.4-1.5-3.4 0-5Z" transform="rotate(60 12 12)" />
      <path d="M12 4.6c1.5 1.6 1.5 3.6 0 5-1.5-1.4-1.5-3.4 0-5Z" transform="rotate(120 12 12)" />
      <path d="M12 4.6c1.5 1.6 1.5 3.6 0 5-1.5-1.4-1.5-3.4 0-5Z" transform="rotate(180 12 12)" />
      <path d="M12 4.6c1.5 1.6 1.5 3.6 0 5-1.5-1.4-1.5-3.4 0-5Z" transform="rotate(240 12 12)" />
      <path d="M12 4.6c1.5 1.6 1.5 3.6 0 5-1.5-1.4-1.5-3.4 0-5Z" transform="rotate(300 12 12)" />
    </template>

    <!-- 进阶挑战：寻神者 -->
    <template v-else-if="category === 'advanced'">
      <circle cx="12" cy="12" r="9" opacity="0.4" />
      <circle cx="12" cy="12" r="1.9" />
      <path d="M12 4.2 13.5 8 12 9.6 10.5 8Z" />
      <path d="M12 4.2 13.5 8 12 9.6 10.5 8Z" transform="rotate(90 12 12)" />
      <path d="M12 4.2 13.5 8 12 9.6 10.5 8Z" transform="rotate(180 12 12)" />
      <path d="M12 4.2 13.5 8 12 9.6 10.5 8Z" transform="rotate(270 12 12)" />
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
