<script setup lang="ts">
import { CATEGORIES } from '../data/categories'
</script>

<template>
  <footer class="site-footer">
    <div class="container footer-main">
      <div class="footer-brand">
        <div class="footer-brand__row">
          <img class="footer-brand__img" src="/badges/knight.png" alt="圣巢档案" />
          <div>
            <p class="footer-cn">圣巢档案</p>
            <p class="footer-en">HALLOWNEST ARCHIVE</p>
          </div>
        </div>
        <p class="footer-quote">“没有荣耀，只有继续前行。”</p>
      </div>

      <div class="footer-links">
        <div class="footer-col">
          <span class="footer-col__title">分类</span>
          <RouterLink
            v-for="cat in CATEGORIES"
            :key="cat.id"
            :to="{ path: '/guides', query: { category: cat.id } }"
          >
            {{ cat.name }}
          </RouterLink>
        </div>
        <div class="footer-col">
          <span class="footer-col__title">站内</span>
          <RouterLink to="/guides">全部攻略</RouterLink>
          <RouterLink to="/search">搜索档案</RouterLink>
          <a href="/#featured">精选推荐</a>
        </div>
      </div>
    </div>

    <div class="container footer-bottom">
      <p>© {{ new Date().getFullYear() }} 圣巢档案 · 民间攻略档案，与 Team Cherry 无关</p>
      <p class="footer-note">手工整理 · 持续更新</p>
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  position: relative;
  z-index: 1;
  margin-top: 40px;
  border-top: 1px solid var(--line);
  background: linear-gradient(180deg, rgba(8, 10, 11, 0.5), rgba(3, 4, 5, 0.9));
}

.footer-main {
  display: flex;
  justify-content: space-between;
  gap: 60px;
  padding-block: 64px 48px;
}

.footer-brand__row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.footer-brand__img {
  width: 38px;
  height: 38px;
  object-fit: contain;
}

.footer-cn {
  font-family: var(--font-display);
  font-size: 18px;
  letter-spacing: 0.22em;
  color: var(--bone);
}

.footer-en {
  margin-top: 2px;
  font-size: 8px;
  letter-spacing: 0.3em;
  color: var(--dim);
}

.footer-quote {
  margin-top: 26px;
  font-family: var(--font-display);
  font-size: 14px;
  letter-spacing: 0.12em;
  color: var(--muted);
}

.footer-links {
  display: flex;
  gap: 80px;
}

.footer-col {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.footer-col__title {
  font-size: 10px;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: var(--dim);
  margin-bottom: 6px;
}

.footer-col a {
  font-size: 13px;
  letter-spacing: 0.08em;
  color: var(--muted);
  transition: color 0.3s;
}

.footer-col a:hover {
  color: var(--soul);
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding-block: 22px 30px;
  border-top: 1px solid var(--line);
  font-size: 11px;
  letter-spacing: 0.08em;
  color: var(--dim);
}

.footer-note {
  letter-spacing: 0.2em;
}

@media (max-width: 820px) {
  .footer-main {
    flex-direction: column;
    gap: 40px;
    padding-block: 48px 36px;
  }
  .footer-links {
    gap: 56px;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
