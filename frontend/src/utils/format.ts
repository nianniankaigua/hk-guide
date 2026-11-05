export function formatCount(value: number): string {
  if (value >= 10000) {
    const wan = value / 10000
    return `${wan.toFixed(wan >= 10 ? 0 : 1)} 万`
  }
  return String(value)
}

export function formatDate(value: string): string {
  return value.replace(/-/g, '.')
}
