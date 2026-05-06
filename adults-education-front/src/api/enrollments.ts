import { apiPost } from './client'
import type { EnrollmentRequest, EnrollmentResponse } from '@/types/api'

export function enroll(data: EnrollmentRequest): Promise<EnrollmentResponse> {
  return apiPost<EnrollmentResponse>('/api/enrollments', data)
}
