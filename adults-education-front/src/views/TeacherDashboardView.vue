<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter, RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppButton from '@/components/common/AppButton.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import ConfirmDialog from '@/components/admin/ConfirmDialog.vue'
import { getMyCoursesAsTeacher, deleteCourse, setCourseVisibility } from '@/api/courses'
import { getMyEducationsAsTeacher } from '@/api/educations'
import { useAuthStore } from '@/stores/auth'
import type { CourseResponse, EducationResponse } from '@/types/api'


const { t } = useI18n()
const router = useRouter()
const auth = useAuthStore()

const courses = ref<CourseResponse[]>([])
const students = ref<EducationResponse[]>([])
const loading = ref(true)
const deleteConfirmOpen = ref(false)
const deletingCourse = ref(false)
const courseToDelete = ref<number | null>(null)

const activeTab = ref<'COURSES' | 'STUDENTS'>('COURSES')

const stats = computed(() => {
  const totalCourses = courses.value.length
  const totalStudents = courses.value.reduce((acc, c) => acc + c.studentsCount, 0)
  const avgRating = courses.value.length > 0 
    ? (courses.value.reduce((acc, c) => acc + c.averageRating, 0) / courses.value.length).toFixed(1)
    : '0.0'
  return { totalCourses, totalStudents, avgRating }
})

