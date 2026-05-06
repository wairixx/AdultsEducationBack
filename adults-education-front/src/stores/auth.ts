import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type {
  AuthRequest,
  AuthResponse,
  RegisterRequest,
  Role,
  UserResponse,
} from '@/types/api'
import { login as apiLogin, register as apiRegister } from '@/api/auth'
import { getMyProfile } from '@/api/me'

const AUTH_TOKEN_KEY = 'auth_token'
const AUTH_USER_KEY = 'auth_user'

export const useAuthStore = defineStore('auth', () => {
  // ── State ─────────────────────────────────────────────────────────
  const token = ref<string | null>(localStorage.getItem(AUTH_TOKEN_KEY))
  const user = ref<UserResponse | null>(
    (() => {
      try {
        const raw = localStorage.getItem(AUTH_USER_KEY)
        return raw ? (JSON.parse(raw) as UserResponse) : null
      } catch {
        return null
      }
    })(),
  )

  // ── Getters ───────────────────────────────────────────────────────
  const isAuthenticated = computed(() => !!token.value)

  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isTeacher = computed(() => user.value?.role === 'TEACHER')
  const isStudent = computed(() => user.value?.role === 'STUDENT')

  function hasRole(role: Role): boolean {
    return user.value?.role === role
  }

  // ── Internal helpers ──────────────────────────────────────────────
  function persistAuth(authResponse: AuthResponse) {
    token.value = authResponse.token
    localStorage.setItem(AUTH_TOKEN_KEY, authResponse.token)
  }

  function persistUser(u: UserResponse) {
    user.value = u
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(u))
  }

  function clearAuth() {
    token.value = null
    user.value = null
    localStorage.removeItem(AUTH_TOKEN_KEY)
    localStorage.removeItem(AUTH_USER_KEY)
  }

  // ── Actions ───────────────────────────────────────────────────────
  async function login(credentials: AuthRequest) {
    const response = await apiLogin(credentials)
    persistAuth(response)
    await fetchMyProfile()
  }

  async function register(data: RegisterRequest) {
    const response = await apiRegister(data)
    persistAuth(response)
    await fetchMyProfile()
  }

  function logout() {
    clearAuth()
  }

  async function fetchMyProfile() {
    try {
      const profile = await getMyProfile()
      persistUser(profile)
    } catch {
      clearAuth()
    }
  }

  // ── Hydration ─────────────────────────────────────────────────────
  async function hydrate() {
    if (token.value) {
      await fetchMyProfile()
    }
  }

  return {
    // state
    token,
    user,
    // getters
    isAuthenticated,
    isAdmin,
    isTeacher,
    isStudent,
    hasRole,
    // actions
    login,
    register,
    logout,
    fetchMyProfile,
    hydrate,
  }
})
