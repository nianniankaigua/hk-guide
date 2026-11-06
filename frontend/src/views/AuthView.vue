<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../api'
import { useAuth, type AuthUser } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const { setAuth } = useAuth()

const mode = ref<'login' | 'register'>('login')
const username = ref('')
const nickname = ref('')
const password = ref('')
const confirm = ref('')
const error = ref('')
const submitting = ref(false)

function switchMode(next: 'login' | 'register') {
  mode.value = next
  error.value = ''
}

async function submit() {
  error.value = ''
  const name = username.value.trim()
  if (!name || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  if (mode.value === 'register') {
    if (name.length < 3) {
      error.value = '用户名至少 3 个字符'
      return
    }
    if (password.value.length < 6) {
      error.value = '密码至少 6 位'
      return
    }
    if (password.value !== confirm.value) {
      error.value = '两次输入的密码不一致'
      return
    }
  }

  submitting.value = true
  try {
    const isRegister = mode.value === 'register'
    const data = await api<{ token: string; username: string; nickname: string }>(
      isRegister ? '/auth/register' : '/auth/login',
      {
        method: 'POST',
        body: JSON.stringify(
          isRegister ? { username: name, password: password.value, nickname: nickname.value.trim() || undefined } : { username: name, password: password.value },
        ),
      },
    )
    const user: AuthUser = { username: data.username, nickname: data.nickname || data.username }
    setAuth(data.token, user)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    router.push(redirect)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '网络异常，请稍后再试'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="auth container">
    <div class="auth-card reveal">
      <p class="eyebrow">ACCOUNT · 旅者名册</p>

      <div class="auth-tabs" role="tablist">
        <button
          type="button"
          class="auth-tab"
          :class="{ active: mode === 'login' }"
          @click="switchMode('login')"
        >
          登录
        </button>
        <button
          type="button"
          class="auth-tab"
          :class="{ active: mode === 'register' }"
          @click="switchMode('register')"
        >
          注册
        </button>
      </div>

      <h1 class="auth-title">{{ mode === 'login' ? '回到圣巢' : '留下你的名字' }}</h1>
      <p class="auth-sub">
        {{ mode === 'login' ? '登录后可以收藏攻略，记录你在圣巢的足迹。' : '注册一个旅者账号，开始收藏你的攻略。' }}
      </p>

      <form class="auth-form" @submit.prevent="submit">
        <label class="field">
          <span class="field-label">用户名</span>
          <input v-model="username" type="text" autocomplete="username" placeholder="3 个字符以上" />
        </label>

        <label v-if="mode === 'register'" class="field">
          <span class="field-label">昵称（选填）</span>
          <input v-model="nickname" type="text" placeholder="不填则使用用户名" />
        </label>

        <label class="field">
          <span class="field-label">密码</span>
          <input
            v-model="password"
            type="password"
            :autocomplete="mode === 'login' ? 'current-password' : 'new-password'"
            :placeholder="mode === 'register' ? '至少 6 位' : ''"
          />
        </label>

        <label v-if="mode === 'register'" class="field">
          <span class="field-label">确认密码</span>
          <input v-model="confirm" type="password" autocomplete="new-password" />
        </label>

        <p v-if="error" class="auth-error" role="alert">{{ error }}</p>

        <button class="btn btn--solid auth-submit" type="submit" :disabled="submitting">
          {{ submitting ? '请稍候…' : mode === 'login' ? '登 录' : '注 册' }}
        </button>
      </form>

      <RouterLink to="/" class="auth-back">← 先随便逛逛</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.auth {
  display: flex;
  justify-content: center;
  padding-block: 96px 120px;
}

.auth-card {
  width: min(440px, 100%);
  padding: 44px 40px 36px;
  border: 1px solid var(--line);
  background: linear-gradient(160deg, rgba(233, 240, 236, 0.03), rgba(233, 240, 236, 0.008));
}

.auth-tabs {
  display: flex;
  gap: 8px;
  margin-top: 26px;
}

.auth-tab {
  flex: 1;
  padding: 10px 0;
  border: 1px solid var(--line);
  background: transparent;
  font-size: 12px;
  letter-spacing: 0.24em;
  color: var(--muted);
  transition: color 0.3s, border-color 0.3s, background 0.3s;
}

.auth-tab:hover {
  color: var(--bone);
  border-color: var(--line-strong);
}

.auth-tab.active {
  color: var(--soul);
  border-color: rgba(169, 230, 218, 0.45);
  background: var(--soul-soft);
}

.auth-title {
  margin-top: 30px;
  font-size: 30px;
  letter-spacing: 0.12em;
}

.auth-sub {
  margin-top: 10px;
  font-size: 13px;
  color: var(--muted);
}

.auth-form {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field-label {
  font-size: 11px;
  letter-spacing: 0.2em;
  color: var(--dim);
}

.field input {
  border: 0;
  border-bottom: 1px solid var(--line);
  background: transparent;
  padding: 8px 2px;
  color: var(--bone);
  font-family: var(--font-body);
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.field input:focus {
  border-color: rgba(169, 230, 218, 0.6);
}

.field input::placeholder {
  color: var(--dim);
}

.auth-error {
  font-size: 12.5px;
  color: #d98f8f;
  letter-spacing: 0.05em;
}

.auth-submit {
  width: 100%;
  margin-top: 6px;
}

.auth-submit:disabled {
  opacity: 0.55;
  cursor: wait;
}

.auth-back {
  display: inline-block;
  margin-top: 24px;
  font-size: 12px;
  letter-spacing: 0.1em;
  color: var(--muted);
  transition: color 0.3s;
}

.auth-back:hover {
  color: var(--soul);
}
</style>
