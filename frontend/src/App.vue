<script setup lang="ts">
import { onMounted } from 'vue'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import InkField from './components/InkField.vue'
import { loadCategories } from './composables/useCategories'
import { useGuides } from './composables/useGuides'

const { loadGuides } = useGuides()

// 应用启动时预取分类与攻略列表（后端不可用时页面用静态兜底/失败态）
onMounted(() => {
  loadCategories()
  loadGuides()
})
</script>

<template>
  <InkField />
  <AppHeader />
  <main class="site-main">
    <RouterView v-slot="{ Component }">
      <Transition name="page" mode="out-in">
        <component :is="Component" />
      </Transition>
    </RouterView>
  </main>
  <AppFooter />
</template>

<style>
.page-enter-active,
.page-leave-active {
  transition: opacity 0.32s ease, transform 0.32s cubic-bezier(0.22, 1, 0.36, 1);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}

@media (prefers-reduced-motion: reduce) {
  .page-enter-active,
  .page-leave-active {
    transition: none;
  }
}
</style>
