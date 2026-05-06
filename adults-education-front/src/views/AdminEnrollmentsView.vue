<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DataTable from '@/components/admin/DataTable.vue'
import ConfirmDialog from '@/components/admin/ConfirmDialog.vue'
import EditModal from '@/components/admin/EditModal.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppInput from '@/components/common/AppInput.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import SelectField from '@/components/common/SelectField.vue'
import SearchInput from '@/components/common/SearchInput.vue'
import { getAllEducations, updateEducationByTeacher } from '@/api/educations'
import { enroll } from '@/api/enrollments'
import { getAllCoursesForAdmin } from '@/api/courses'
import { getAllUsers } from '@/api/users'
import type {
  EducationResponse,
  EducationStatus,
  EducationLevel,
  UpdateEducationByTeacherRequest,
  CourseResponse,
  UserResponse,
  PaymentMethod,
} from '@/types/api'

const { t } = useI18n()

const loading = ref(true)
const enrollments = ref<EducationResponse[]>([])
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// Filters
const filterStatus = ref<EducationStatus | ''>('')
const filterSearch = ref('')

const statusOptions = [
  { value: '', label: t('filters.all') },
  { value: 'PENDING', label: t('statuses.PENDING') },
  { value: 'ACTIVE', label: t('statuses.ACTIVE') },
  { value: 'COMPLETED', label: t('statuses.COMPLETED') },
  { value: 'CANCELLED', label: t('statuses.CANCELLED') },
]

const statusEditOptions = statusOptions.filter((o) => o.value !== '')

const levelOptions = [
  { value: '', label: '—' },
  { value: 'BEGINNER', label: t('levels.BEGINNER') },
  { value: 'INTERMEDIATE', label: t('levels.INTERMEDIATE') },
  { value: 'ADVANCED', label: t('levels.ADVANCED') },
  { value: 'EXPERT', label: t('levels.EXPERT') },
]

const paymentMethodOptions = [
  { value: 'FREE', label: t('admin.enrollments.methodFree') },
  { value: 'CARD', label: t('course.methodCard') },
  { value: 'BANK_TRANSFER', label: t('course.methodBank') },
]

const columns = [
  { key: 'studentFullName', label: t('admin.enrollments.colStudent') },
  { key: 'courseTitle', label: t('admin.enrollments.colCourse') },
  { key: 'status', label: t('admin.enrollments.colStatus') },
  { key: 'progress', label: t('admin.enrollments.colProgress') },
  { key: 'level', label: t('admin.enrollments.colLevel') },
  { key: 'enrolledDate', label: t('admin.enrollments.colDate') },
  { key: 'actions', label: '' },
]

// Lookups for dropdowns
const courseOptions = ref<{ value: string | number; label: string }[]>([])
const studentOptions = ref<{ value: string | number; label: string }[]>([])

async function loadLookups() {
  try {
    const [coursesRes, usersRes] = await Promise.all([
      getAllCoursesForAdmin(undefined, 0, 200, 'title,asc'),
      getAllUsers({ role: 'STUDENT' }, 0, 200, 'id,asc'),
    ])
    courseOptions.value = coursesRes.content.map((c: CourseResponse) => ({
      value: c.id,
      label: c.title,
    }))
    studentOptions.value = usersRes.content.map((u: UserResponse) => ({
      value: u.id,
      label: `${u.fullName || u.email} (ID: ${u.id})`,
    }))
  } catch {
    /* ignore */
  }
}

