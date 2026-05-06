import { apiGet, apiPut } from './client'
import type { ChangePasswordRequest, UpdateMeRequest, UserResponse } from '@/types/api'

export function getMyProfile(): Promise<UserResponse> {
  return apiGet<UserResponse>('/api/me')
}

export function updateMyProfile(data: UpdateMeRequest): Promise<UserResponse> {
  return apiPut<UserResponse>('/api/me', data)
}

export function changePassword(data: ChangePasswordRequest): Promise<void> {
  return apiPut<void>('/api/me/password', data)
}
