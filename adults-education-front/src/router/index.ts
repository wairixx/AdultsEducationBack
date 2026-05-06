import { createRouter, createWebHistory } from 'vue-router'
import type { Role } from '@/types/api'

// Extend RouteMeta
declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    roles?: Role[]
    guestOnly?: boolean
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(_to, _from, savedPosition) {
    return savedPosition || { top: 0 }
  },
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/courses',
      name: 'courses',
      component: () => import('@/views/CourseCatalogView.vue'),
    },
    {
      path: '/courses/:id',
      name: 'course-detail',
      component: () => import('@/views/CourseDetailView.vue'),
    },
    {
      path: '/teachers/:id',
      name: 'teacher-profile',
      component: () => import('@/views/TeacherProfileView.vue'),
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/TeacherDashboardView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/courses/new',
      name: 'course-new',
      component: () => import('@/views/CourseFormView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/dashboard/courses/:id/edit',
      name: 'course-edit',
      component: () => import('@/views/CourseFormView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER', 'ADMIN'] },
    },
    {
      path: '/dashboard/courses/:id/lessons',
      name: 'course-lessons',
      component: () => import('@/views/TeacherCourseLessonsView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER', 'ADMIN'] },
    },
    {
      path: '/dashboard/students',
      name: 'teacher-students',
      component: () => import('@/views/TeacherStudentsView.vue'),
      meta: { requiresAuth: true, roles: ['TEACHER'] },
    },
    {
      path: '/admin',
      component: () => import('@/views/AdminLayoutView.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
      children: [
        {
          path: '',
          redirect: '/admin/users'
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/AdminUsersView.vue'),
        },
        {
          path: 'courses',
          name: 'admin-courses',
          component: () => import('@/views/AdminCoursesView.vue'),
        },
        {
          path: 'courses/:id/edit',
          name: 'admin-course-edit',
          component: () => import('@/views/CourseFormView.vue'),
        },
        {
          path: 'reviews',
          name: 'admin-reviews',
          component: () => import('@/views/AdminReviewsView.vue'),
        },
        {
          path: 'enrollments',
          name: 'admin-enrollments',
          component: () => import('@/views/AdminEnrollmentsView.vue'),
        }
      ]
    },
    {
      path: '/my-courses',
      name: 'my-courses',
      component: () => import('@/views/MyCoursesView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/my-courses/:id',
      name: 'learn-course',
      component: () => import('@/views/LearnCourseView.vue'),
      meta: { requiresAuth: true, roles: ['STUDENT'] },
    },
    {
      path: '/certificates/by-education/:educationId',
      name: 'certificate',
      component: () => import('@/views/CertificateView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/cert/:number',
      name: 'certificate-verify',
      component: () => import('@/views/CertificateVerifyView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/403',
      name: 'forbidden',
      component: () => import('@/views/ForbiddenView.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

// ── Auth hydration state ──────────────────────────────────────────────
let isHydrated = false
let hydrationPromise: Promise<void> | null = null

export function setHydrationPromise(promise: Promise<void>) {
  hydrationPromise = promise
}

// ── Global navigation guard ──────────────────────────────────────────
router.beforeEach(async (to) => {
  // Wait for auth store hydration before any guard logic
  if (!isHydrated && hydrationPromise) {
    await hydrationPromise
    isHydrated = true
  }

  // Lazy import to avoid circular dependency
  const { useAuthStore } = await import('@/stores/auth')
  const auth = useAuthStore()

  // Guest-only pages (login, register): redirect authenticated users to home
  if (to.meta.guestOnly && auth.isAuthenticated) {
    return { name: 'home' }
  }

  // Protected routes: redirect unauthenticated users to login
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  // Role-based guard
  if (to.meta.roles && to.meta.roles.length > 0) {
    const userRole = auth.user?.role
    if (!userRole || !to.meta.roles.includes(userRole)) {
      return { name: 'forbidden' }
    }
  }
})

export default router
