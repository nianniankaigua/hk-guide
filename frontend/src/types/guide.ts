export type CategoryId = 'boss' | 'charm' | 'route' | 'ending' | 'advanced' | 'enemy' | 'dream'

export interface GuideTheme {
  accent: string
}

export interface Guide {
  id: number
  slug: string
  title: string
  subtitle: string
  category: CategoryId
  /** 小怪所属地图环境，用于按区域分组 */
  region?: string
  summary: string
  content: string
  tags: string[]
  /** 卡片上的大字符水印，用来做水墨感的封面 */
  watermark: string
  /** 从游戏资源中提取的 BOSS 立绘，放在 public/covers 下 */
  cover?: string
  /** 场景截图，用作卡片与页面背景；默认按 slug 自动匹配 src/assets/backdrops */
  backdrop?: string
  /** 每个 BOSS 的专属强调色 */
  theme?: GuideTheme
  /** 难度 1 - 5，0 表示非战斗类 */
  difficulty: number
  views: number
  likes: number
  readMinutes: number
  updatedAt: string
  featured?: boolean
}

export interface CategoryMeta {
  id: CategoryId
  name: string
  en: string
  desc: string
}

export interface TocItem {
  level: number
  text: string
  id: string
}
