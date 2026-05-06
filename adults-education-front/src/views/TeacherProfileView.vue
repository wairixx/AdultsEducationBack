<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppCard from '@/components/common/AppCard.vue'
import { getPublicTeacher } from '@/api/users'
import { getAllCourses } from '@/api/courses'
import type { UserResponse, CourseResponse } from '@/types/api'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const teacherId = Number(route.params.id)
const teacher = ref<UserResponse | null>(null)
const courses = ref<CourseResponse[]>([])
const loading = ref(true)

const topicColors: Record<string, string> = {
  PROGRAMMING: 'bg-blue-100 text-blue-700',
  DESIGN: 'bg-pink-100 text-pink-700',
  MARKETING: 'bg-orange-100 text-orange-700',
  BUSINESS: 'bg-emerald-100 text-emerald-700',
  LANGUAGES: 'bg-violet-100 text-violet-700',
  SCIENCE: 'bg-cyan-100 text-cyan-700',
  ARTS: 'bg-rose-100 text-rose-700',
  HEALTH: 'bg-green-100 text-green-700',
  FINANCE: 'bg-amber-100 text-amber-700',
  OTHER: 'bg-slate-100 text-slate-700',
}

async function loadData() {
  loading.value = true
  try {
    const [t, c] = await Promise.all([
      getPublicTeacher(teacherId),
      getAllCourses({ teacherId }, 0, 50, 'createdAt,desc'),
    ])
    teacher.value = t
    courses.value = c.content
  } catch {
    router.replace('/404')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function initials(name?: string): string {
  if (!name) return '?'
  return name.split(' ').map(w => w.charAt(0)).join('').toUpperCase().slice(0, 2)
}

function formatPrice(price: number): string {
  if (price === 0) return t('course.free')
  return `${price.toLocaleString()} ${t('course.price')}`
}

function getAge(birthDate?: string): number | null {
  if (!birthDate) return null
  const birth = new Date(`${birthDate}T00:00:00`)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  const dayDiff = today.getDate() - birth.getDate()
  if (monthDiff < 0 || (monthDiff === 0 && dayDiff < 0)) age -= 1
  return age
}
</script>

<template>
  <DefaultLayout>
    <div v-if="loading" class="flex min-h-[60vh] items-center justify-center">
      <div class="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
    </div>

    <template v-else-if="teacher">
      <!-- Hero Section -->
      <div class="relative bg-gradient-to-r from-slate-900 via-slate-800 to-violet-900 py-16">
        <div class="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-amber-500/10 via-transparent to-transparent"></div>
        <div class="relative mx-auto max-w-5xl px-4 sm:px-6 lg:px-8">
          <div class="flex flex-col items-center gap-6 sm:flex-row sm:items-start sm:gap-8">
            <!-- Avatar -->
            <div class="relative">
              <img
                v-if="teacher.avatarUrl"
                :src="teacher.avatarUrl"
                :alt="teacher.fullName"
                class="h-28 w-28 rounded-2xl border-4 border-white/20 object-cover shadow-xl sm:h-32 sm:w-32"
              />
              <div
                v-else
                class="flex h-28 w-28 items-center justify-center rounded-2xl border-4 border-white/20 bg-gradient-to-br from-amber-400 to-amber-600 text-3xl font-bold text-white shadow-xl sm:h-32 sm:w-32"
              >
                {{ initials(teacher.fullName) }}
              </div>
              <div class="absolute -bottom-2 -right-2 rounded-full bg-amber-500 p-1.5 text-white shadow-md">
                <Icon icon="mdi:school" class="h-4 w-4" />
              </div>
            </div>

            <!-- Info -->
            <div class="text-center sm:text-left">
              <span class="inline-block rounded-full bg-amber-500/20 px-3 py-1 text-xs font-semibold text-amber-400 ring-1 ring-inset ring-amber-500/30 mb-3">
                {{ t('roles.TEACHER') }}
              </span>
              <h1 class="text-3xl font-bold tracking-tight text-white sm:text-4xl">
                {{ teacher.fullName }}
              </h1>
              <p v-if="teacher.specialization" class="mt-2 text-lg text-slate-300">
                {{ teacher.specialization }}
              </p>
              <div class="mt-4 flex flex-wrap items-center justify-center gap-4 text-sm text-slate-400 sm:justify-start">
                <span v-if="teacher.experienceYears" class="flex items-center gap-1.5">
                  <Icon icon="mdi:briefcase-outline" class="h-4 w-4" />
                  {{ teacher.experienceYears }} {{ t('teacherProfile.yearsExp') }}
                </span>
                <span v-if="teacher.birthDate" class="flex items-center gap-1.5">
                  <Icon icon="mdi:calendar-outline" class="h-4 w-4" />
                  {{ new Date(teacher.birthDate).toLocaleDateString() }}
                </span>
                <span v-if="getAge(teacher.birthDate) !== null" class="flex items-center gap-1.5">
                  <Icon icon="mdi:account-clock-outline" class="h-4 w-4" />
                  {{ getAge(teacher.birthDate) }} {{ t('teacherProfile.yearsOld') }}
                </span>
                <span class="flex items-center gap-1.5">
                  <Icon icon="mdi:book-open-variant" class="h-4 w-4" />
                  {{ courses.length }} {{ t('teacherProfile.coursesCount') }}
                </span>
                <span v-if="teacher.email" class="flex items-center gap-1.5">
                  <Icon icon="mdi:email-outline" class="h-4 w-4" />
                  {{ teacher.email }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="bg-slate-50 py-12">
        <div class="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8">
          <!-- Bio -->
          <div v-if="teacher.bio" class="mb-10">
            <AppCard>
              <template #header>
                <h2 class="text-lg font-bold text-slate-900">{{ t('teacherProfile.about') }}</h2>
              </template>
              <p class="whitespace-pre-line text-sm leading-relaxed text-slate-600">{{ teacher.bio }}</p>
            </AppCard>
          </div>

          <!-- Courses -->
          <div>
            <h2 class="mb-6 text-xl font-bold tracking-tight text-slate-900">
              {{ t('teacherProfile.courses') }}
            </h2>

            <div v-if="courses.length > 0" class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <RouterLink
                v-for="course in courses"
                :key="course.id"
                :to="`/courses/${course.id}`"
                class="group overflow-hidden rounded-2xl border border-slate-100 bg-white shadow-sm transition-all duration-300 hover:-translate-y-1 hover:shadow-xl"
              >
                <!-- Cover -->
                <div class="relative h-40 overflow-hidden">
                  <img
                    v-if="course.coverUrl"
                    :src="course.coverUrl"
                    :alt="course.title"
                    class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
                  />
                  <div
                    v-else
                    class="flex h-full w-full items-center justify-center bg-gradient-to-br from-amber-400 via-amber-500 to-violet-600"
                  >
                    <Icon icon="mdi:book-open-variant" class="h-12 w-12 text-white/60" />
                  </div>
                  <div
                    class="absolute right-3 top-3 rounded-full bg-white/90 px-3 py-1 text-sm font-semibold shadow-sm backdrop-blur-sm"
                    :class="course.price === 0 ? 'text-emerald-600' : 'text-slate-900'"
                  >
                    {{ formatPrice(course.price) }}
                  </div>
                </div>

                <!-- Content -->
                <div class="p-5">
                  <span
                    class="inline-block rounded-full px-3 py-1 text-xs font-medium"
                    :class="topicColors[course.topic] || 'bg-slate-100 text-slate-700'"
                  >
                    {{ t(`topics.${course.topic}`) }}
                  </span>
                  <h3 class="mt-2 text-base font-semibold text-slate-900 line-clamp-2 group-hover:text-amber-700 transition-colors">
                    {{ course.title }}
                  </h3>
                  <div class="mt-3 flex items-center justify-between text-xs text-slate-500">
                    <span class="flex items-center gap-1">
                      <Icon icon="mdi:clock-outline" class="h-3.5 w-3.5" />
                      {{ course.durationHours }} {{ t('course.hours') }}
                    </span>
                    <span class="flex items-center gap-1">
                      <Icon icon="mdi:account-group-outline" class="h-3.5 w-3.5" />
                      {{ course.studentsCount }}
                    </span>
                    <span v-if="course.averageRating > 0" class="flex items-center gap-1 text-amber-500">
                      <Icon icon="mdi:star" class="h-3.5 w-3.5" />
                      {{ course.averageRating.toFixed(1) }}
                    </span>
                  </div>
                </div>
              </RouterLink>
            </div>

            <div v-else class="rounded-2xl border border-dashed border-slate-200 bg-white p-12 text-center">
              <Icon icon="mdi:book-open-page-variant-outline" class="mx-auto h-16 w-16 text-slate-300" />
              <p class="mt-4 text-lg font-medium text-slate-500">{{ t('teacherProfile.noCourses') }}</p>
            </div>
          </div>
        </div>
      </div>
    </template>
  </DefaultLayout>
</template>
