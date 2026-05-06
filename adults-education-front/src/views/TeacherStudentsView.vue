<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import SearchInput from '@/components/common/SearchInput.vue'
import SelectField from '@/components/common/SelectField.vue'
import AppButton from '@/components/common/AppButton.vue'
import { getMyEducationsAsTeacher, updateEducationByTeacher } from '@/api/educations'
import { getMyCoursesAsTeacher } from '@/api/courses'
import type { EducationResponse, EducationStatus, EducationLevel } from '@/types/api'


const { t } = useI18n()

const loading = ref(true)
const students = ref<EducationResponse[]>([])
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

const searchQuery = ref('')
const filterStatus = ref<EducationStatus | ''>('')
const filterCourse = ref<number | ''>('')
const coursesOptions = ref<{value: number, label: string}[]>([])

// Load courses for filter
async function loadCourses() {
  try {
    const res = await getMyCoursesAsTeacher({}, 0, 100)
    coursesOptions.value = res.content.map(c => ({ value: c.id, label: c.title }))
  } catch {
    console.error('An error occurred')
  }
}

async function loadData() {
  loading.value = true
  try {
    // API doesn't have a direct student name search, so we fetch and filter locally if needed,
    // but the backend EducationFilter has `courseId` and `status`.
    // We will just pass courseId and status. We can search client-side later if the list is small.
    // For proper search, backend should support `studentName` in EducationFilter.
    // We will simulate it by fetching a bit more or relying on backend.
    const res = await getMyEducationsAsTeacher(
      { 
        status: filterStatus.value || undefined,
        courseId: filterCourse.value || undefined
      },
      page.value,
      10,
      'enrolledDate,desc'
    )
    
    let items = res.content
    // Client-side search for name
    if (searchQuery.value) {
      const q = searchQuery.value.toLowerCase()
      items = items.filter(s => s.studentFullName.toLowerCase().includes(q))
    }
    
    students.value = items
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch {
    console.error('An error occurred')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCourses()
  loadData()
})

watch([searchQuery, filterStatus, filterCourse], () => {
  page.value = 0
  loadData()
})

function setPage(p: number) {
  page.value = p
  loadData()
}

// Modal logic
const showModal = ref(false)
const selectedStudent = ref<EducationResponse | null>(null)
const modalSubmitting = ref(false)

const editStatus = ref<EducationStatus>('ACTIVE')
const editLevel = ref<EducationLevel>('BEGINNER')
const editNote = ref('')

const statusOptions = [
  { value: 'PENDING', label: t('statuses.PENDING') },
  { value: 'ACTIVE', label: t('statuses.ACTIVE') },
  { value: 'COMPLETED', label: t('statuses.COMPLETED') },
  { value: 'CANCELLED', label: t('statuses.CANCELLED') },
]

const levelOptions = [
  { value: 'BEGINNER', label: t('levels.BEGINNER') },
  { value: 'INTERMEDIATE', label: t('levels.INTERMEDIATE') },
  { value: 'ADVANCED', label: t('levels.ADVANCED') },
  { value: 'EXPERT', label: t('levels.EXPERT') },
]

function openModal(student: EducationResponse) {
  selectedStudent.value = student
  editStatus.value = student.status
  editLevel.value = student.level || 'BEGINNER'
  editNote.value = student.note || ''
  showModal.value = true
}

async function saveStudentUpdates() {
  if (!selectedStudent.value) return
  modalSubmitting.value = true
  try {
    const res = await updateEducationByTeacher(selectedStudent.value.id, {
      status: editStatus.value,
      level: editLevel.value,
      note: editNote.value
    })
    
    // Update local state
    const idx = students.value.findIndex(s => s.id === res.id)
    if (idx !== -1) {
      students.value[idx] = res
    }
    toast.success(t('teacher.students.updateSuccess'))
    showModal.value = false
  } catch {
    console.error('An error occurred')
  } finally {
    modalSubmitting.value = false
  }
}
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 py-8 min-h-[calc(100vh-64px)]">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        
        <div class="mb-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">{{ t('teacher.students.title') }}</h1>
          
          <div class="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
            <SearchInput
              v-model="searchQuery"
              :placeholder="t('teacher.students.search')"
              class="w-full sm:w-64"
            />
            
            <SelectField
              v-model="filterCourse"
              :options="[{value: '', label: 'Всі курси'}, ...coursesOptions]"
              class="w-full sm:w-48"
            />
            
            <SelectField
              v-model="filterStatus"
              :options="[{value: '', label: 'Всі статуси'}, ...statusOptions]"
              class="w-full sm:w-48"
            />
          </div>
        </div>

        <div v-if="loading" class="flex justify-center py-12">
          <div class="h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
        </div>

        <div v-else class="bg-white rounded-2xl shadow-sm ring-1 ring-slate-200 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-slate-200">
              <thead class="bg-slate-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Студент</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Курс</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Статус / Прогрес</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Рівень</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Дата</th>
                  <th class="px-6 py-3 relative"><span class="sr-only">Дії</span></th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-200 bg-white">
                <tr v-for="st in students" :key="st.id" class="hover:bg-slate-50 transition-colors">
                  <td class="whitespace-nowrap px-6 py-4">
                    <div class="flex items-center">
                      <div class="h-10 w-10 flex-shrink-0 rounded-full bg-slate-200 overflow-hidden">
                        <img v-if="st.studentAvatarUrl" :src="st.studentAvatarUrl" alt="Student avatar" class="h-full w-full object-cover" />
                        <div v-else class="flex h-full w-full items-center justify-center text-sm font-bold text-slate-500">{{ st.studentFullName.charAt(0) }}</div>
                      </div>
                      <div class="ml-4">
                        <p class="text-sm font-medium text-slate-900">{{ st.studentFullName }}</p>
                        <p v-if="st.note" class="text-xs text-slate-500 flex items-center mt-0.5" title="Є замітка">
                          <Icon icon="mdi:note-text-outline" class="mr-1 h-3 w-3" /> Замітка
                        </p>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4">
                    <RouterLink :to="`/courses/${st.courseId}`" class="text-sm font-medium text-amber-600 hover:text-amber-700 line-clamp-2">
                      {{ st.courseTitle }}
                    </RouterLink>
                  </td>
                  <td class="whitespace-nowrap px-6 py-4">
                    <div class="flex items-center gap-2 mb-1.5">
                      <span class="inline-flex rounded-full px-2 py-0.5 text-xs font-medium"
                        :class="{
                          'bg-amber-100 text-amber-800': st.status === 'ACTIVE',
                          'bg-emerald-100 text-emerald-800': st.status === 'COMPLETED',
                          'bg-slate-100 text-slate-800': st.status === 'CANCELLED',
                          'bg-orange-100 text-orange-800': st.status === 'PENDING'
                        }">
                        {{ t(`statuses.${st.status}`) }}
                      </span>
                    </div>
                    <div class="flex items-center gap-2">
                      <div class="h-1.5 w-16 bg-slate-100 rounded-full overflow-hidden">
                        <div class="h-full bg-amber-500 rounded-full" :style="{ width: `${st.progress}%` }"></div>
                      </div>
                      <span class="text-xs text-slate-500">{{ st.progress }}%</span>
                    </div>
                  </td>
                  <td class="whitespace-nowrap px-6 py-4 text-sm text-slate-500">
                    {{ t(`levels.${st.level}`) }}
                  </td>
                  <td class="whitespace-nowrap px-6 py-4 text-sm text-slate-500">
                    {{ new Date(st.enrolledDate).toLocaleDateString() }}
                  </td>
                  <td class="whitespace-nowrap px-6 py-4 text-right text-sm font-medium">
                    <button class="text-amber-600 hover:text-amber-900 bg-amber-50 px-3 py-1.5 rounded-lg transition-colors" @click="openModal(st)">
                      Деталі
                    </button>
                  </td>
                </tr>
                <tr v-if="students.length === 0">
                  <td colspan="6" class="px-6 py-12 text-center text-slate-500">
                    Нічого не знайдено
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div v-if="totalPages > 1" class="border-t border-slate-200 px-6 py-4">
            <AppPagination
              :current-page="page"
              :total-pages="totalPages"
              @page-change="setPage"
            />
          </div>
        </div>

      </div>
    </div>

    <!-- Student Detail Modal -->
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div v-if="showModal && selectedStudent" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" @click="showModal = false"></div>
        <div class="relative w-full max-w-lg flex flex-col rounded-2xl bg-white shadow-2xl overflow-hidden">
          
          <div class="flex items-center justify-between border-b border-slate-100 p-6 bg-slate-50">
            <div class="flex items-center gap-3">
              <div class="h-10 w-10 rounded-full bg-slate-200 overflow-hidden">
                <img v-if="selectedStudent.studentAvatarUrl" :src="selectedStudent.studentAvatarUrl" alt="Student avatar" class="h-full w-full object-cover" />
              </div>
              <div>
                <h3 class="font-bold text-slate-900">{{ selectedStudent.studentFullName }}</h3>
                <p class="text-xs text-slate-500">{{ selectedStudent.courseTitle }}</p>
              </div>
            </div>
            <button @click="showModal = false" class="text-slate-400 hover:text-slate-600 transition-colors">
              <Icon icon="mdi:close" class="h-6 w-6" />
            </button>
          </div>
          
          <form @submit.prevent="saveStudentUpdates" class="p-6 space-y-5">
            <div class="grid grid-cols-2 gap-4">
              <SelectField
                v-model="editStatus"
                :label="t('teacher.students.status')"
                :options="statusOptions"
              />
              <SelectField
                v-model="editLevel"
                :label="t('teacher.students.level')"
                :options="levelOptions"
              />
            </div>
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1.5">{{ t('teacher.students.note') }}</label>
              <textarea
                v-model="editNote"
                rows="4"
                placeholder="Приватна замітка викладача..."
                class="block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-amber-500 focus:outline-none focus:ring-1 focus:ring-amber-500"
              ></textarea>
              <p class="mt-1 text-xs text-slate-500">Цю замітку бачите лише ви.</p>
            </div>

            <div class="flex justify-end gap-3 pt-4">
              <AppButton type="button" variant="ghost" @click="showModal = false" :disabled="modalSubmitting">
                {{ t('actions.cancel') }}
              </AppButton>
              <AppButton type="submit" variant="primary" :loading="modalSubmitting">
                Зберегти зміни
              </AppButton>
            </div>
          </form>

        </div>
      </div>
    </Transition>
  </DefaultLayout>
</template>
