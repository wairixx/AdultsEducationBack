<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter, RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import AppButton from '@/components/common/AppButton.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import { getMyEducationsAsStudent } from '@/api/educations'
import type { EducationResponse, EducationStatus } from '@/types/api'

const { t } = useI18n()
const router = useRouter()

const educations = ref<EducationResponse[]>([])
const loading = ref(true)

const activeTab = ref<EducationStatus | 'ALL'>('ALL')
const page = ref(0)
const totalPages = ref(0)

const tabs = [
  { value: 'ALL', label: t('filters.all') },
  { value: 'ACTIVE', label: t('statuses.ACTIVE') },
  { value: 'COMPLETED', label: t('statuses.COMPLETED') },
  { value: 'CANCELLED', label: t('statuses.CANCELLED') },
]

async function loadData() {
  loading.value = true
  try {
    const res = await getMyEducationsAsStudent(
      { status: activeTab.value === 'ALL' ? undefined : activeTab.value as EducationStatus },
      page.value,
      12,
      'enrolledDate,desc'
    )
    educations.value = res.content
    totalPages.value = res.totalPages
  } catch {
    console.error('An error occurred')
  } finally {
    loading.value = false
  }
}

watch(activeTab, () => {
  page.value = 0
  loadData()
})

onMounted(loadData)

function setPage(p: number) {
  page.value = p
  loadData()
}

function getStatusColor(status: EducationStatus) {
  switch (status) {
    case 'ACTIVE': return 'bg-amber-100 text-amber-800'
    case 'COMPLETED': return 'bg-emerald-100 text-emerald-800'
    case 'CANCELLED': return 'bg-slate-100 text-slate-800'
    default: return 'bg-slate-100 text-slate-800'
  }
}

function canOpenLearning(status: EducationStatus) {
  return status !== 'CANCELLED'
}

