import { apiPost } from './client'
import type { AuthRequest, AuthResponse, RegisterRequest } from '@/types/api'

export function login(data: AuthRequest): Promise<AuthResponse> {
  return apiPost<AuthResponse>('/api/auth/login', data)
}

export function register(data: RegisterRequest): Promise<AuthResponse> {
  return apiPost<AuthResponse>('/api/auth/register', data)
}