// Fetch data
async function loadData() {
  loading.value = true
  try {
    const res = await getAllEducations(
      { status: filterStatus.value || undefined },
      page.value,
      10,
      'enrolledDate,desc',
    )
    let data = res.content
    if (filterSearch.value.trim()) {
      const q = filterSearch.value.toLowerCase().trim()
      data = data.filter(
        (e) =>
          e.studentFullName.toLowerCase().includes(q) ||
          e.courseTitle.toLowerCase().includes(q) ||
          (e.note && e.note.toLowerCase().includes(q)),
      )
    }
    enrollments.value = data
    totalPages.value = res.totalPages
    totalElements.value = res.totalElements
  } catch {
    toast.error(t('errors.generic'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLookups()
  loadData()
})

watch([filterStatus, filterSearch], () => {
  page.value = 0
  loadData()
})

function setPage(p: number) {
  page.value = p
  loadData()
}

// Quick status change
async function changeStatus(enrollment: EducationResponse, newStatus: EducationStatus) {
  try {
    const res = await updateEducationByTeacher(enrollment.id, { status: newStatus })
    const idx = enrollments.value.findIndex((e) => e.id === enrollment.id)
    if (idx !== -1) enrollments.value[idx] = res
    toast.success(t('admin.enrollments.successUpdate'))
  } catch {
    /* handled by interceptor */
  }
}

// ── Create Modal ──────────────────────────────────────────────────────
const createModalOpen = ref(false)
const createSaving = ref(false)
const createCourseId = ref<string>('')
const createStudentId = ref<string>('')
const createPaymentMethod = ref<string>('FREE')
const createTransactionRef = ref('')

function openCreate() {
  createCourseId.value = courseOptions.value.length > 0 ? String(courseOptions.value[0]?.value) : ''
  createStudentId.value = studentOptions.value.length > 0 ? String(studentOptions.value[0]?.value) : ''
  createPaymentMethod.value = 'FREE'
  createTransactionRef.value = ''
  createModalOpen.value = true
}

async function saveCreate() {
  if (!createCourseId.value) {
    toast.error(t('admin.enrollments.selectCourseRequired'))
    return
  }
  createSaving.value = true
  try {
    await enroll({
      courseId: Number(createCourseId.value),
      paymentMethod: createPaymentMethod.value as PaymentMethod,
      transactionRef: createTransactionRef.value || undefined,
      studentId: createStudentId.value ? Number(createStudentId.value) : undefined,
    })
    toast.success(t('admin.enrollments.successCreate'))
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
const editingEnrollment = ref<EducationResponse | null>(null)
const editStatus = ref<string>('ACTIVE')
const editLevel = ref<string>('')
const editNote = ref('')
const editSaving = ref(false)

function openEdit(enrollment: EducationResponse) {
  editingEnrollment.value = enrollment
  editStatus.value = enrollment.status
  editLevel.value = enrollment.level ?? ''
  editNote.value = enrollment.note ?? ''
  editModalOpen.value = true
}

async function saveEdit() {
  if (!editingEnrollment.value) return
  editSaving.value = true
  try {
    const payload: UpdateEducationByTeacherRequest = {
      status: editStatus.value as EducationStatus,
      level: editLevel.value ? (editLevel.value as EducationLevel) : undefined,
      note: editNote.value || undefined,
    }
    const res = await updateEducationByTeacher(editingEnrollment.value.id, payload)
    const idx = enrollments.value.findIndex((e) => e.id === res.id)
    if (idx !== -1) enrollments.value[idx] = res
    toast.success(t('admin.enrollments.successUpdate'))
    editModalOpen.value = false
  } catch {
    /* handled by interceptor */
  } finally {
    editSaving.value = false
  }
}

// Cancel confirm
const confirmOpen = ref(false)
const enrollmentToCancel = ref<EducationResponse | null>(null)

function openCancel(enrollment: EducationResponse) {
  enrollmentToCancel.value = enrollment
  confirmOpen.value = true
}

async function executeCancel() {
  if (!enrollmentToCancel.value) return
  await changeStatus(enrollmentToCancel.value, 'CANCELLED')
  confirmOpen.value = false
}

// Status badge styling
function statusClass(status: EducationStatus) {
  return {
    'bg-orange-100 text-orange-800': status === 'PENDING',
    'bg-amber-100 text-amber-800': status === 'ACTIVE',
    'bg-emerald-100 text-emerald-800': status === 'COMPLETED',
    'bg-slate-100 text-slate-800': status === 'CANCELLED',
  }
}
</script>

<template>
  <div class="space-y-6">
    <!-- Header + Filters -->
    <div class="flex flex-col gap-4">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-slate-900">{{ t('admin.enrollments.title') }}</h1>
          <p class="text-sm text-slate-500 mt-1">
            {{ t('admin.enrollments.totalCount', { count: totalElements }) }}
          </p>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row gap-3">
        <SearchInput
          v-model="filterSearch"
          :placeholder="t('admin.enrollments.searchPlaceholder')"
          class="w-full sm:w-72"
        />
        <SelectField v-model="filterStatus" :options="statusOptions" class="w-full sm:w-48" />
      </div>
    </div>

    <!-- Table -->
    <DataTable :columns="columns" :data="enrollments" :loading="loading">
      <template #col-studentFullName="{ item }">
        <div class="flex items-center gap-2">
          <div
            class="h-8 w-8 rounded-full bg-slate-100 flex items-center justify-center flex-shrink-0 overflow-hidden"
          >
            <img
              v-if="item.studentAvatarUrl"
              :src="item.studentAvatarUrl"
              alt=""
              class="h-full w-full object-cover"
            />
            <Icon v-else icon="mdi:account" class="h-5 w-5 text-slate-400" />
          </div>
          <span class="font-medium text-slate-900 text-sm">{{ item.studentFullName }}</span>
        </div>
      </template>

      <template #col-courseTitle="{ item }">
        <span class="text-sm text-slate-700 line-clamp-1 max-w-[160px]" :title="item.courseTitle">
          {{ item.courseTitle }}
        </span>
      </template>

      <template #col-status="{ item }">
        <span
          class="inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium"
          :class="statusClass(item.status)"
        >
          {{ t(`statuses.${item.status}`) }}
        </span>
      </template>

      <template #col-progress="{ item }">
        <div class="flex items-center gap-2">
          <div class="h-1.5 w-16 bg-slate-100 rounded-full overflow-hidden">
            <div
              class="h-full rounded-full transition-all duration-300"
              :class="item.progress >= 100 ? 'bg-emerald-500' : 'bg-amber-500'"
              :style="{ width: `${item.progress}%` }"
            ></div>
          </div>
          <span class="text-xs text-slate-500 font-medium">{{ item.progress }}%</span>
        </div>
      </template>

      <template #col-level="{ item }">
        <span v-if="item.level" class="text-xs font-medium text-slate-600">
          {{ t(`levels.${item.level}`) }}
        </span>
        <span v-else class="text-xs text-slate-400">—</span>
      </template>

      <template #col-enrolledDate="{ item }">
        <span class="text-sm text-slate-600">
          {{ new Date(item.enrolledDate).toLocaleDateString() }}
        </span>
      </template>

      <template #col-actions="{ item }">
        <div class="flex justify-end gap-1">
          <button
            @click="openEdit(item)"
            class="p-1.5 text-slate-400 hover:text-amber-600 transition-colors"
            :title="t('actions.edit')"
            aria-label="Edit enrollment"
          >
            <Icon icon="mdi:pencil" class="h-5 w-5" />
          </button>
          <button
            v-if="item.status === 'PENDING'"
            @click="changeStatus(item, 'ACTIVE')"
            class="p-1.5 text-slate-400 hover:text-emerald-600 transition-colors"
            :title="t('admin.enrollments.activate')"
            aria-label="Activate"
          >
            <Icon icon="mdi:check-circle-outline" class="h-5 w-5" />
          </button>
          <button
            v-if="item.status === 'ACTIVE'"
            @click="changeStatus(item, 'COMPLETED')"
            class="p-1.5 text-slate-400 hover:text-emerald-600 transition-colors"
            :title="t('admin.enrollments.complete')"
            aria-label="Complete"
          >
            <Icon icon="mdi:check-all" class="h-5 w-5" />
          </button>
          <button
            v-if="item.status !== 'CANCELLED' && item.status !== 'COMPLETED'"
            @click="openCancel(item)"
            class="p-1.5 text-slate-400 hover:text-rose-600 transition-colors"
            :title="t('admin.enrollments.cancel')"
            aria-label="Cancel enrollment"
          >
            <Icon icon="mdi:close-circle-outline" class="h-5 w-5" />
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

    <!-- Cancel Confirm -->
    <ConfirmDialog
      :is-open="confirmOpen"
      :title="t('admin.enrollments.cancelConfirm')"
      :message="t('admin.enrollments.cancelMessage')"
      danger
      @confirm="executeCancel"
      @cancel="confirmOpen = false"
    />

    <!-- ═══ Create Enrollment Modal ═══ -->
    <EditModal
      :is-open="createModalOpen"
      :title="t('admin.enrollments.createTitle')"
      @close="createModalOpen = false"
    >
      <div class="space-y-4">
        <SelectField
          v-model="createStudentId"
          :label="t('admin.enrollments.colStudent')"
          :options="studentOptions"
          required
        />
        <SelectField
          v-model="createCourseId"
          :label="t('admin.enrollments.colCourse')"
          :options="courseOptions"
          required
        />
        <SelectField
          v-model="createPaymentMethod"
          :label="t('course.paymentMethod')"
          :options="paymentMethodOptions"
        />
        <AppInput
          v-if="createPaymentMethod !== 'FREE'"
          v-model="createTransactionRef"
          :label="t('course.bankRef')"
          :placeholder="t('course.bankRefPlaceholder')"
        />
        <p class="text-xs text-slate-400">
          Запис буде створено від імені обраного студента.
        </p>
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

    <!-- ═══ Edit Enrollment Modal ═══ -->
    <EditModal
      :is-open="editModalOpen"
      :title="t('admin.enrollments.editTitle')"
      @close="editModalOpen = false"
    >
      <div v-if="editingEnrollment" class="space-y-4">
        <div class="rounded-xl bg-slate-50 p-4 space-y-1">
          <p class="text-sm text-slate-500">
            {{ t('admin.enrollments.colStudent') }}:
            <span class="font-medium text-slate-900">{{
              editingEnrollment.studentFullName
            }}</span>
          </p>
          <p class="text-sm text-slate-500">
            {{ t('admin.enrollments.colCourse') }}:
            <span class="font-medium text-slate-900">{{ editingEnrollment.courseTitle }}</span>
          </p>
        </div>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <SelectField
            v-model="editStatus"
            :label="t('admin.enrollments.colStatus')"
            :options="statusEditOptions"
          />
          <SelectField
            v-model="editLevel"
            :label="t('admin.enrollments.colLevel')"
            :options="levelOptions"
          />
        </div>
        <div>
          <label class="mb-1.5 block text-sm font-medium text-slate-700">{{
            t('admin.enrollments.note')
          }}</label>
          <textarea
            v-model="editNote"
            rows="3"
            class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm transition-all duration-200 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
            :placeholder="t('admin.enrollments.notePlaceholder')"
          ></textarea>
        </div>
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