async function loadData() {
  loading.value = true
  try {
    const [cRes, sRes] = await Promise.all([
      getMyCoursesAsTeacher({}, 0, 50, 'id,desc'),
      getMyEducationsAsTeacher({}, 0, 10, 'enrolledDate,desc')
    ])
    courses.value = cRes.content
    students.value = sRes.content
  } catch {
    console.error('An error occurred')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function handleDeleteCourse(id: number) {
  courseToDelete.value = id
  deleteConfirmOpen.value = true
}

async function executeDeleteCourse() {
  if (!courseToDelete.value) return
  deletingCourse.value = true
  try {
    await deleteCourse(courseToDelete.value)
    courses.value = courses.value.filter(c => c.id !== courseToDelete.value)
  } catch {
    console.error('An error occurred')
  } finally {
    deletingCourse.value = false
    deleteConfirmOpen.value = false
    courseToDelete.value = null
  }
}

async function toggleVisibility(course: CourseResponse) {
  try {
    const res = await setCourseVisibility(course.id, !course.visible)
    const idx = courses.value.findIndex(c => c.id === course.id)
    if (idx !== -1) {
      courses.value[idx] = res
    }
  } catch {
    console.error('An error occurred')
  }
}
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 py-8 min-h-[calc(100vh-64px)]">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        <!-- Hero & Stats -->
        <div class="mb-8 rounded-2xl bg-gradient-to-r from-amber-500 to-amber-600 p-8 shadow-sm text-white">
          <h1 class="text-3xl font-bold mb-6">{{ t('teacher.greeting', { name: auth.user?.firstName || '' }) }}</h1>
          
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="rounded-xl bg-white/20 p-4 backdrop-blur-md">
              <div class="text-amber-100 text-sm font-medium mb-1">{{ t('teacher.stats.myCourses') }}</div>
              <div class="text-3xl font-bold">{{ stats.totalCourses }}</div>
            </div>
            <div class="rounded-xl bg-white/20 p-4 backdrop-blur-md">
              <div class="text-amber-100 text-sm font-medium mb-1">{{ t('teacher.stats.totalStudents') }}</div>
              <div class="text-3xl font-bold">{{ stats.totalStudents }}</div>
            </div>
            <div class="rounded-xl bg-white/20 p-4 backdrop-blur-md">
              <div class="text-amber-100 text-sm font-medium mb-1">{{ t('teacher.stats.avgRating') }}</div>
              <div class="flex items-center gap-1.5">
                <span class="text-3xl font-bold">{{ stats.avgRating }}</span>
                <Icon icon="mdi:star" class="h-6 w-6 text-amber-200" />
              </div>
            </div>
          </div>
        </div>

        <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <SkeletonLoader v-for="i in 3" :key="i" type="card" />
        </div>

        <div v-else>
          <!-- Tabs & Actions -->
          <div class="border-b border-slate-200 mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <nav class="-mb-px flex space-x-8">
              <button
                class="whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium transition-colors"
                :class="activeTab === 'COURSES' ? 'border-amber-500 text-amber-600' : 'border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700'"
                @click="activeTab = 'COURSES'"
              >
                {{ t('teacher.tabs.courses') }}
              </button>
              <button
                class="whitespace-nowrap border-b-2 py-4 px-1 text-sm font-medium transition-colors"
                :class="activeTab === 'STUDENTS' ? 'border-amber-500 text-amber-600' : 'border-transparent text-slate-500 hover:border-slate-300 hover:text-slate-700'"
                @click="activeTab = 'STUDENTS'"
              >
                {{ t('teacher.tabs.students') }}
              </button>
            </nav>
            <div v-if="activeTab === 'COURSES'" class="py-3 sm:py-0">
              <AppButton variant="primary" @click="router.push('/dashboard/courses/new')">
                <Icon icon="mdi:plus" class="mr-1.5 h-4.5 w-4.5" />
                {{ t('teacher.courses.createNew') }}
              </AppButton>
            </div>
            <div v-if="activeTab === 'STUDENTS'" class="py-3 sm:py-0">
              <RouterLink to="/dashboard/students" class="text-sm font-medium text-amber-600 hover:text-amber-700">
                Переглянути всіх &rarr;
              </RouterLink>
            </div>
          </div>

          <!-- Courses Tab -->
          <div v-if="activeTab === 'COURSES'">
            <div v-if="courses.length === 0" class="rounded-xl border border-dashed border-slate-300 py-16 text-center">
              <p class="text-slate-500 mb-4">У вас ще немає курсів.</p>
              <AppButton variant="primary" @click="router.push('/dashboard/courses/new')">
                {{ t('teacher.courses.createNew') }}
              </AppButton>
            </div>
            <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div v-for="course in courses" :key="course.id" class="rounded-2xl bg-white shadow-sm ring-1 ring-slate-100 overflow-hidden flex flex-col group transition-all hover:shadow-md">
                <div class="aspect-video w-full bg-slate-100 relative group cursor-pointer" @click="router.push(`/courses/${course.id}`)">
                  <img v-if="course.coverUrl" :src="course.coverUrl" alt="Course cover" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full flex items-center justify-center text-slate-400">
                    <Icon icon="mdi:image-outline" class="h-10 w-10" />
                  </div>
                  <div class="absolute top-2 right-2 rounded-md bg-slate-900/60 backdrop-blur px-2 py-1 text-xs text-white">
                    <Icon v-if="course.visible" icon="mdi:eye" class="inline mr-1" />
                    <Icon v-else icon="mdi:eye-off" class="inline mr-1 text-amber-400" />
                    {{ course.visible ? t('teacher.courses.visible') : t('teacher.courses.hidden') }}
                  </div>
                </div>
                <div class="p-4 flex-1 flex flex-col">
                  <h3 class="font-bold text-slate-900 line-clamp-1 mb-1" :title="course.title">{{ course.title }}</h3>
                  <p class="text-xs text-slate-500 mb-4">{{ course.studentsCount }} {{ t('course.students') }} • {{ course.lessonsCount }} {{ t('course.lessonsCount') }}</p>
                  
                  <div class="mt-auto flex flex-col gap-2">
                    <div class="flex gap-2">
                      <AppButton variant="secondary" size="sm" class="flex-1" @click="router.push(`/dashboard/courses/${course.id}/edit`)">
                        <Icon icon="mdi:pencil" class="mr-1 h-4 w-4" />
                        {{ t('teacher.courses.edit') }}
                      </AppButton>
                      <AppButton variant="secondary" size="sm" class="flex-1" @click="router.push(`/dashboard/courses/${course.id}/lessons`)">
                        <Icon icon="mdi:format-list-checks" class="mr-1 h-4 w-4" />
                        {{ t('teacher.courses.manageLessons') }}
                      </AppButton>
                    </div>
                    <div class="flex gap-2 border-t border-slate-100 pt-2">
                      <button class="flex-1 text-xs font-medium text-slate-500 hover:text-slate-700 py-1 transition-colors" @click="toggleVisibility(course)">
                        <Icon :icon="course.visible ? 'mdi:eye-off' : 'mdi:eye'" class="inline h-3.5 w-3.5 mb-0.5" />
                        {{ course.visible ? 'Приховати' : 'Показати' }}
                      </button>
                      <button class="flex-1 text-xs font-medium text-rose-500 hover:text-rose-700 py-1 transition-colors" @click="handleDeleteCourse(course.id)">
                        <Icon icon="mdi:delete" class="inline h-3.5 w-3.5 mb-0.5" />
                        Видалити
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Students Tab -->
          <div v-if="activeTab === 'STUDENTS'">
            <div class="overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-200">
              <table class="min-w-full divide-y divide-slate-200">
                <thead class="bg-slate-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Студент</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Курс</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Прогрес</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Зараховано</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-200 bg-white">
                  <tr v-for="st in students" :key="st.id" class="hover:bg-slate-50 transition-colors cursor-pointer" @click="router.push('/dashboard/students')">
                    <td class="whitespace-nowrap px-6 py-4">
                      <div class="flex items-center">
                        <div class="h-8 w-8 flex-shrink-0 rounded-full bg-slate-200 overflow-hidden">
                          <img v-if="st.studentAvatarUrl" :src="st.studentAvatarUrl" alt="Student avatar" class="h-full w-full object-cover" />
                          <div v-else class="flex h-full w-full items-center justify-center text-xs font-bold text-slate-500">{{ st.studentFullName.charAt(0) }}</div>
                        </div>
                        <div class="ml-3">
                          <p class="text-sm font-medium text-slate-900">{{ st.studentFullName }}</p>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4">
                      <div class="text-sm text-slate-900 line-clamp-1" :title="st.courseTitle">{{ st.courseTitle }}</div>
                    </td>
                    <td class="whitespace-nowrap px-6 py-4">
                      <div class="flex items-center gap-2">
                        <div class="h-1.5 w-16 bg-slate-100 rounded-full overflow-hidden">
                          <div class="h-full bg-amber-500 rounded-full" :style="{ width: `${st.progress}%` }"></div>
                        </div>
                        <span class="text-xs text-slate-500">{{ st.progress }}%</span>
                      </div>
                    </td>
                    <td class="whitespace-nowrap px-6 py-4 text-sm text-slate-500">
                      {{ new Date(st.enrolledDate).toLocaleDateString() }}
                    </td>
                  </tr>
                  <tr v-if="students.length === 0">
                    <td colspan="4" class="px-6 py-8 text-center text-slate-500">
                      Студентів поки немає
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
        </div>
      </div>
    </div>
    <ConfirmDialog
      :is-open="deleteConfirmOpen"
      :title="t('teacher.courses.deleteConfirm')"
      :message="t('admin.courses.deleteMessage')"
      :loading="deletingCourse"
      danger
      @confirm="executeDeleteCourse"
      @cancel="deleteConfirmOpen = false"
    />
  </DefaultLayout>
</template>
