<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import type { CourseResponse } from '@/types/api'
import { getAllCourses } from '@/api/courses'
import { getPublicStats } from '@/api/stats'

const { t } = useI18n()

// Featured courses
const courses = ref<CourseResponse[]>([])
const coursesLoading = ref(true)

// Statistics from API
const stats = reactive([
  { key: 'courses', icon: 'mdi:book-open-variant', value: 0 },
  { key: 'students', icon: 'mdi:account-group', value: 0 },
  { key: 'teachers', icon: 'mdi:school', value: 0 },
  { key: 'certificates', icon: 'mdi:certificate', value: 0 },
])

function animateCounter(target: number, index: number) {
  const duration = 1200
  const steps = 40
  const stepTime = duration / steps
  const increment = target / steps
  let current = 0
  const timer = setInterval(() => {
    current += increment
    if (current >= target) {
      const s = stats[index]
      if (s) s.value = target
      clearInterval(timer)
    } else {
      const s = stats[index]
      if (s) s.value = Math.floor(current)
    }
  }, stepTime)
}

onMounted(async () => {
  // Fetch courses and stats in parallel
  const coursesPromise = getAllCourses(undefined, 0, 6, 'id,desc')
    .then((page) => { courses.value = page.content })
    .catch(() => { /* Silently fail — landing still renders fine without courses */ })
    .finally(() => { coursesLoading.value = false })

  const statsPromise = getPublicStats()
    .then((data) => {
      const mapping: Record<string, number> = {
        courses: data.coursesCount,
        students: data.studentsCount,
        teachers: data.teachersCount,
        certificates: data.certificatesIssued,
      }
      stats.forEach((s, i) => {
        animateCounter(mapping[s.key] ?? 0, i)
      })
    })
    .catch(() => { /* Stats stay at 0 */ })

  await Promise.all([coursesPromise, statsPromise])
})

// Topic color map for badges
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

function formatPrice(price: number): string {
  if (price === 0) return t('course.free')
  return `${price.toLocaleString()} ${t('course.price')}`
}

function renderStars(rating: number): number[] {
  return Array.from({ length: 5 }, (_, i) => (i < Math.round(rating) ? 1 : 0))
}
</script>

