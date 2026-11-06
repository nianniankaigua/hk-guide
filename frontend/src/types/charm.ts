export interface Charm {
  /** 游戏内护符序号 */
  id: number
  slug: string
  name: string
  en: string
  /** 占用护符槽 */
  notches: number
  /** 效果 */
  effect: string
  /** 游戏内原文描述 */
  lore: string
  /** 获取方式 */
  location: string
  /** 使用建议 */
  notes: string
  /** 图标路径 */
  icon: string
  /** 组合搭配（slug），后端可能返回 null */
  synergy?: string[] | null
  /** 搭配说明 */
  synergyNote?: string | null
}
