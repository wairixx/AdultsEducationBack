import { apiPost, type CustomRequestConfig } from './client'
import type { AuthRequest, AuthResponse, RegisterRequest } from '@/types/api'

export function login(data: AuthRequest, config?: CustomRequestConfig): Promise<AuthResponse> {
  return apiPost<AuthResponse>('/api/auth/login', data, config)
}

export function register(data: RegisterRequest, config?: CustomRequestConfig): Promise<AuthResponse> {
  return apiPost<AuthResponse>('/api/auth/register', data, config)
}
