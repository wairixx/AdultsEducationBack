<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import CourseCard from '@/components/course/CourseCard.vue'
import CourseFilters from '@/components/course/CourseFilters.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import AppButton from '@/components/common/AppButton.vue'
import { usePagination } from '@/composables/usePagination'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import { getAllCourses } from '@/api/courses'
import type { CourseResponse } from '@/types/api'
import { useRoute } from 'vue-router'

const { t } = useI18n()
const route = useRoute()

const { page, size, sort, setPage, setTotal, totalElements, totalPages, reset: resetPagination, syncToUrl } = usePagination({ defaultSize: 12 })

const filters = reactive({
  title: (route.query.title as string) || '',
  topic: (route.query.topic as string) || '',
  format: (route.query.format as string) || '',
  minPrice: route.query.minPrice ? Number(route.query.minPrice) : undefined,
  maxPrice: route.query.maxPrice ? Number(route.query.maxPrice) : undefined,
  minHours: route.query.minHours ? Number(route.query.minHours) : undefined,
  maxHours: route.query.maxHours ? Number(route.query.maxHours) : undefined,
})

const courses = ref<CourseResponse[]>([])
const loading = ref(true)
const isMobileFiltersOpen = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res = await getAllCourses(
      {
        title: filters.title || undefined,
        topic: filters.topic ? (filters.topic as any) : undefined,
        format: filters.format ? (filters.format as any) : undefined,
        minPrice: filters.minPrice,
        maxPrice: filters.maxPrice,
        minHours: filters.minHours,
        maxHours: filters.maxHours,
      },
      page.value,
      size.value,
      sort.value
    )
    courses.value = res.content
    setTotal(res.totalElements, res.totalPages)
  } catch {
    console.error('An error occurred')
  } finally {
    loading.value = false
  }
}

// Watch filters to reset page and reload
watch(filters, () => {
  page.value = 0
  syncToUrl(filters)
  loadData()
}, { deep: true })

// Watch sort and page to reload (sort/page are handled by usePagination)
watch([page, sort], loadData)

onMounted(loadData)

function resetAll() {
  filters.title = ''
  filters.topic = ''
  filters.format = ''
  filters.minPrice = undefined
  filters.maxPrice = undefined
  filters.minHours = undefined
  filters.maxHours = undefined
  resetPagination(false)
}
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 py-8 min-h-[calc(100vh-64px)]">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <!-- Header & Mobile Toggle -->
        <div class="flex flex-col items-start justify-between gap-4 border-b border-slate-200 pb-6 sm:flex-row sm:items-center">
          <div>
            <h1 class="text-3xl font-bold tracking-tight text-slate-900">{{ t('pages.catalog.title') }}</h1>
            <p class="mt-2 text-sm text-slate-500">
              {{ t('pages.catalog.found', { count: totalElements }) }}
            </p>
          </div>
          <AppButton 
            variant="secondary" 
            class="sm:hidden"
            @click="isMobileFiltersOpen = true"
          >
            <Icon icon="mdi:filter-variant" class="mr-2 h-5 w-5" />
            {{ t('filters.title') }}
          </AppButton>
        </div>

        <div class="mt-8 grid grid-cols-1 gap-8 lg:grid-cols-4">
          <!-- Filters Desktop Sidebar -->
          <div class="hidden lg:block lg:col-span-1">
            <div class="sticky top-24">
              <CourseFilters
                v-model:title="filters.title"
                v-model:topic="filters.topic"
                v-model:format="filters.format"
                v-model:minPrice="filters.minPrice"
                v-model:maxPrice="filters.maxPrice"
                v-model:minHours="filters.minHours"
                v-model:maxHours="filters.maxHours"
                v-model:sort="sort"
                @reset="resetAll"
              />
            </div>
          </div>

          <!-- Mobile Filters Drawer -->
          <Transition
            enter-active-class="transition duration-300 ease-in-out"
            enter-from-class="translate-x-full"
            enter-to-class="translate-x-0"
            leave-active-class="transition duration-300 ease-in-out"
            leave-from-class="translate-x-0"
            leave-to-class="translate-x-full"
          >
            <div v-if="isMobileFiltersOpen" class="fixed inset-0 z-50 flex justify-end lg:hidden">
              <div class="fixed inset-0 bg-slate-900/50 backdrop-blur-sm" @click="isMobileFiltersOpen = false"></div>
              <div class="relative w-full max-w-xs overflow-y-auto bg-slate-50 p-6 shadow-2xl">
                <button
                  class="absolute right-4 top-4 text-slate-400 hover:text-slate-600"
                  @click="isMobileFiltersOpen = false"
                >
                  <Icon icon="mdi:close" class="h-6 w-6" />
                </button>
                <CourseFilters
                  v-model:title="filters.title"
                  v-model:topic="filters.topic"
                  v-model:format="filters.format"
                  v-model:minPrice="filters.minPrice"
                  v-model:maxPrice="filters.maxPrice"
                  v-model:minHours="filters.minHours"
                  v-model:maxHours="filters.maxHours"
                  v-model:sort="sort"
                  @reset="resetAll"
                  class="mt-8 border-none ring-0 shadow-none bg-transparent p-0"
                />
              </div>
            </div>
          </Transition>

          <!-- Course Grid -->
          <div class="lg:col-span-3">
            <!-- Loading Skeletons -->
            <div v-if="loading" class="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-3">
              <SkeletonLoader v-for="i in 6" :key="i" type="card" />
            </div>

            <!-- Empty State -->
            <div v-else-if="courses.length === 0" class="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-white py-24 text-center">
              <Icon icon="mdi:school-outline" class="h-16 w-16 text-slate-300" />
              <h3 class="mt-4 text-lg font-semibold text-slate-900">{{ t('pages.catalog.emptyTitle') }}</h3>
              <p class="mt-1 text-sm text-slate-500">{{ t('pages.catalog.emptyDesc') }}</p>
              <AppButton variant="secondary" class="mt-6" @click="resetAll">
                {{ t('actions.clear') }}
              </AppButton>
            </div>

            <!-- Results -->
            <div v-else>
              <div class="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-3">
                <CourseCard
                  v-for="course in courses"
                  :key="course.id"
                  :course="course"
                />
              </div>
              
              <div class="mt-10 flex justify-center border-t border-slate-200 pt-8">
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
    </div>
  </DefaultLayout>
</template>
