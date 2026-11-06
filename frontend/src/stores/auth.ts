import { computed, reactive } from 'vue'

export interface AuthUser {
  username: string
  nickname: string
}

interface AuthState {
  token: string
  user: AuthUser | null
}

const TOKEN_KEY = 'hk_token'
const USER_KEY = 'hk_user'

const state = reactive<AuthState>({
  token: localStorage.getItem(TOKEN_KEY) ?? '',
  user: readUser(),
})

function readUser(): AuthUser | null {
  try {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? (JSON.parse(raw) as AuthUser) : null
  } catch {
    return null
  }
}

export function useAuth() {
  const isLoggedIn = computed(() => !!state.token && !!state.user)

  function setAuth(token: string, user: AuthUser) {
    state.token = token
    state.user = user
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  }

  function logout() {
    state.token = ''
    state.user = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { state, isLoggedIn, setAuth, logout }
}
