<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CATEGORIES } from '../data/categories'
import { useAuth } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const { state: auth, isLoggedIn, logout } = useAuth()

function handleLogout() {
  logout()
  if (route.path === '/favorites') router.push('/')
}

const keyword = ref('')
const scrolled = ref(false)

const navItems = [
  { id: 'all', name: '全部', to: { path: '/guides' } },
  ...CATEGORIES.map((cat) => ({
    id: cat.id,
    name: cat.name.replace(' 攻略', '').replace('图鉴', '').replace('收集', '').replace('挑战', '').replace('流程', ''),
    to: { path: '/guides', query: { category: cat.id } },
  })),
  { id: 'forum', name: '论坛', to: { path: '/forum' } },
]

function isActive(id: string): boolean {
  if (id === 'forum') return route.path.startsWith('/forum')
  if (id === 'all') return route.path === '/guides' && !route.query.category
  return route.path === '/guides' && route.query.category === id
}

function submitSearch() {
  const value = keyword.value.trim()
  if (!value) return
  router.push({ path: '/search', query: { q: value } })
  keyword.value = ''
}

function handleScroll() {
  scrolled.value = window.scrollY > 12
}

onMounted(() => {
  handleScroll()
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <header class="site-header" :class="{ 'is-scrolled': scrolled }">
    <div class="container header-inner">
      <RouterLink to="/" class="brand" aria-label="返回首页">
        <span class="brand-mark">
          <img class="brand-mark__img" src="/badges/knight.png" alt="圣巢档案" />
        </span>
        <span class="brand-copy">
          <span class="brand-cn">圣巢档案</span>
          <span class="brand-en">HALLOWNEST ARCHIVE</span>
        </span>
      </RouterLink>

      <nav class="nav" aria-label="分类导航">
        <RouterLink
          v-for="item in navItems"
          :key="item.id"
          :to="item.to"
          class="nav-link"
          :class="{ active: isActive(item.id) }"
        >
          {{ item.name }}
        </RouterLink>
      </nav>

      <form class="search" role="search" @submit.prevent="submitSearch">
        <svg class="search-icon" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" aria-hidden="true">
          <circle cx="10.5" cy="10.5" r="6.5" />
          <path d="M15.5 15.5 21 21" />
        </svg>
        <input v-model="keyword" type="search" placeholder="搜索攻略…" aria-label="搜索攻略" />
      </form>

      <RouterLink v-if="!isLoggedIn" to="/auth" class="auth-entry">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" aria-hidden="true">
          <circle cx="12" cy="8" r="4" />
          <path d="M4.5 20c1.6-3.4 4.2-5 7.5-5s5.9 1.6 7.5 5" />
        </svg>
        <span>登录</span>
      </RouterLink>
      <div v-else class="auth-area">
        <RouterLink to="/favorites" class="auth-link" :class="{ active: route.path === '/favorites' }">
          {{ auth.user?.nickname || auth.user?.username }}
        </RouterLink>
        <button type="button" class="auth-logout" @click="handleLogout">退出</button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.site-header {
  position: sticky;
  top: 0;
  z-index: 60;
  height: var(--header-h);
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--line);
  background: linear-gradient(180deg, rgba(4, 5, 6, 0.9), rgba(4, 5, 6, 0.68));
  backdrop-filter: blur(16px) saturate(120%);
  -webkit-backdrop-filter: blur(16px) saturate(120%);
  transition: background 0.4s, border-color 0.4s, box-shadow 0.4s;
}

.site-header.is-scrolled {
  background: rgba(4, 5, 6, 0.96);
  border-bottom-color: var(--line-strong);
  box-shadow: 0 18px 40px -30px #000;
}

.header-inner {
  display: flex;
  align-items: center;
  gap: 44px;
  height: 100%;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.brand-mark {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
}

.brand-mark__img {
  width: 30px;
  height: 30px;
  object-fit: contain;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  line-height: 1.35;
}

.brand-cn {
  font-family: var(--font-display);
  font-size: 16px;
  letter-spacing: 0.2em;
  color: var(--bone);
}

.brand-en {
  font-size: 8px;
  letter-spacing: 0.3em;
  color: var(--dim);
}

.nav {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-link {
  position: relative;
  font-size: 12.5px;
  letter-spacing: 0.18em;
  color: var(--muted);
  padding-block: 6px;
  transition: color 0.3s;
}

.nav-link::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1px;
  background: var(--soul);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.nav-link:hover,
.nav-link.active {
  color: var(--bone);
}

.nav-link:hover::after,
.nav-link.active::after {
  transform: scaleX(1);
}

.search {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 10px;
  width: 200px;
  padding: 8px 2px;
  border-bottom: 1px solid var(--line);
  color: var(--dim);
  transition: border-color 0.35s, color 0.35s;
}

.search:focus-within {
  border-color: rgba(169, 230, 218, 0.6);
  color: var(--soul);
}

.search input {
  flex: 1;
  min-width: 0;
  border: 0;
  background: transparent;
  color: var(--bone);
  font-family: var(--font-body);
  font-size: 13px;
  letter-spacing: 0.05em;
  outline: none;
}

.search input::placeholder {
  color: var(--dim);
}

.search input::-webkit-search-cancel-button {
  filter: invert(0.6);
}

.auth-entry {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  flex-shrink: 0;
  font-size: 12.5px;
  letter-spacing: 0.18em;
  color: var(--muted);
  padding-block: 6px;
  transition: color 0.3s;
}

.auth-entry:hover {
  color: var(--bone);
}

.auth-area {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}

.auth-link {
  font-size: 12.5px;
  letter-spacing: 0.12em;
  color: var(--soul);
  padding-block: 6px;
  transition: color 0.3s;
}

.auth-link:hover,
.auth-link.active {
  color: var(--bone);
}

.auth-logout {
  border: 0;
  background: transparent;
  padding: 6px 0;
  font-size: 12px;
  letter-spacing: 0.18em;
  color: var(--dim);
  transition: color 0.3s;
}

.auth-logout:hover {
  color: #d98f8f;
}

@media (max-width: 1040px) {
  .nav {
    gap: 20px;
  }
  .header-inner {
    gap: 28px;
  }
}

@media (max-width: 900px) {
  .nav {
    display: none;
  }
}

@media (max-width: 520px) {
  .search {
    width: 130px;
  }
  .brand-en {
    display: none;
  }
}
</style>
