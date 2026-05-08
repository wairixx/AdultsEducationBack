<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppInput from '@/components/common/AppInput.vue'
import { useAuthStore } from '@/stores/auth'
import { getCourseById } from '@/api/courses'
import { getLessonsFull, createLesson, updateLesson, deleteLesson } from '@/api/lessons'
import type { CourseResponse, LessonResponse, LessonRequest } from '@/types/api'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const auth = useAuthStore()

const courseId = Number(route.params.id)
const course = ref<CourseResponse | null>(null)
const lessons = ref<LessonResponse[]>([])
const loading = ref(true)

const showModal = ref(false)
const modalSubmitting = ref(false)
const editMode = ref(false)
const editingLessonId = ref<number | null>(null)

const form = ref<LessonRequest>({
  courseId: courseId,
  title: '',
  content: '',
  videoUrl: '',
  orderNumber: 1
})

function goBackToCourses() {
  if (auth.isAdmin) {
    router.push('/admin/courses')
    return
  }
  router.push('/dashboard')
}

async function loadData() {
  loading.value = true
  try {
    const [cRes, lRes] = await Promise.all([
      getCourseById(courseId),
      getLessonsFull(courseId)
    ])
    course.value = cRes
    lessons.value = lRes.sort((a, b) => a.orderNumber - b.orderNumber)
  } catch {
    goBackToCourses()
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function openCreateModal() {
  form.value = {
    courseId: courseId,
    title: '',
    content: '',
    videoUrl: '',
    orderNumber: lessons.value.length > 0 ? (lessons.value[lessons.value.length - 1]?.orderNumber ?? 0) + 1 : 1
  }
  editMode.value = false
  editingLessonId.value = null
  showModal.value = true
}

function openEditModal(lesson: LessonResponse) {
  form.value = {
    courseId: courseId,
    title: lesson.title,
    content: lesson.content,
    videoUrl: lesson.videoUrl,
    orderNumber: lesson.orderNumber
  }
  editMode.value = true
  editingLessonId.value = lesson.id
  showModal.value = true
}

async function submitLesson() {
  modalSubmitting.value = true
  try {
    if (editMode.value && editingLessonId.value) {
      await updateLesson(editingLessonId.value, form.value)
      toast.success(t('teacher.lessons.successUpdated'))
    } else {
      await createLesson(form.value)
      toast.success(t('teacher.lessons.successCreated'))
    }
    showModal.value = false
    await loadData()
  } catch {
    console.error('An error occurred')
  } finally {
    modalSubmitting.value = false
  }
}

async function handleDelete(id: number) {
  if (!confirm(t('teacher.lessons.deleteConfirm'))) return
  try {
    await deleteLesson(id)
    toast.success(t('teacher.lessons.successDeleted'))
    await loadData()
  } catch {
    console.error('An error occurred')
  }
}
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 py-8 min-h-[calc(100vh-64px)]">
      <div class="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8">
        
        <div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <button @click="goBackToCourses" class="inline-flex items-center text-sm font-medium text-slate-500 hover:text-amber-600 mb-2 transition-colors">
              <Icon icon="mdi:arrow-left" class="mr-1 h-4 w-4" />
              Назад до курсів
            </button>
            <h1 class="text-2xl sm:text-3xl font-bold tracking-tight text-slate-900">
              {{ t('teacher.lessons.title') }}
            </h1>
            <p v-if="course" class="text-sm text-slate-500 mt-1">Курс: <span class="font-medium text-slate-700">{{ course.title }}</span></p>
          </div>
          <AppButton variant="primary" @click="openCreateModal">
            <Icon icon="mdi:plus" class="mr-1.5 h-4.5 w-4.5" />
            {{ t('teacher.lessons.addLesson') }}
          </AppButton>
        </div>

        <!-- Warning block -->
        <div class="mb-6 rounded-xl bg-amber-50 p-4 border border-amber-200 text-amber-800 text-sm flex gap-3 shadow-sm">
          <Icon icon="mdi:alert-circle-outline" class="h-5 w-5 flex-shrink-0 text-amber-600" />
          <p>{{ t('teacher.lessons.reorderWarning') }}</p>
        </div>

        <div v-if="loading" class="flex justify-center py-12">
          <div class="h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
        </div>

        <div v-else class="bg-white rounded-2xl shadow-sm ring-1 ring-slate-200 overflow-hidden">
          <div v-if="lessons.length === 0" class="flex flex-col items-center justify-center p-12 text-center">
            <Icon icon="mdi:file-document-outline" class="h-16 w-16 text-slate-300" />
            <p class="mt-4 text-slate-500">{{ t('teacher.lessons.noLessons') }}</p>
            <AppButton variant="secondary" class="mt-4" @click="openCreateModal">
              {{ t('teacher.lessons.addLesson') }}
            </AppButton>
          </div>
          
          <ul v-else class="divide-y divide-slate-100">
            <li v-for="lesson in lessons" :key="lesson.id" class="flex items-center gap-4 p-4 hover:bg-slate-50 transition-colors group">
              <div class="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-lg bg-slate-100 text-sm font-bold text-slate-500">
                {{ lesson.orderNumber }}
              </div>
              <div class="min-w-0 flex-1">
                <p class="truncate text-sm font-semibold text-slate-900">{{ lesson.title }}</p>
                <div class="flex gap-4 mt-1 text-xs text-slate-500">
                  <span v-if="lesson.videoUrl" class="flex items-center gap-1 text-emerald-600">
                    <Icon icon="mdi:video" class="h-3.5 w-3.5" /> Відео
                  </span>
                  <span v-if="lesson.content" class="flex items-center gap-1 text-blue-600">
                    <Icon icon="mdi:text-box-outline" class="h-3.5 w-3.5" /> Текст
                  </span>
                </div>
              </div>
              <div class="flex flex-shrink-0 items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <button
                  class="rounded-lg p-2 text-slate-400 hover:bg-slate-200 hover:text-slate-700 transition-colors"
                  title="Редагувати"
                  @click="openEditModal(lesson)"
                >
                  <Icon icon="mdi:pencil" class="h-5 w-5" />
                </button>
                <button
                  class="rounded-lg p-2 text-slate-400 hover:bg-rose-100 hover:text-rose-600 transition-colors"
                  title="Видалити"
                  @click="handleDelete(lesson.id)"
                >
                  <Icon icon="mdi:delete" class="h-5 w-5" />
                </button>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Lesson Modal -->
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" @click="showModal = false"></div>
        <div class="relative w-full max-w-2xl max-h-[90vh] flex flex-col rounded-2xl bg-white shadow-2xl overflow-hidden">
          
          <div class="flex items-center justify-between border-b border-slate-100 p-6">
            <h3 class="text-xl font-bold text-slate-900">
              {{ editMode ? t('teacher.lessons.editLesson') : t('teacher.lessons.addLesson') }}
            </h3>
            <button @click="showModal = false" class="text-slate-400 hover:text-slate-600 transition-colors">
              <Icon icon="mdi:close" class="h-6 w-6" />
            </button>
          </div>
          
          <form @submit.prevent="submitLesson" class="flex-1 overflow-y-auto p-6 space-y-6">
            <AppInput
              v-model="form.title"
              :label="t('teacher.lessons.lessonTitle')"
              required
            />
            
            <AppInput
              v-model="form.videoUrl"
              :label="t('teacher.lessons.videoUrl')"
              type="url"
            />
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1.5">{{ t('teacher.lessons.content') }}</label>
              <textarea
                v-model="form.content"
                rows="8"
                class="block w-full font-mono text-sm rounded-lg border border-slate-300 px-3 py-2 text-slate-900 placeholder-slate-400 focus:border-amber-500 focus:outline-none focus:ring-1 focus:ring-amber-500"
              ></textarea>
            </div>
            
            <AppInput
              v-model.number="form.orderNumber"
              :label="t('teacher.lessons.order')"
              type="number"
              min="1"
              required
            />

            <div class="flex justify-end gap-3 pt-4 border-t border-slate-100">
              <AppButton type="button" variant="ghost" @click="showModal = false" :disabled="modalSubmitting">
                {{ t('actions.cancel') }}
              </AppButton>
              <AppButton type="submit" variant="primary" :loading="modalSubmitting">
                {{ t('actions.save') }}
              </AppButton>
            </div>
          </form>

        </div>
      </div>
    </Transition>
  </DefaultLayout>
</template>
