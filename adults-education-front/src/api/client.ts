import axios, { type AxiosRequestConfig } from 'axios'
import { toast } from 'vue-sonner'

const AUTH_TOKEN_KEY = 'auth_token'
const LOCALE_KEY = 'locale'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
})

// ── Request interceptor ───────────────────────────────────────────────
apiClient.interceptors.request.use((config) => {
  // Attach JWT token
  const token = localStorage.getItem(AUTH_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  // Attach locale
  const locale = localStorage.getItem(LOCALE_KEY) || 'uk'
  config.headers['Accept-Language'] = locale

  return config
})

// ── Response interceptor ──────────────────────────────────────────────
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error) && error.response) {
      const status = error.response.status

      if (status === 401) {
        // Don't redirect for auth endpoints — let the login/register page handle it
        const url = error.config?.url || ''
        if (!url.startsWith('/api/auth')) {
          // Clear auth state
          localStorage.removeItem(AUTH_TOKEN_KEY)

          // Redirect to login (avoid redirect loop)
          if (window.location.pathname !== '/login') {
            window.location.href = '/login'
          }
        }
      }

      if (status === 403) {
        const message =
          (error.response.data as { message?: string })?.message || 'Access denied'
        toast.error(message)
      } else if (status !== 401 && error.config?.method && error.config.method.toLowerCase() !== 'get') {
        // Show toast for mutations (POST, PUT, PATCH, DELETE) unless skipToast is true
        const skipToast = (error.config as any)?.skipToast
        if (!skipToast) {
          const message =
            (error.response.data as { message?: string })?.message || 'An error occurred'
          toast.error(message)
        }
      }
    } else if (axios.isAxiosError(error) && !error.response) {
      toast.error('Network error or server is down')
    }

    return Promise.reject(error)
  },
)

export interface CustomRequestConfig extends AxiosRequestConfig {
  skipToast?: boolean
}

// ── Typed helper methods ──────────────────────────────────────────────
export async function apiGet<T>(url: string, config?: CustomRequestConfig): Promise<T> {
  const response = await apiClient.get<T>(url, config)
  return response.data
}

export async function apiPost<T>(
  url: string,
  data?: unknown,
  config?: CustomRequestConfig,
): Promise<T> {
  const response = await apiClient.post<T>(url, data, config)
  return response.data
}

export async function apiPut<T>(
  url: string,
  data?: unknown,
  config?: CustomRequestConfig,
): Promise<T> {
  const response = await apiClient.put<T>(url, data, config)
  return response.data
}

export async function apiPatch<T>(
  url: string,
  data?: unknown,
  config?: CustomRequestConfig,
): Promise<T> {
  const response = await apiClient.patch<T>(url, data, config)
  return response.data
}

export async function apiDelete<T = void>(
  url: string,
  config?: CustomRequestConfig,
): Promise<T> {
  const response = await apiClient.delete<T>(url, config)
  return response.data
}

export default apiClient
