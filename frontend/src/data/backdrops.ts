/**
 * 自动收集 src/assets/backdrops 下的场景图。
 * 只要文件名与攻略 slug 相同（如 broken-vessel.png），
 * 卡片背景和页面背景会自动启用，无需再改数据。
 */
const files = import.meta.glob('../assets/backdrops/*.{png,jpg,jpeg,webp,avif}', {
  eager: true,
  query: '?url',
  import: 'default',
}) as Record<string, string>

const bySlug = new Map<string, string>()

for (const [path, url] of Object.entries(files)) {
  const name = path.split('/').pop()?.replace(/\.(png|jpg|jpeg|webp|avif)$/i, '')
  if (name) bySlug.set(name.toLowerCase(), url)
}

export function backdropFor(slug: string): string | undefined {
  return bySlug.get(slug.toLowerCase())
}
