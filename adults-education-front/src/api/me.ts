import { apiGet, apiPut, type CustomRequestConfig } from './client'
import type { ChangePasswordRequest, UpdateMeRequest, UserResponse } from '@/types/api'

export function getMyProfile(): Promise<UserResponse> {
  return apiGet<UserResponse>('/api/me')
}

export function updateMyProfile(data: UpdateMeRequest, config?: CustomRequestConfig): Promise<UserResponse> {
  return apiPut<UserResponse>('/api/me', data, config)
}

export function changePassword(data: ChangePasswordRequest, config?: CustomRequestConfig): Promise<void> {
  return apiPut<void>('/api/me/password', data, config)
}
