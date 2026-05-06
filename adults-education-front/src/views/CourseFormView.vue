<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppInput from '@/components/common/AppInput.vue'
import SelectField from '@/components/common/SelectField.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import { createCourse, getCourseById, updateCourse, setCourseVisibility } from '@/api/courses'
import { uploadCourseImage } from '@/api/files'
import { useAuthStore } from '@/stores/auth'
import type { CourseRequest } from '@/types/api'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const isEditing = computed(() => !!route.params.id)
const courseId = computed(() => Number(route.params.id))
const auth = useAuthStore()
const isAdminContext = computed(() => route.path.startsWith('/admin'))

const loading = ref(isEditing.value)
const submitting = ref(false)
const imageUploading = ref(false)

const form = ref<CourseRequest>({
  title: '',
  description: '',
  topic: 'PROGRAMMING',
  format: 'ONLINE',
  price: 0,
  durationHours: 0,
  coverUrl: ''
})

const isVisible = ref(false)

const topics = [
  { value: 'PROGRAMMING', label: t('topics.PROGRAMMING') },
  { value: 'DESIGN', label: t('topics.DESIGN') },
  { value: 'MARKETING', label: t('topics.MARKETING') },
  { value: 'BUSINESS', label: t('topics.BUSINESS') },
  { value: 'LANGUAGES', label: t('topics.LANGUAGES') },
  { value: 'SCIENCE', label: t('topics.SCIENCE') },
  { value: 'ARTS', label: t('topics.ARTS') },
  { value: 'HEALTH', label: t('topics.HEALTH') },
  { value: 'FINANCE', label: t('topics.FINANCE') },
  { value: 'OTHER', label: t('topics.OTHER') },
]

const formats = [
  { value: 'ONLINE', label: t('formats.ONLINE') },
  { value: 'OFFLINE', label: t('formats.OFFLINE') },
  { value: 'HYBRID', label: t('formats.HYBRID') },
  { value: 'SELF_PACED', label: t('formats.SELF_PACED') },
]

async function loadCourse() {
  if (!isEditing.value) return
  loading.value = true
  try {
    const res = await getCourseById(courseId.value)
    form.value = {
      title: res.title,
      description: res.description,
      topic: res.topic,
      format: res.format,
      price: res.price,
      durationHours: res.durationHours,
      coverUrl: res.coverUrl || ''
    }
    isVisible.value = res.visible
  } catch {
    router.replace(isAdminContext.value ? '/admin/courses' : '/dashboard')
  } finally {
    loading.value = false
  }
}

onMounted(loadCourse)

async function handleImageUpload(e: Event) {
  const target = e.target as HTMLInputElement
  if (!target.files?.length) return
  const file = target.files[0]
  if (!file) return
  
  if (!file.type.startsWith('image/')) {
    toast.error('Only images are allowed')
    return
  }
  
  imageUploading.value = true
  try {
    const res = await uploadCourseImage(file)
    form.value.coverUrl = res.url
  } catch {
    console.error('An error occurred')
  } finally {
    imageUploading.value = false
  }
}

async function submit() {
  submitting.value = true
  try {
    if (isEditing.value) {
      await updateCourse(courseId.value, form.value)
      await setCourseVisibility(courseId.value, isVisible.value)
      toast.success(t('teacher.courseForm.successUpdated'))
    } else {
      await createCourse(form.value)
      toast.success(t('teacher.courseForm.successCreated'))
    }
    router.push(isAdminContext.value ? '/admin/courses' : '/dashboard')
  } catch {
    console.error('An error occurred')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 py-8 min-h-[calc(100vh-64px)]">
      <div class="mx-auto max-w-3xl px-4 sm:px-6 lg:px-8">
        
        <div class="mb-6">
          <button @click="router.back()" class="inline-flex items-center text-sm font-medium text-slate-500 hover:text-amber-600 mb-4 transition-colors">
            <Icon icon="mdi:arrow-left" class="mr-1 h-4 w-4" />
            Назад
          </button>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">
            {{ isEditing ? t('teacher.courseForm.editTitle') : t('teacher.courseForm.createTitle') }}
          </h1>
        </div>

        <div v-if="loading" class="space-y-6">
          <SkeletonLoader type="card" />
        </div>

        <form v-else @submit.prevent="submit" class="bg-white rounded-2xl shadow-sm ring-1 ring-slate-200 overflow-hidden">
          
          <!-- Image upload -->
          <div class="relative w-full aspect-video bg-slate-100 group">
            <img v-if="form.coverUrl" :src="form.coverUrl" alt="Course cover" class="w-full h-full object-cover" />
            <div v-else class="w-full h-full flex flex-col items-center justify-center text-slate-400">
              <Icon icon="mdi:image-plus" class="h-12 w-12 mb-2" />
              <span class="text-sm font-medium">{{ t('teacher.courseForm.coverImage') }}</span>
            </div>
            
            <label class="absolute inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer text-white font-medium">
              <Icon v-if="imageUploading" icon="mdi:loading" class="h-8 w-8 animate-spin" />
              <span v-else class="flex items-center"><Icon icon="mdi:upload" class="mr-2 h-5 w-5" /> Завантажити фото</span>
              <input type="file" class="hidden" accept="image/*" @change="handleImageUpload" :disabled="imageUploading" />
            </label>
          </div>

          <div class="p-6 space-y-6">
            <AppInput
              v-model="form.title"
              :label="t('teacher.courseForm.title')"
              required
            />
            
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1.5">{{ t('teacher.courseForm.description') }}</label>
              <textarea
                v-model="form.description"
                rows="4"
                class="block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 placeholder-slate-400 focus:border-amber-500 focus:outline-none focus:ring-1 focus:ring-amber-500 disabled:bg-slate-50 disabled:text-slate-500"
                required
              ></textarea>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <SelectField
                v-model="form.topic"
                :label="t('teacher.courseForm.topic')"
                :options="topics"
              />
              <SelectField
                v-model="form.format"
                :label="t('teacher.courseForm.format')"
                :options="formats"
              />
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <AppInput
                v-model.number="form.price"
                type="number"
                :label="t('teacher.courseForm.price')"
                min="0"
                required
              />
              <AppInput
                v-model.number="form.durationHours"
                type="number"
                :label="t('teacher.courseForm.duration')"
                min="1"
                required
              />
            </div>

            <div v-if="isEditing" class="flex items-center p-4 rounded-xl border border-slate-200 bg-slate-50">
              <input
                id="visibility"
                type="checkbox"
                v-model="isVisible"
                class="h-4 w-4 rounded border-slate-300 text-amber-600 focus:ring-amber-600"
              />
              <label for="visibility" class="ml-3 block text-sm font-medium text-slate-900">
                {{ t('teacher.courseForm.visibility') }}
                <span class="block text-xs font-normal text-slate-500">Якщо вимкнено, студенти не зможуть знайти цей курс у каталозі.</span>
              </label>
            </div>
          </div>
          
          <div class="bg-slate-50 p-6 border-t border-slate-200 flex justify-end gap-3">
            <AppButton type="button" variant="ghost" @click="router.back()" :disabled="submitting">
              {{ t('actions.cancel') }}
            </AppButton>
            <AppButton type="submit" variant="primary" :loading="submitting">
              {{ t('actions.save') }}
            </AppButton>
          </div>
        </form>

      </div>
    </div>
  </DefaultLayout>
</template>
