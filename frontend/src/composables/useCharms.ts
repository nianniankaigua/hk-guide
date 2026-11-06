import { ref } from 'vue'
import { api } from '../api'
import type { Charm } from '../types/charm'

/**
 * 护符图鉴的全局缓存。
 * /api/charms 返回全量 45 条护符，体积小，全站共享一份。
 */
const charms = ref<Charm[]>([])
const loading = ref(false)
const loaded = ref(false)
const failed = ref(false)

export function useCharms() {
  async function loadCharms(force = false) {
    if (loading.value) return
    if (loaded.value && !force) return
    loading.value = true
    failed.value = false
    try {
      charms.value = await api<Charm[]>('/charms')
      loaded.value = true
    } catch {
      failed.value = true
    } finally {
      loading.value = false
    }
  }

  return { charms, loading, loaded, failed, loadCharms }
}
