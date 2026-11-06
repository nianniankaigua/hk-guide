<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import GuideCard from '../components/GuideCard.vue'
import { api } from '../api'
import { useGuides } from '../composables/useGuides'
import { useAuth } from '../stores/auth'

const { isLoggedIn } = useAuth()
const { guides, loadGuides } = useGuides()

const slugs = ref<string[]>([])
const loading = ref(false)
const failed = ref(false)

const favorites = computed(() => {
  const order = new Map(slugs.value.map((slug, index) => [slug, index]))
  return guides.value
    .filter((guide) => order.has(guide.slug))
    .sort((a, b) => (order.get(a.slug) ?? 0) - (order.get(b.slug) ?? 0))
})

async function load() {
  if (!isLoggedIn.value) return
  loading.value = true
  failed.value = false
  try {
    const [data] = await Promise.all([api<{ slugs: string[] }>('/favorites'), loadGuides()])
    slugs.value = data.slugs
  } catch {
    failed.value = true
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="favorites">
    <section class="page-head container">
      <p class="eyebrow reveal">COLLECTION · 我的收藏</p>
      <h1 class="page-title reveal reveal-delay-1">收藏的攻略</h1>
      <p class="page-desc reveal reveal-delay-2">你标记过的攻略都会收在这里，随时回来翻阅。</p>
    </section>

    <section v-if="!isLoggedIn" class="container empty">
      <p class="empty__char">钥</p>
      <p class="empty__text">登录之后才能查看收藏。</p>
      <RouterLink to="/auth" class="btn btn--solid">去登录</RouterLink>
    </section>

    <section v-else-if="loading" class="container empty">
      <p class="empty__text">正在读取收藏…</p>
    </section>

    <section v-else-if="failed" class="container empty">
      <p class="empty__char">!</p>
      <p class="empty__text">收藏列表读取失败，确认后端已启动后重试。</p>
      <button class="btn" type="button" @click="load">重试</button>
    </section>

    <section v-else-if="favorites.length" class="container fav-grid">
      <GuideCard
        v-for="(guide, index) in favorites"
        :key="guide.id"
        :guide="guide"
        class="reveal"
        :class="`reveal-delay-${(index % 3) + 1}`"
      />
    </section>

    <section v-else class="container empty">
      <p class="empty__char">空</p>
      <p class="empty__text">还没有收藏任何攻略，去档案库逛逛吧。</p>
      <RouterLink to="/guides" class="btn">进入档案库</RouterLink>
    </section>
  </div>
</template>

<style scoped>
.favorites {
  padding-bottom: 96px;
}

.page-head {
  padding-top: 72px;
  padding-bottom: 48px;
}

.page-title {
  margin-top: 26px;
  font-size: clamp(36px, 4.6vw, 52px);
  letter-spacing: 0.12em;
  color: var(--bone);
}

.page-desc {
  margin-top: 14px;
  font-size: 13.5px;
  color: var(--muted);
}

.fav-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.empty {
  padding-block: 72px;
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
  .fav-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .fav-grid {
    grid-template-columns: 1fr;
  }
}
</style>
