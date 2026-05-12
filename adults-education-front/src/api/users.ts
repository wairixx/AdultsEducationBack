import { apiGet, apiPut, apiDelete, type CustomRequestConfig } from './client'
import type { Page, UpdateUserRequest, UserFilter, UserResponse } from '@/types/api'

export function getUserById(id: number): Promise<UserResponse> {
  return apiGet<UserResponse>(`/api/users/${id}`)
}

export function getAllUsers(
  filter?: UserFilter,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<UserResponse>> {
  return apiGet<Page<UserResponse>>('/api/users', {
    params: { ...filter, page, size, sort },
  })
}

export function updateUser(id: number, data: UpdateUserRequest, config?: CustomRequestConfig): Promise<UserResponse> {
  return apiPut<UserResponse>(`/api/users/${id}`, data, config)
}

export function deleteUser(id: number, config?: CustomRequestConfig): Promise<void> {
  return apiDelete(`/api/users/${id}`, config)
}

/** Public endpoint — no auth required */
export function getPublicTeacher(id: number): Promise<UserResponse> {
  return apiGet<UserResponse>(`/api/public/teachers/${id}`)
}