<template>
  <DefaultLayout>
    <!-- ═══ Hero ═══ -->
    <section class="relative overflow-hidden bg-gradient-to-br from-amber-500 via-amber-600 to-violet-700">
      <!-- Decorative shapes -->
      <div class="absolute -left-20 -top-20 h-72 w-72 rounded-full bg-white/10 blur-3xl"></div>
      <div class="absolute -bottom-20 -right-20 h-96 w-96 rounded-full bg-violet-500/20 blur-3xl"></div>
      <div class="absolute left-1/2 top-1/2 h-64 w-64 -translate-x-1/2 -translate-y-1/2 rounded-full bg-amber-400/20 blur-2xl"></div>

      <div class="relative mx-auto max-w-7xl px-4 py-20 sm:px-6 sm:py-28 lg:px-8 lg:py-36">
        <div class="mx-auto max-w-3xl text-center">
          <h1 class="text-4xl font-bold leading-tight tracking-tight text-white sm:text-5xl lg:text-6xl">
            {{ t('pages.home.heroHeadline') }}
          </h1>
          <p class="mt-6 text-lg leading-relaxed text-amber-100 sm:text-xl">
            {{ t('pages.home.heroSubheadline') }}
          </p>
          <div class="mt-10 flex flex-col items-center gap-4 sm:flex-row sm:justify-center">
            <RouterLink
              to="/courses"
              class="inline-flex items-center gap-2 rounded-xl bg-white px-8 py-3.5 text-sm font-semibold text-amber-700 shadow-lg transition-all duration-200 hover:-translate-y-0.5 hover:shadow-xl"
            >
              <Icon icon="mdi:compass-outline" class="h-5 w-5" />
              {{ t('actions.browseCourses') }}
            </RouterLink>
            <RouterLink
              to="/register?role=teacher"
              class="inline-flex items-center gap-2 rounded-xl border-2 border-white/30 bg-white/10 px-8 py-3.5 text-sm font-semibold text-white backdrop-blur-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-white/50 hover:bg-white/20"
            >
              <Icon icon="mdi:school-outline" class="h-5 w-5" />
              {{ t('actions.becomeTeacher') }}
            </RouterLink>
          </div>
        </div>
      </div>
    </section>

    <!-- ═══ Stats ═══ -->
    <section class="relative -mt-12 z-10 mx-auto max-w-5xl px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-2 gap-4 sm:grid-cols-4 sm:gap-6">
        <div
          v-for="stat in stats"
          :key="stat.key"
          class="rounded-2xl border border-slate-100 bg-white p-5 text-center shadow-lg transition-all duration-200 hover:-translate-y-1 hover:shadow-xl sm:p-6"
        >
          <div class="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-amber-50 text-amber-600">
            <Icon :icon="stat.icon" class="h-6 w-6" />
          </div>
          <p class="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
            {{ stat.value }}
          </p>
          <p class="mt-1 text-sm text-slate-500">
            {{ t(`pages.home.stats.${stat.key}`) }}
          </p>
        </div>
      </div>
    </section>

    <!-- ═══ Featured Courses ═══ -->
    <section class="mx-auto max-w-7xl px-4 py-20 sm:px-6 lg:px-8">
      <div class="mb-12 text-center">
        <h2 class="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
          {{ t('pages.home.featuredTitle') }}
        </h2>
        <p class="mt-3 text-lg text-slate-600">
          {{ t('pages.home.featuredSubtitle') }}
        </p>
      </div>

      <!-- Loading skeleton -->
      <div v-if="coursesLoading" class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        <div
          v-for="i in 6"
          :key="i"
          class="animate-pulse rounded-2xl border border-slate-100 bg-white p-0 shadow-sm"
        >
          <div class="h-44 rounded-t-2xl bg-slate-200"></div>
          <div class="p-5">
            <div class="mb-3 h-4 w-20 rounded bg-slate-200"></div>
            <div class="mb-2 h-5 w-3/4 rounded bg-slate-200"></div>
            <div class="mb-4 h-4 w-1/2 rounded bg-slate-200"></div>
            <div class="flex justify-between">
              <div class="h-4 w-16 rounded bg-slate-200"></div>
              <div class="h-4 w-12 rounded bg-slate-200"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Course cards -->
      <div
        v-else-if="courses.length > 0"
        class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3"
      >
        <RouterLink
          v-for="course in courses"
          :key="course.id"
          :to="`/courses/${course.id}`"
          class="group overflow-hidden rounded-2xl border border-slate-100 bg-white shadow-sm transition-all duration-300 hover:-translate-y-1 hover:shadow-xl"
        >
          <!-- Cover image -->
          <div class="relative h-44 overflow-hidden">
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
              <Icon icon="mdi:book-open-variant" class="h-16 w-16 text-white/60" />
            </div>
            <!-- Price badge -->
            <div class="absolute right-3 top-3 rounded-full bg-white/90 px-3 py-1 text-sm font-semibold shadow-sm backdrop-blur-sm"
              :class="course.price === 0 ? 'text-emerald-600' : 'text-slate-900'"
            >
              {{ formatPrice(course.price) }}
            </div>
          </div>

          <!-- Content -->
          <div class="p-5">
            <!-- Topic badge -->
            <span
              class="inline-block rounded-full px-3 py-1 text-xs font-medium"
              :class="topicColors[course.topic] || 'bg-slate-100 text-slate-700'"
            >
              {{ t(`topics.${course.topic}`) }}
            </span>

            <h3 class="mt-3 text-base font-semibold text-slate-900 line-clamp-2 group-hover:text-amber-700 transition-colors duration-200">
              {{ course.title }}
            </h3>

            <p class="mt-1.5 flex items-center gap-1 text-sm text-slate-500">
              <Icon icon="mdi:account-outline" class="h-3.5 w-3.5" />
              {{ course.teacherFullName }}
            </p>

            <!-- Footer: rating + meta -->
            <div class="mt-4 flex items-center justify-between border-t border-slate-100 pt-3">
              <div class="flex items-center gap-1">
                <template v-if="course.averageRating > 0">
                  <div class="flex">
                    <Icon
                      v-for="(star, idx) in renderStars(course.averageRating)"
                      :key="idx"
                      :icon="star ? 'mdi:star' : 'mdi:star-outline'"
                      class="h-4 w-4"
                      :class="star ? 'text-amber-400' : 'text-slate-300'"
                    />
                  </div>
                  <span class="ml-1 text-xs font-medium text-slate-600">
                    {{ course.averageRating.toFixed(1) }}
                  </span>
                </template>
                <span v-else class="text-xs text-slate-400">
                  {{ t('course.noRating') }}
                </span>
              </div>
              <div class="flex items-center gap-3 text-xs text-slate-500">
                <span class="flex items-center gap-1">
                  <Icon icon="mdi:clock-outline" class="h-3.5 w-3.5" />
                  {{ course.durationHours }} {{ t('course.hours') }}
                </span>
                <span class="flex items-center gap-1">
                  <Icon icon="mdi:account-group-outline" class="h-3.5 w-3.5" />
                  {{ course.studentsCount }}
                </span>
              </div>
            </div>
          </div>
        </RouterLink>
      </div>

      <!-- Empty state -->
      <div v-else class="rounded-2xl border border-dashed border-slate-200 bg-white p-12 text-center">
        <Icon icon="mdi:book-open-page-variant-outline" class="mx-auto h-16 w-16 text-slate-300" />
        <p class="mt-4 text-lg font-medium text-slate-500">
          {{ t('pages.home.featuredSubtitle') }}
        </p>
      </div>

      <!-- View all button -->
      <div v-if="courses.length > 0" class="mt-10 text-center">
        <RouterLink
          to="/courses"
          class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-6 py-3 text-sm font-medium text-slate-700 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-amber-200 hover:shadow-md"
        >
          {{ t('actions.browseCourses') }}
          <Icon icon="mdi:arrow-right" class="h-4 w-4" />
        </RouterLink>
      </div>
    </section>

    <!-- ═══ How It Works ═══ -->
    <section class="bg-white py-20">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="mb-14 text-center">
          <h2 class="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
            {{ t('pages.home.howItWorks.title') }}
          </h2>
          <p class="mt-3 text-lg text-slate-600">
            {{ t('pages.home.howItWorks.subtitle') }}
          </p>
        </div>

        <div class="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-4">
          <!-- Step 1 -->
          <div class="group relative text-center">
            <div class="mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-2xl bg-amber-50 text-amber-600 shadow-sm transition-all duration-200 group-hover:bg-amber-100 group-hover:shadow-md">
              <Icon icon="mdi:account-plus-outline" class="h-8 w-8" />
            </div>
            <span class="absolute -top-2 left-1/2 -translate-x-1/2 rounded-full bg-amber-500 px-2.5 py-0.5 text-xs font-bold text-white">
              1
            </span>
            <h3 class="text-base font-semibold text-slate-900">
              {{ t('pages.home.howItWorks.step1Title') }}
            </h3>
            <p class="mt-2 text-sm leading-relaxed text-slate-600">
              {{ t('pages.home.howItWorks.step1Desc') }}
            </p>
          </div>

          <!-- Step 2 -->
          <div class="group relative text-center">
            <div class="mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-2xl bg-violet-50 text-violet-600 shadow-sm transition-all duration-200 group-hover:bg-violet-100 group-hover:shadow-md">
              <Icon icon="mdi:compass-outline" class="h-8 w-8" />
            </div>
            <span class="absolute -top-2 left-1/2 -translate-x-1/2 rounded-full bg-violet-600 px-2.5 py-0.5 text-xs font-bold text-white">
              2
            </span>
            <h3 class="text-base font-semibold text-slate-900">
              {{ t('pages.home.howItWorks.step2Title') }}
            </h3>
            <p class="mt-2 text-sm leading-relaxed text-slate-600">
              {{ t('pages.home.howItWorks.step2Desc') }}
            </p>
          </div>

          <!-- Step 3 -->
          <div class="group relative text-center">
            <div class="mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-2xl bg-emerald-50 text-emerald-600 shadow-sm transition-all duration-200 group-hover:bg-emerald-100 group-hover:shadow-md">
              <Icon icon="mdi:play-circle-outline" class="h-8 w-8" />
            </div>
            <span class="absolute -top-2 left-1/2 -translate-x-1/2 rounded-full bg-emerald-500 px-2.5 py-0.5 text-xs font-bold text-white">
              3
            </span>
            <h3 class="text-base font-semibold text-slate-900">
              {{ t('pages.home.howItWorks.step3Title') }}
            </h3>
            <p class="mt-2 text-sm leading-relaxed text-slate-600">
              {{ t('pages.home.howItWorks.step3Desc') }}
            </p>
          </div>

          <!-- Step 4 -->
          <div class="group relative text-center">
            <div class="mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-2xl bg-amber-50 text-amber-600 shadow-sm transition-all duration-200 group-hover:bg-amber-100 group-hover:shadow-md">
              <Icon icon="mdi:certificate-outline" class="h-8 w-8" />
            </div>
            <span class="absolute -top-2 left-1/2 -translate-x-1/2 rounded-full bg-gradient-to-r from-amber-400 to-amber-600 px-2.5 py-0.5 text-xs font-bold text-white">
              4
            </span>
            <h3 class="text-base font-semibold text-slate-900">
              {{ t('pages.home.howItWorks.step4Title') }}
            </h3>
            <p class="mt-2 text-sm leading-relaxed text-slate-600">
              {{ t('pages.home.howItWorks.step4Desc') }}
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- ═══ Testimonials ═══ -->
    <section class="bg-slate-50 py-20">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="mb-14 text-center">
          <h2 class="text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
            {{ t('pages.home.testimonials.title') }}
          </h2>
          <p class="mt-3 text-lg text-slate-600">
            {{ t('pages.home.testimonials.subtitle') }}
          </p>
        </div>

        <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <!-- Testimonial 1 -->
          <div class="rounded-2xl border border-slate-100 bg-white p-6 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:shadow-lg">
            <div class="mb-4 flex text-amber-400">
              <Icon v-for="i in 5" :key="i" icon="mdi:star" class="h-5 w-5" />
            </div>
            <p class="text-sm leading-relaxed text-slate-600 italic">
              "{{ t('pages.home.testimonials.t1Text') }}"
            </p>
            <div class="mt-5 flex items-center gap-3 border-t border-slate-100 pt-5">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-pink-400 to-rose-500 text-sm font-bold text-white">
                ОК
              </div>
              <div>
                <p class="text-sm font-semibold text-slate-900">
                  {{ t('pages.home.testimonials.t1Name') }}
                </p>
                <p class="text-xs text-slate-500">
                  {{ t('pages.home.testimonials.t1Role') }}
                </p>
              </div>
            </div>
          </div>

          <!-- Testimonial 2 -->
          <div class="rounded-2xl border border-slate-100 bg-white p-6 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:shadow-lg">
            <div class="mb-4 flex text-amber-400">
              <Icon v-for="i in 5" :key="i" icon="mdi:star" class="h-5 w-5" />
            </div>
            <p class="text-sm leading-relaxed text-slate-600 italic">
              "{{ t('pages.home.testimonials.t2Text') }}"
            </p>
            <div class="mt-5 flex items-center gap-3 border-t border-slate-100 pt-5">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-blue-400 to-indigo-500 text-sm font-bold text-white">
                ІП
              </div>
              <div>
                <p class="text-sm font-semibold text-slate-900">
                  {{ t('pages.home.testimonials.t2Name') }}
                </p>
                <p class="text-xs text-slate-500">
                  {{ t('pages.home.testimonials.t2Role') }}
                </p>
              </div>
            </div>
          </div>

          <!-- Testimonial 3 -->
          <div class="rounded-2xl border border-slate-100 bg-white p-6 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:shadow-lg sm:col-span-2 lg:col-span-1">
            <div class="mb-4 flex text-amber-400">
              <Icon v-for="i in 5" :key="i" icon="mdi:star" class="h-5 w-5" />
            </div>
            <p class="text-sm leading-relaxed text-slate-600 italic">
              "{{ t('pages.home.testimonials.t3Text') }}"
            </p>
            <div class="mt-5 flex items-center gap-3 border-t border-slate-100 pt-5">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-violet-400 to-purple-500 text-sm font-bold text-white">
                МШ
              </div>
              <div>
                <p class="text-sm font-semibold text-slate-900">
                  {{ t('pages.home.testimonials.t3Name') }}
                </p>
                <p class="text-xs text-slate-500">
                  {{ t('pages.home.testimonials.t3Role') }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ═══ CTA Banner ═══ -->
    <section class="relative overflow-hidden bg-gradient-to-r from-amber-500 to-violet-600 py-16">
      <div class="absolute -left-16 -top-16 h-64 w-64 rounded-full bg-white/10 blur-3xl"></div>
      <div class="absolute -bottom-16 -right-16 h-64 w-64 rounded-full bg-violet-500/20 blur-3xl"></div>

      <div class="relative mx-auto max-w-4xl px-4 text-center sm:px-6 lg:px-8">
        <h2 class="text-3xl font-bold tracking-tight text-white sm:text-4xl">
          {{ t('pages.home.ctaTitle') }}
        </h2>
        <p class="mt-4 text-lg text-amber-100">
          {{ t('pages.home.ctaSubtitle') }}
        </p>
        <div class="mt-8">
          <RouterLink
            to="/register"
            class="inline-flex items-center gap-2 rounded-xl bg-white px-8 py-3.5 text-sm font-semibold text-amber-700 shadow-lg transition-all duration-200 hover:-translate-y-0.5 hover:shadow-xl"
          >
            <Icon icon="mdi:rocket-launch-outline" class="h-5 w-5" />
            {{ t('actions.joinToday') }}
          </RouterLink>
        </div>
      </div>
    </section>
  </DefaultLayout>
</template>
