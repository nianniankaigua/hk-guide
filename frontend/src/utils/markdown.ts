import MarkdownIt from 'markdown-it'
import type { TocItem } from '../types/guide'

const md = new MarkdownIt({
  html: false,
  linkify: true,
  typographer: true,
})

function slugify(text: string): string {
  return text
    .trim()
    .toLowerCase()
    .replace(/[^\p{L}\p{N}]+/gu, '-')
    .replace(/^-+|-+$/g, '')
}

md.renderer.rules.heading_open = (tokens, idx, options, _env, self) => {
  const token = tokens[idx]
  if (token.tag === 'h2' || token.tag === 'h3') {
    const next = tokens[idx + 1]
    if (next) token.attrSet('id', slugify(next.content))
  }
  return self.renderToken(tokens, idx, options)
}

export interface RenderedGuide {
  html: string
  toc: TocItem[]
}

export function renderGuide(content: string): RenderedGuide {
  const env: Record<string, unknown> = {}
  const tokens = md.parse(content, env)
  const toc: TocItem[] = []

  for (let i = 0; i < tokens.length; i++) {
    const token = tokens[i]
    if (token.type === 'heading_open' && (token.tag === 'h2' || token.tag === 'h3')) {
      const text = tokens[i + 1]?.content ?? ''
      toc.push({
        level: Number(token.tag.slice(1)),
        text,
        id: slugify(text),
      })
    }
  }

  return { html: md.renderer.render(tokens, md.options, env), toc }
}