const emptyState = computed(() => {
  if (activeTab.value === 'CANCELLED') {
    return {
      title: t('myCourses.emptyCancelledTitle'),
      desc: t('myCourses.emptyCancelledDesc'),
      showBrowseButton: false,
    }
  }

  return {
    title: t('myCourses.emptyTitle'),
    desc: t('myCourses.emptyDesc'),
    showBrowseButton: true,
  }
})
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 py-8 min-h-[calc(100vh-64px)]">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        <div class="border-b border-slate-200 pb-5 sm:flex sm:items-center sm:justify-between">
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">{{ t('nav.myCourses') }}</h1>
          <div class="mt-3 sm:ml-4 sm:mt-0">
            <RouterLink to="/courses" class="inline-flex items-center text-sm font-medium text-amber-600 hover:text-amber-500">
              <Icon icon="mdi:plus" class="mr-1 h-5 w-5" />
              {{ t('actions.browseCourses') }}
            </RouterLink>
          </div>
        </div>

        <!-- Tabs -->
        <div class="mt-6 border-b border-slate-200">
          <nav class="-mb-px flex space-x-8 overflow-x-auto">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              class="whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium transition-colors"
              :class="activeTab === tab.value ? 'border-amber-500 text-amber-600' : 'border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700'"
              @click="activeTab = tab.value as any"
            >
              {{ tab.label }}
            </button>
          </nav>
        </div>

        <div class="mt-8">
          <div v-if="loading" class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
            <SkeletonLoader v-for="i in 3" :key="i" type="card" />
          </div>

          <div v-else-if="educations.length === 0" class="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-white py-24 text-center">
            <Icon icon="mdi:book-open-blank-variant" class="h-16 w-16 text-slate-300" />
            <h3 class="mt-4 text-lg font-semibold text-slate-900">{{ emptyState.title }}</h3>
            <p class="mt-1 text-sm text-slate-500">{{ emptyState.desc }}</p>
            <AppButton
              v-if="emptyState.showBrowseButton"
              variant="secondary"
              class="mt-6"
              @click="router.push('/courses')"
            >
              {{ t('actions.browseCourses') }}
            </AppButton>
          </div>

          <div v-else>
            <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <!-- Enrollment Card -->
              <div
                v-for="ed in educations"
                :key="ed.id"
                class="group flex flex-col overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-100 transition-all hover:shadow-xl hover:ring-amber-500/30"
              >
                <!-- Cover -->
                <RouterLink
                  v-if="canOpenLearning(ed.status)"
                  :to="`/my-courses/${ed.id}`"
                  class="relative aspect-video w-full overflow-hidden bg-slate-100 block"
                >
                  <img
                    v-if="ed.courseCoverUrl"
                    :src="ed.courseCoverUrl"
                    alt="Course cover"
                    class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
                  />
                  <div
                    v-else
                    class="flex h-full w-full items-center justify-center bg-gradient-to-br from-slate-200 to-slate-300 transition-transform duration-500 group-hover:scale-105"
                  >
                    <Icon icon="mdi:school-outline" class="h-12 w-12 text-slate-400" />
                  </div>
                  
                  <!-- Status Badge -->
                  <div class="absolute left-3 top-3 rounded-full px-2.5 py-1 text-xs font-semibold backdrop-blur-sm" :class="getStatusColor(ed.status)">
                    {{ t(`statuses.${ed.status}`) }}
                  </div>
                </RouterLink>
                <div
                  v-else
                  class="relative aspect-video w-full overflow-hidden bg-slate-100 block"
                >
                  <img
                    v-if="ed.courseCoverUrl"
                    :src="ed.courseCoverUrl"
                    alt="Course cover"
                    class="h-full w-full object-cover"
                  />
                  <div
                    v-else
                    class="flex h-full w-full items-center justify-center bg-gradient-to-br from-slate-200 to-slate-300"
                  >
                    <Icon icon="mdi:school-outline" class="h-12 w-12 text-slate-400" />
                  </div>
                  <div class="absolute left-3 top-3 rounded-full px-2.5 py-1 text-xs font-semibold backdrop-blur-sm" :class="getStatusColor(ed.status)">
                    {{ t(`statuses.${ed.status}`) }}
                  </div>
                </div>

                <div class="flex flex-1 flex-col p-5">
                  <RouterLink
                    v-if="canOpenLearning(ed.status)"
                    :to="`/my-courses/${ed.id}`"
                    class="line-clamp-2 text-lg font-bold leading-tight text-slate-900 group-hover:text-amber-600"
                  >
                    {{ ed.courseTitle }}
                  </RouterLink>
                  <p
                    v-else
                    class="line-clamp-2 text-lg font-bold leading-tight text-slate-900"
                  >
                    {{ ed.courseTitle }}
                  </p>
                  
                  <div class="mt-4 flex-1">
                    <div class="flex items-center justify-between text-sm">
                      <span class="font-medium text-slate-700">{{ t('myCourses.progress') }}</span>
                      <span class="font-semibold" :class="ed.progress === 100 ? 'text-emerald-600' : 'text-slate-900'">{{ ed.progress }}%</span>
                    </div>
                    <div class="mt-2 h-2 w-full overflow-hidden rounded-full bg-slate-100">
                      <div
                        class="h-full rounded-full transition-all duration-500"
                        :class="ed.progress === 100 ? 'bg-emerald-500' : 'bg-gradient-to-r from-amber-400 to-amber-500'"
                        :style="{ width: `${ed.progress}%` }"
                      ></div>
                    </div>
                  </div>

                  <div class="mt-6 flex items-center justify-between gap-3 border-t border-slate-100 pt-4">
                    <AppButton
                      v-if="ed.status === 'COMPLETED'"
                      variant="ghost"
                      size="sm"
                      class="text-amber-600 hover:bg-amber-50"
                      @click="router.push(`/certificates/by-education/${ed.id}`)"
                    >
                      <Icon icon="mdi:certificate-outline" class="mr-1.5 h-4.5 w-4.5" />
                      {{ t('myCourses.viewCertificate') }}
                    </AppButton>
                    <AppButton
                      v-else
                      variant="secondary"
                      size="sm"
                      full-width
                      :disabled="!canOpenLearning(ed.status)"
                      @click="router.push(`/my-courses/${ed.id}`)"
                    >
                      {{
                        canOpenLearning(ed.status)
                          ? t('course.goToLearning')
                          : t('myCourses.cancelledUnavailable')
                      }}
                    </AppButton>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="totalPages > 1" class="mt-10 flex justify-center border-t border-slate-200 pt-8">
              <AppPagination
                :current-page="page"
                :total-pages="totalPages"
                @page-change="setPage"
              />
            </div>
          </div>
        </div>

      </div>
    </div>
  </DefaultLayout>
</template>
