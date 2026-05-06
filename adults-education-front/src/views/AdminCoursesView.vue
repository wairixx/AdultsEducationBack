<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DataTable from '@/components/admin/DataTable.vue'
import ConfirmDialog from '@/components/admin/ConfirmDialog.vue'
import EditModal from '@/components/admin/EditModal.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppInput from '@/components/common/AppInput.vue'
import SearchInput from '@/components/common/SearchInput.vue'
import SelectField from '@/components/common/SelectField.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import {
  getAllCoursesForAdmin,
  setCourseVisibility,
  deleteCourse,
  updateCourse,
  createCourse,
} from '@/api/courses'
import type { CourseResponse, CourseTopic, CourseFormat, CourseRequest, UserResponse } from '@/types/api'
import { getAllUsers } from '@/api/users'

const router = useRouter()
const { t } = useI18n()

const loading = ref(true)
const courses = ref<CourseResponse[]>([])
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// Teachers for dropdown
const teacherOptions = ref<{ value: string | number; label: string }[]>([])

async function loadTeachers() {
  try {
    const res = await getAllUsers({ role: 'TEACHER' }, 0, 200, 'id,asc')
    teacherOptions.value = res.content.map((u: UserResponse) => ({
      value: u.id,
      label: `${u.fullName || u.email} (ID: ${u.id})`,
    }))
  } catch {
    /* ignore */
  }
}

// Filters
const filterTitle = ref('')
const filterTopic = ref<CourseTopic | ''>('')
const filterFormat = ref<CourseFormat | ''>('')

