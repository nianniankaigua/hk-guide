import { api } from '../api'
import { CATEGORIES } from '../data/categories'
import type { CategoryMeta } from '../types/guide'

let started = false

/**
 * 从后端拉取分类并就地替换。
 * CATEGORIES 是 reactive 数组，替换后所有引用处（导航、页脚、筛选条）自动更新；
 * 后端不可用时保留 data/categories.ts 里的静态兜底数据。
 */
export async function loadCategories() {
  if (started) return
  started = true
  try {
    const list = await api<CategoryMeta[]>('/categories')
    if (Array.isArray(list) && list.length) {
      CATEGORIES.splice(0, CATEGORIES.length, ...list)
    }
  } catch {
    // 保留静态兜底
  }
}
