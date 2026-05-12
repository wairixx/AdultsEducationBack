<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppCard from '@/components/common/AppCard.vue'
import StarRating from '@/components/review/StarRating.vue'
import ReviewsList from '@/components/review/ReviewsList.vue'
import EnrollModal from '@/components/course/EnrollModal.vue'
import { getCourseById } from '@/api/courses'
import { getLessonsPreview } from '@/api/lessons'
import { getMyEducationsAsStudent } from '@/api/educations'
import { useAuthStore } from '@/stores/auth'
import type { CourseResponse, LessonPreviewResponse, EducationResponse } from '@/types/api'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const auth = useAuthStore()

const courseId = Number(route.params.id)

const course = ref<CourseResponse | null>(null)
const lessons = ref<LessonPreviewResponse[]>([])
const myEducation = ref<EducationResponse | null>(null)
const loading = ref(true)
const enrollModalOpen = ref(false)

const activeTab = ref<'about' | 'lessons' | 'reviews'>('about')

async function loadData() {
  loading.value = true
  try {
    const [cRes, lRes] = await Promise.all([
      getCourseById(courseId),
      getLessonsPreview(courseId)
    ])
    course.value = cRes
    lessons.value = lRes

    if (auth.isStudent) {
      const edRes = await getMyEducationsAsStudent({ courseId }, 0, 1)
      if (edRes.totalElements > 0) {
        myEducation.value = edRes.content[0] as EducationResponse
      }
    }
  } catch {
    router.replace('/404')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

const enrollButtonData = computed(() => {
  if (!auth.isAuthenticated) {
    return { text: t('course.loginToEnroll'), action: () => router.push(`/login?redirect=/courses/${courseId}`), variant: 'secondary' as const }
  }
  if (auth.isStudent) {
    if (myEducation.value) {
      return { text: t('course.goToLearning'), action: () => router.push(`/my-courses/${myEducation.value!.id}`), variant: 'primary' as const }
    }
    return { text: t('course.enrollNow'), action: () => enrollModalOpen.value = true, variant: 'primary' as const }
  }
  if (auth.isTeacher && course.value?.teacherId === auth.user?.id) {
    return { text: t('actions.edit'), action: () => router.push(`/dashboard/courses/${courseId}/edit`), variant: 'secondary' as const }
  }
  if (auth.isAdmin) {
    return { text: t('actions.edit'), action: () => router.push(`/admin/courses/${courseId}/edit`), variant: 'secondary' as const }
  }
  return null // Hidden for teachers who aren't owners
})

function onEnrollSuccess(educationId: number) {
  enrollModalOpen.value = false
  router.push(`/my-courses/${educationId}`)
}
</script>

<template>
  <DefaultLayout>
    <div v-if="loading" class="flex min-h-[60vh] items-center justify-center">
      <div class="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
    </div>

    <template v-else-if="course">
      <!-- Hero Section -->
      <div class="relative bg-slate-900 py-16 sm:py-24">
        <!-- Background Cover -->
        <div class="absolute inset-0 overflow-hidden">
          <img
            v-if="course.coverUrl"
            :src="course.coverUrl"
            alt=""
            class="h-full w-full object-cover opacity-20"
          />
          <div v-else class="h-full w-full bg-gradient-to-br from-amber-900/40 to-violet-900/40 opacity-50"></div>
          <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-[2px]"></div>
        </div>

        <div class="relative mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div class="grid grid-cols-1 gap-12 lg:grid-cols-3">
            <!-- Hero text -->
            <div class="lg:col-span-2 text-white">
              <div class="flex flex-wrap items-center gap-3">
                <span class="rounded-full bg-amber-500/20 px-3 py-1 text-sm font-semibold text-amber-400 ring-1 ring-inset ring-amber-500/30">
                  {{ t(`topics.${course.topic}`) }}
                </span>
                <span class="rounded-full bg-violet-500/20 px-3 py-1 text-sm font-semibold text-violet-300 ring-1 ring-inset ring-violet-500/30">
                  {{ t(`formats.${course.format}`) }}
                </span>
              </div>
              
              <h1 class="mt-6 text-4xl font-bold tracking-tight sm:text-5xl lg:text-6xl">{{ course.title }}</h1>
              <p class="mt-6 max-w-2xl text-lg text-slate-300">{{ course.description }}</p>

              <div class="mt-8 flex flex-wrap items-center gap-6 text-sm text-slate-300">
                <div class="flex items-center gap-2">
                  <StarRating :model-value="course.averageRating" readonly />
                  <span class="font-medium text-white">{{ course.averageRating.toFixed(1) }}</span>
                  <span>({{ course.reviewsCount }} {{ t('course.reviewsLabel') }})</span>
                </div>
                <div class="flex items-center gap-2">
                  <Icon icon="mdi:account-group-outline" class="h-5 w-5 text-slate-400" />
                  <span>{{ course.studentsCount }} {{ t('course.students') }}</span>
                </div>
              </div>
            </div>

            <!-- Teacher card -->
            <div class="lg:col-span-1 flex items-center lg:justify-end">
              <div class="flex items-center gap-4 rounded-2xl bg-white/10 p-6 backdrop-blur-md ring-1 ring-white/20 transition-all hover:bg-white/15">
                <div class="flex h-16 w-16 overflow-hidden shrink-0 items-center justify-center rounded-full bg-slate-800 text-xl font-bold text-white shadow-inner">
                  <img v-if="course.teacherAvatarUrl" :src="course.teacherAvatarUrl" alt="" class="h-full w-full object-cover" />
                  <span v-else>{{ course.teacherFullName.charAt(0).toUpperCase() }}</span>
                </div>
                <div>
                  <p class="text-sm font-medium text-slate-400">{{ t('course.teacherLabel') }}</p>
                  <p class="text-lg font-semibold text-white">{{ course.teacherFullName }}</p>
                  <RouterLink :to="`/teachers/${course.teacherId}`" class="mt-1 inline-flex items-center text-sm text-amber-400 hover:text-amber-300">
                    {{ t('course.viewProfile') }} <Icon icon="mdi:arrow-right" class="ml-1 h-4 w-4" />
                  </RouterLink>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Main content -->
      <div class="bg-slate-50 py-12">
        <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div class="grid grid-cols-1 gap-12 lg:grid-cols-3">
            
            <!-- Left content tabs -->
            <div class="lg:col-span-2">
              <div class="border-b border-slate-200">
                <nav class="-mb-px flex space-x-8">
                  <button
                    v-for="tab in ['about', 'lessons', 'reviews']"
                    :key="tab"
                    class="whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium transition-colors"
                    :class="activeTab === tab ? 'border-amber-500 text-amber-600' : 'border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700'"
                    @click="activeTab = tab as any"
                  >
                    {{ t(`course.tab_${tab}`) }}
                  </button>
                </nav>
              </div>

              <div class="mt-8">
                <!-- About Tab -->
                <div v-show="activeTab === 'about'" class="prose prose-slate max-w-none text-slate-600">
                  <p class="whitespace-pre-line leading-relaxed">{{ course.description }}</p>
                </div>

                <!-- Lessons Tab -->
                <div v-show="activeTab === 'lessons'">
                  <h3 class="mb-6 text-lg font-bold text-slate-900">{{ t('course.program') }}</h3>
                  <div class="divide-y divide-slate-100 rounded-2xl border border-slate-100 bg-white shadow-sm">
                    <div v-for="lesson in lessons" :key="lesson.id" class="flex items-center gap-4 p-5">
                      <div class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-slate-50 text-slate-400">
                        <Icon icon="mdi:play-circle-outline" class="h-6 w-6" />
                      </div>
                      <div class="flex-1">
                        <p class="text-sm font-medium text-slate-900">{{ lesson.title }}</p>
                      </div>
                    </div>
                    <div v-if="lessons.length === 0" class="p-8 text-center text-slate-500">
                      {{ t('course.noLessons') }}
                    </div>
                  </div>
                </div>

                <!-- Reviews Tab -->
                <div v-show="activeTab === 'reviews'">
                  <ReviewsList :course-id="courseId" @review-added="loadData" @review-deleted="loadData" />
                </div>
              </div>
            </div>

            <!-- Right Sidebar -->
            <div class="lg:col-span-1">
              <div class="sticky top-24">
                <AppCard>
                  <div class="text-center">
                    <p class="text-4xl font-bold text-slate-900">
                      {{ course.price === 0 ? t('course.free') : `${course.price} ${t('course.price')}` }}
                    </p>
                    
                    <AppButton
                      v-if="enrollButtonData"
                      :variant="enrollButtonData.variant"
                      size="lg"
                      full-width
                      class="mt-6"
                      @click="enrollButtonData.action"
                    >
                      {{ enrollButtonData.text }}
                    </AppButton>
                  </div>

                  <div class="mt-8 space-y-4 text-sm text-slate-600">
                    <div class="flex items-center justify-between border-b border-slate-100 pb-4">
                      <span class="flex items-center gap-2"><Icon icon="mdi:laptop" class="h-5 w-5 text-slate-400" /> {{ t('filters.format') }}</span>
                      <span class="font-medium text-slate-900">{{ t(`formats.${course.format}`) }}</span>
                    </div>
                    <div class="flex items-center justify-between border-b border-slate-100 pb-4">
                      <span class="flex items-center gap-2"><Icon icon="mdi:clock-outline" class="h-5 w-5 text-slate-400" /> {{ t('course.duration') }}</span>
                      <span class="font-medium text-slate-900">{{ course.durationHours }} {{ t('course.hours') }}</span>
                    </div>
                    <div class="flex items-center justify-between border-b border-slate-100 pb-4">
                      <span class="flex items-center gap-2"><Icon icon="mdi:format-list-bulleted" class="h-5 w-5 text-slate-400" /> {{ t('course.lessonsCount') }}</span>
                      <span class="font-medium text-slate-900">{{ course.lessonsCount }}</span>
                    </div>
                    <div class="flex items-center justify-between pb-2">
                      <span class="flex items-center gap-2"><Icon icon="mdi:calendar-check" class="h-5 w-5 text-slate-400" /> {{ t('course.lastUpdated') }}</span>
                      <span class="font-medium text-slate-900">{{ new Date(course.updatedAt).toLocaleDateString() }}</span>
                    </div>
                  </div>
                </AppCard>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <EnrollModal
        :is-open="enrollModalOpen"
        :course-id="course.id"
        :course-title="course.title"
        :price="course.price"
        @close="enrollModalOpen = false"
        @success="onEnrollSuccess"
      />
    </template>
  </DefaultLayout>
</template>
