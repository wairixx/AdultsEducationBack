import { apiPost, type CustomRequestConfig } from './client'
import type { EnrollmentRequest, EnrollmentResponse } from '@/types/api'

export function enroll(data: EnrollmentRequest, config?: CustomRequestConfig): Promise<EnrollmentResponse> {
  return apiPost<EnrollmentResponse>('/api/enrollments', data, config)
}
