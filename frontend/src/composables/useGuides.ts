import { ref } from 'vue'
import { api } from '../api'
import type { Guide } from '../types/guide'

/**
 * 攻略列表的全局缓存。
 * /api/guides 列表接口不含正文，体积小，全站共享一份。
 */
const guides = ref<Guide[]>([])
const loading = ref(false)
const loaded = ref(false)
const failed = ref(false)

export function useGuides() {
  async function loadGuides(force = false) {
    if (loading.value) return
    if (loaded.value && !force) return
    loading.value = true
    failed.value = false
    try {
      guides.value = await api<Guide[]>('/guides')
      loaded.value = true
    } catch {
      failed.value = true
    } finally {
      loading.value = false
    }
  }

  return { guides, loading, loaded, failed, loadGuides }
}
