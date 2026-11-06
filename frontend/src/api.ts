import { useAuth } from './stores/auth'

const BASE = '/api'

export class ApiError extends Error {
  status: number

  constructor(message: string, status: number) {
    super(message)
    this.status = status
  }
}

/** 统一请求封装：自动带 JWT，统一错误信息 */
export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const { state } = useAuth()
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  if (state.token) headers.Authorization = `Bearer ${state.token}`

  const res = await fetch(BASE + path, { ...options, headers: { ...headers, ...(options.headers as object) } })
  if (!res.ok) {
    const data = await res.json().catch(() => null)
    throw new ApiError(data?.message ?? `请求失败（${res.status}）`, res.status)
  }
  return (await res.json()) as T
}
