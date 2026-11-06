import { reactive } from 'vue'
import type { CategoryId, CategoryMeta } from '../types/guide'

/** 静态兜底数据；后端可用时会被 /api/categories 的数据就地替换（见 useCategories） */
export const CATEGORIES: CategoryMeta[] = reactive([
  { id: 'boss', name: 'BOSS 攻略', en: 'Boss', desc: '招式拆解与安全站位，按节奏打赢每一场。' },
  { id: 'dream', name: '战士之梦', en: 'Warrior Dreams', desc: '七位亡者的梦境战场，在墓碑上用梦之钉敲开。' },
  { id: 'enemy', name: '小怪图鉴', en: 'Bestiary', desc: '按地图环境划分的圣巢生物，先认环境，再认敌人。' },
  { id: 'charm', name: '护符图鉴', en: 'Charms', desc: '槽位、协同与三套常用搭配。' },
  { id: 'route', name: '流程路线', en: 'Route', desc: '从遗忘十字路到圣巢深处的推进顺序。' },
  { id: 'ending', name: '结局收集', en: 'Endings', desc: '结局条件、完成度与易漏项。' },
  { id: 'advanced', name: '进阶挑战', en: 'Advanced', desc: '神居、钢魂、苦痛之路与速通。' },
])

export function getCategory(id: string): CategoryMeta {
  return CATEGORIES.find((item) => item.id === id) ?? CATEGORIES[0]
}

export function categoryName(id: CategoryId): string {
  return getCategory(id).name
}
