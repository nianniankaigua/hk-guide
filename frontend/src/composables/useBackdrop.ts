import { computed, ref } from 'vue'

/** 详情页等场景设置的常驻背景 */
const baseImage = ref<string | null>(null)
const baseAccent = ref<string | null>(null)
/** 卡片 hover / focus 时的临时预览背景 */
const previewImage = ref<string | null>(null)
const previewAccent = ref<string | null>(null)

export const activeBackdrop = computed(() => previewImage.value ?? baseImage.value)
export const activeAccent = computed(() => previewAccent.value ?? baseAccent.value ?? '#a9e6da')

export function setBaseBackdrop(image?: string, accent?: string) {
  baseImage.value = image ?? null
  baseAccent.value = accent ?? null
}

export function previewBackdrop(image?: string, accent?: string) {
  previewImage.value = image ?? null
  previewAccent.value = accent ?? null
}

export function clearBackdropPreview() {
  previewImage.value = null
  previewAccent.value = null
}