const topicOptions = [
  { value: '', label: t('filters.all') },
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

const formatOptions = [
  { value: '', label: t('filters.all') },
  { value: 'ONLINE', label: t('formats.ONLINE') },
  { value: 'OFFLINE', label: t('formats.OFFLINE') },
  { value: 'HYBRID', label: t('formats.HYBRID') },
  { value: 'SELF_PACED', label: t('formats.SELF_PACED') },
]

const topicEditOptions = topicOptions.filter((o) => o.value !== '')
const formatEditOptions = formatOptions.filter((o) => o.value !== '')

const columns = [
  { key: 'title', label: t('admin.courses.colTitle') },
  { key: 'teacherFullName', label: t('admin.courses.teacher') },
  { key: 'topic', label: t('admin.courses.colTopic') },
  { key: 'format', label: t('admin.courses.colFormat') },
  { key: 'price', label: t('admin.courses.colPrice') },
  { key: 'studentsCount', label: t('admin.courses.colStudents') },
  { key: 'visible', label: t('admin.courses.colStatus') },
  { key: 'actions', label: '' },
]

async function loadData() {
  loading.value = true
  try {
    const res = await getAllCoursesForAdmin(
      {
        title: filterTitle.value || undefined,
        topic: filterTopic.value || undefined,
        format: filterFormat.value || undefined,
      },
      page.value,
      10,
      'createdAt,desc',
    )
    courses.value = res.content
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch {
    toast.error(t('errors.generic'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTeachers()
  loadData()
})

watch([filterTitle, filterTopic, filterFormat], () => {
  page.value = 0
  loadData()
})

function setPage(p: number) {
  page.value = p
  loadData()
}

async function toggleVisible(course: CourseResponse) {
  try {
    const res = await setCourseVisibility(course.id, !course.visible)
    const idx = courses.value.findIndex((c) => c.id === course.id)
    if (idx !== -1) courses.value[idx] = res
    toast.success(t('admin.courses.successUpdate'))
  } catch {
    /* handled by interceptor */
  }
}

// ── Create Modal ──────────────────────────────────────────────────────
const createModalOpen = ref(false)
const createSaving = ref(false)
const createForm = ref<CourseRequest>({
  title: '',
  description: '',
  topic: 'PROGRAMMING',
  format: 'ONLINE',
  price: 0,
  durationHours: 0,
})
const createTopic = ref('PROGRAMMING')
const createFormat = ref('ONLINE')
const createTeacherId = ref<string>('')

function openCreate() {
  createForm.value = {
    title: '',
    description: '',
    topic: 'PROGRAMMING',
    format: 'ONLINE',
    price: 0,
    durationHours: 0,
    coverUrl: '',
  }
  createTopic.value = 'PROGRAMMING'
  createFormat.value = 'ONLINE'
  createTeacherId.value = teacherOptions.value.length > 0 ? String(teacherOptions.value[0]?.value) : ''
  createModalOpen.value = true
}

async function saveCreate() {
  createSaving.value = true
  try {
    await createCourse({
      ...createForm.value,
      topic: createTopic.value as CourseTopic,
      format: createFormat.value as CourseFormat,
      teacherId: createTeacherId.value ? Number(createTeacherId.value) : undefined,
    })
    toast.success(t('admin.courses.successCreate'))
    createModalOpen.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    createSaving.value = false
  }
}

// ── Edit Modal ────────────────────────────────────────────────────────
const editModalOpen = ref(false)
const editingCourse = ref<CourseResponse | null>(null)
const editForm = ref<CourseRequest>({
  title: '',
  description: '',
  topic: 'PROGRAMMING',
  format: 'ONLINE',
  price: 0,
  durationHours: 0,
})
const editSaving = ref(false)

function openEdit(course: CourseResponse) {
  editingCourse.value = course
  editForm.value = {
    title: course.title,
    description: course.description,
    topic: course.topic,
    format: course.format,
    price: course.price,
    durationHours: course.durationHours,
    coverUrl: course.coverUrl,
  }
  editModalOpen.value = true
}

async function saveEdit() {
  if (!editingCourse.value) return
  editSaving.value = true
  try {
    const res = await updateCourse(editingCourse.value.id, editForm.value)
    const idx = courses.value.findIndex((c) => c.id === res.id)
    if (idx !== -1) courses.value[idx] = res
    toast.success(t('admin.courses.successEditSave'))
    editModalOpen.value = false
  } catch {
    /* handled by interceptor */
  } finally {
    editSaving.value = false
  }
}

// ── Delete Confirm ────────────────────────────────────────────────────
const confirmOpen = ref(false)
const courseToDelete = ref<number | null>(null)
const deleteLoading = ref(false)

function openDelete(id: number) {
  courseToDelete.value = id
  confirmOpen.value = true
}

async function executeDelete() {
  if (!courseToDelete.value) return
  deleteLoading.value = true
  try {
    await deleteCourse(courseToDelete.value)
    toast.success(t('admin.courses.successDelete'))
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    deleteLoading.value = false
    confirmOpen.value = false
  }
}
</script>

<template>
  <div class="space-y-6">
    <!-- Header + Filters -->
    <div class="flex flex-col gap-4">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-slate-900">{{ t('admin.courses.title') }}</h1>
          <p class="text-sm text-slate-500 mt-1">
            {{ t('admin.courses.totalCount', { count: totalElements }) }}
          </p>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row sm:items-center gap-3">
        <div class="flex flex-col sm:flex-row gap-3">
          <SearchInput
            v-model="filterTitle"
            :placeholder="t('admin.courses.searchPlaceholder')"
            class="w-full sm:w-72"
          />
          <SelectField v-model="filterTopic" :options="topicOptions" class="w-full sm:w-44" />
          <SelectField v-model="filterFormat" :options="formatOptions" class="w-full sm:w-44" />
        </div>
        <AppButton variant="primary" class="sm:ml-auto" @click="openCreate">
          <Icon icon="mdi:plus" class="h-5 w-5" />
          {{ t('admin.courses.createCourse') }}
        </AppButton>
      </div>
    </div>

    <!-- Table -->
    <DataTable :columns="columns" :data="courses" :loading="loading">
      <template #col-title="{ item }">
        <div class="flex items-center gap-3">
          <div class="h-10 w-14 bg-slate-100 rounded-lg overflow-hidden flex-shrink-0">
            <img
              v-if="item.coverUrl"
              :src="item.coverUrl"
              alt="Course cover"
              class="h-full w-full object-cover"
            />
            <Icon v-else icon="mdi:image-outline" class="h-full w-full p-2 text-slate-300" />
          </div>
          <div>
            <p
              class="font-medium text-slate-900 line-clamp-1 max-w-[180px] text-sm"
              :title="item.title"
            >
              {{ item.title }}
            </p>
            <p class="text-xs text-slate-400">{{ item.durationHours }} {{ t('course.hours') }}</p>
          </div>
        </div>
      </template>

      <template #col-topic="{ item }">
        <span class="text-xs font-medium text-slate-600">{{ t(`topics.${item.topic}`) }}</span>
      </template>

      <template #col-format="{ item }">
        <span
          class="inline-flex rounded-full px-2 py-0.5 text-xs font-medium bg-slate-100 text-slate-700"
        >
          {{ t(`formats.${item.format}`) }}
        </span>
      </template>

      <template #col-price="{ item }">
        <span class="text-sm font-medium">
          {{ item.price === 0 ? t('course.free') : `₴${item.price}` }}
        </span>
      </template>

      <template #col-studentsCount="{ item }">
        <div class="flex items-center gap-1.5 text-sm text-slate-600">
          <Icon icon="mdi:account-group" class="h-4 w-4 text-slate-400" />
          {{ item.studentsCount }}
        </div>
      </template>

      <template #col-visible="{ item }">
        <button
          @click="toggleVisible(item)"
          class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium transition-colors"
          :class="
            item.visible
              ? 'bg-emerald-50 text-emerald-700 hover:bg-emerald-100'
              : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
          "
        >
          <Icon :icon="item.visible ? 'mdi:eye' : 'mdi:eye-off'" class="mr-1 h-3.5 w-3.5" />
          {{ item.visible ? t('admin.courses.visible') : t('admin.courses.hidden') }}
        </button>
      </template>

      <template #col-actions="{ item }">
        <div class="flex justify-end gap-1">
          <button
            @click="router.push(`/courses/${item.id}`)"
            class="p-1.5 text-slate-400 hover:text-sky-600 transition-colors"
            :title="t('admin.courses.view')"
            aria-label="View course"
          >
            <Icon icon="mdi:open-in-new" class="h-5 w-5" />
          </button>
          <button
            @click="openEdit(item)"
            class="p-1.5 text-slate-400 hover:text-amber-600 transition-colors"
            :title="t('actions.edit')"
            aria-label="Edit course"
          >
            <Icon icon="mdi:pencil" class="h-5 w-5" />
          </button>
          <button
            @click="router.push(`/dashboard/courses/${item.id}/lessons`)"
            class="p-1.5 text-slate-400 hover:text-violet-600 transition-colors"
            :title="t('admin.courses.manageLessons')"
            aria-label="Manage lessons"
          >
            <Icon icon="mdi:book-open-page-variant" class="h-5 w-5" />
          </button>
          <button
            @click="openDelete(item.id)"
            class="p-1.5 text-slate-400 hover:text-rose-600 transition-colors"
            :title="t('actions.delete')"
            aria-label="Delete course"
          >
            <Icon icon="mdi:delete" class="h-5 w-5" />
          </button>
        </div>
      </template>
    </DataTable>

    <AppPagination
      v-if="totalPages > 1"
      :current-page="page"
      :total-pages="totalPages"
      @page-change="setPage"
    />

    <!-- Delete Confirm -->
    <ConfirmDialog
      :is-open="confirmOpen"
      :title="t('admin.courses.deleteConfirm')"
      :message="t('admin.courses.deleteMessage')"
      :loading="deleteLoading"
      danger
      @confirm="executeDelete"
      @cancel="confirmOpen = false"
    />

    <!-- ═══ Create Course Modal ═══ -->
    <EditModal
      :is-open="createModalOpen"
      :title="t('admin.courses.createTitle')"
      @close="createModalOpen = false"
    >
      <div class="space-y-4">
        <AppInput v-model="createForm.title" :label="t('teacher.courseForm.title')" required />
        <div>
          <label class="mb-1.5 block text-sm font-medium text-slate-700">
            {{ t('teacher.courseForm.description') }} <span class="text-rose-500">*</span>
          </label>
          <textarea
            v-model="createForm.description"
            rows="4"
            class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm transition-all duration-200 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
          ></textarea>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <SelectField
            v-model="createTopic"
            :label="t('teacher.courseForm.topic')"
            :options="topicEditOptions"
          />
          <SelectField
            v-model="createFormat"
            :label="t('teacher.courseForm.format')"
            :options="formatEditOptions"
          />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="createForm.price"
            :label="t('teacher.courseForm.price')"
            type="number"
          />
          <AppInput
            v-model="createForm.durationHours"
            :label="t('teacher.courseForm.duration')"
            type="number"
          />
        </div>
        <AppInput v-model="createForm.coverUrl" :label="t('admin.courses.coverUrl')" />
        <SelectField
          v-model="createTeacherId"
          :label="t('admin.courses.teacher')"
          :options="teacherOptions"
        />
      </div>
      <template #footer>
        <AppButton variant="ghost" @click="createModalOpen = false">
          {{ t('actions.cancel') }}
        </AppButton>
        <AppButton variant="primary" :loading="createSaving" @click="saveCreate">
          {{ t('actions.create') }}
        </AppButton>
      </template>
    </EditModal>

    <!-- ═══ Edit Course Modal ═══ -->
    <EditModal
      :is-open="editModalOpen"
      :title="t('admin.courses.editTitle')"
      @close="editModalOpen = false"
    >
      <div v-if="editingCourse" class="space-y-4">
        <AppInput v-model="editForm.title" :label="t('teacher.courseForm.title')" />
        <div>
          <label class="mb-1.5 block text-sm font-medium text-slate-700">{{
            t('teacher.courseForm.description')
          }}</label>
          <textarea
            v-model="editForm.description"
            rows="4"
            class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm transition-all duration-200 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
          ></textarea>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <SelectField
            v-model="editForm.topic"
            :label="t('teacher.courseForm.topic')"
            :options="topicEditOptions"
          />
          <SelectField
            v-model="editForm.format"
            :label="t('teacher.courseForm.format')"
            :options="formatEditOptions"
          />
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <AppInput
            v-model="editForm.price"
            :label="t('teacher.courseForm.price')"
            type="number"
          />
          <AppInput
            v-model="editForm.durationHours"
            :label="t('teacher.courseForm.duration')"
            type="number"
          />
        </div>
        <AppInput v-model="editForm.coverUrl" :label="t('admin.courses.coverUrl')" />
      </div>
      <template #footer>
        <AppButton variant="ghost" @click="editModalOpen = false">
          {{ t('actions.cancel') }}
        </AppButton>
        <AppButton variant="primary" :loading="editSaving" @click="saveEdit">
          {{ t('actions.save') }}
        </AppButton>
      </template>
    </EditModal>
  </div>
</template>
