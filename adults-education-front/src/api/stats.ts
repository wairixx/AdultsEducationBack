import { apiGet, type CustomRequestConfig } from './client'

export interface PublicStats {
  coursesCount: number
  studentsCount: number
  teachersCount: number
  certificatesIssued: number
}

export function getPublicStats(): Promise<PublicStats> {
  return apiGet<PublicStats>('/api/stats/public')
}
