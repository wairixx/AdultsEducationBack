<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DataTable from '@/components/admin/DataTable.vue'
import ConfirmDialog from '@/components/admin/ConfirmDialog.vue'
import EditModal from '@/components/admin/EditModal.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppPagination from '@/components/common/AppPagination.vue'
import SearchInput from '@/components/common/SearchInput.vue'
import SelectField from '@/components/common/SelectField.vue'
import { getAllReviewsForAdmin, setReviewVisibility, deleteReview, createReview } from '@/api/reviews'
import { getAllCoursesForAdmin } from '@/api/courses'
import { getAllUsers } from '@/api/users'
import type { ReviewResponse, CourseResponse, UserResponse } from '@/types/api'

const { t } = useI18n()

const loading = ref(true)
const reviews = ref<ReviewResponse[]>([])
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// Filters
const filterSearch = ref('')
const filterMinRating = ref<string>('')
const filterVisibility = ref<'visible' | 'hidden' | ''>('')

const ratingOptions = [
  { value: '', label: t('filters.all') },
  { value: '1', label: '★ 1+' },
  { value: '2', label: '★★ 2+' },
  { value: '3', label: '★★★ 3+' },
  { value: '4', label: '★★★★ 4+' },
  { value: '5', label: '★★★★★ 5' },
]

const visibilityOptions = [
  { value: '', label: t('filters.all') },
  { value: 'visible', label: t('admin.reviews.visibleLabel') },
  { value: 'hidden', label: t('admin.reviews.hiddenLabel') },
]

const ratingEditOptions = [
  { value: '1', label: '★ 1' },
  { value: '2', label: '★★ 2' },
  { value: '3', label: '★★★ 3' },
  { value: '4', label: '★★★★ 4' },
  { value: '5', label: '★★★★★ 5' },
]

const columns = [
  { key: 'studentFullName', label: t('admin.reviews.student') },
  { key: 'courseTitle', label: t('admin.reviews.course') },
  { key: 'rating', label: t('admin.reviews.rating') },
  { key: 'comment', label: t('admin.reviews.comment') },
  { key: 'visible', label: t('admin.reviews.colStatus') },
  { key: 'createdAt', label: t('admin.reviews.date') },
  { key: 'actions', label: '' },
]

// Courses for dropdown
const coursesList = ref<CourseResponse[]>([])
const courseOptions = ref<{ value: string | number; label: string }[]>([])

// Students for dropdown
const studentOptions = ref<{ value: string | number; label: string }[]>([])

async function loadLookups() {
  try {
    const [coursesRes, usersRes] = await Promise.all([
      getAllCoursesForAdmin(undefined, 0, 200, 'title,asc'),
      getAllUsers({ role: 'STUDENT' }, 0, 200, 'id,asc'),
    ])
    coursesList.value = coursesRes.content
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

async function loadData() {
  loading.value = true
  try {
    const res = await getAllReviewsForAdmin(
      {
        minRating: filterMinRating.value ? Number(filterMinRating.value) : undefined,
      },
      page.value,
      10,
      'createdAt,desc',
    )
    let data = res.content
    if (filterSearch.value.trim()) {
      const q = filterSearch.value.toLowerCase().trim()
      data = data.filter(
        (r) =>
          r.studentFullName.toLowerCase().includes(q) ||
          r.courseTitle.toLowerCase().includes(q) ||
          (r.comment && r.comment.toLowerCase().includes(q)),
      )
    }
    if (filterVisibility.value === 'visible') {
      data = data.filter((r) => r.visible)
    } else if (filterVisibility.value === 'hidden') {
      data = data.filter((r) => !r.visible)
    }
    reviews.value = data
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

watch([filterSearch, filterMinRating, filterVisibility], () => {
  page.value = 0
  loadData()
})

function setPage(p: number) {
  page.value = p
  loadData()
}

async function toggleVisible(review: ReviewResponse) {
  try {
    const res = await setReviewVisibility(review.id, !review.visible)
    const idx = reviews.value.findIndex((r) => r.id === review.id)
    if (idx !== -1) reviews.value[idx] = res
    toast.success(t('admin.reviews.successUpdate'))
  } catch {
    /* handled by interceptor */
  }
}

// ── Create Modal ──────────────────────────────────────────────────────
const createModalOpen = ref(false)
const createSaving = ref(false)
const createCourseId = ref<string>('')
const createStudentId = ref<string>('')
const createRating = ref<string>('5')
const createComment = ref('')

function openCreate() {
  toast.error(t('admin.reviews.createForbidden'))
  return
  createCourseId.value = courseOptions.value.length > 0 ? String(courseOptions.value[0]?.value) : ''
  createStudentId.value = studentOptions.value.length > 0 ? String(studentOptions.value[0]?.value) : ''
  createRating.value = '5'
  createComment.value = ''
  createModalOpen.value = true
}

async function saveCreate() {
  if (!createCourseId.value) {
    toast.error(t('admin.reviews.selectCourseRequired'))
    return
  }
  createSaving.value = true
  try {
    await createReview({
      courseId: Number(createCourseId.value),
      rating: Number(createRating.value),
      comment: createComment.value || undefined,
      studentId: createStudentId.value ? Number(createStudentId.value) : undefined,
    })
    toast.success(t('admin.reviews.successCreate'))
    createModalOpen.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    createSaving.value = false
  }
}

// ── Delete ────────────────────────────────────────────────────────────
const confirmOpen = ref(false)
const reviewToDelete = ref<number | null>(null)
const deleteLoading = ref(false)

function openDelete(id: number) {
  reviewToDelete.value = id
  confirmOpen.value = true
}

async function executeDelete() {
  if (!reviewToDelete.value) return
  deleteLoading.value = true
  try {
    await deleteReview(reviewToDelete.value)
    toast.success(t('admin.reviews.successDelete'))
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    deleteLoading.value = false
    confirmOpen.value = false
  }
}

function renderStars(rating: number) {
  return '★'.repeat(rating) + '☆'.repeat(5 - rating)
}
</script>

<template>
  <div class="space-y-6">
    <!-- Header + Filters -->
    <div class="flex flex-col gap-4">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-slate-900">{{ t('admin.reviews.title') }}</h1>
          <p class="text-sm text-slate-500 mt-1">
            {{ t('admin.reviews.totalCount', { count: totalElements }) }}
          </p>
        </div>
      </div>
      <div class="flex flex-col sm:flex-row gap-3">
        <SearchInput
          v-model="filterSearch"
          :placeholder="t('admin.reviews.searchPlaceholder')"
          class="w-full sm:w-72"
        />
        <SelectField v-model="filterMinRating" :options="ratingOptions" class="w-full sm:w-40" />
        <SelectField
          v-model="filterVisibility"
          :options="visibilityOptions"
          class="w-full sm:w-40"
        />
      </div>
    </div>

    <!-- Table -->
    <DataTable :columns="columns" :data="reviews" :loading="loading">
      <template #col-studentFullName="{ item }">
        <span class="font-medium text-slate-900 text-sm">{{ item.studentFullName }}</span>
      </template>

      <template #col-courseTitle="{ item }">
        <span class="text-sm text-slate-700 line-clamp-1 max-w-[160px]" :title="item.courseTitle">
          {{ item.courseTitle }}
        </span>
      </template>

      <template #col-rating="{ item }">
        <div class="flex items-center gap-1.5">
          <span class="font-bold text-slate-900 text-sm">{{ item.rating }}</span>
          <span class="text-amber-500 text-xs tracking-tight">{{ renderStars(item.rating) }}</span>
        </div>
      </template>

      <template #col-comment="{ item }">
        <p class="text-sm text-slate-600 line-clamp-2 max-w-xs" :title="item.comment">
          {{ item.comment || '—' }}
        </p>
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
          {{ item.visible ? t('admin.reviews.visibleLabel') : t('admin.reviews.hiddenLabel') }}
        </button>
      </template>

      <template #col-createdAt="{ item }">
        <span class="text-sm text-slate-600">
          {{ new Date(item.createdAt).toLocaleDateString() }}
        </span>
      </template>

      <template #col-actions="{ item }">
        <div class="flex justify-end gap-1">
          <button
            @click="openDelete(item.id)"
            class="p-1.5 text-slate-400 hover:text-rose-600 transition-colors"
            :title="t('actions.delete')"
            aria-label="Delete review"
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
      :title="t('admin.reviews.deleteConfirm')"
      :message="t('admin.reviews.deleteMessage')"
      :loading="deleteLoading"
      danger
      @confirm="executeDelete"
      @cancel="confirmOpen = false"
    />

    <!-- ═══ Create Review Modal ═══ -->
    <EditModal
      :is-open="createModalOpen"
      :title="t('admin.reviews.createTitle')"
      @close="createModalOpen = false"
    >
      <div class="space-y-4">
        <SelectField
          v-model="createStudentId"
          :label="t('admin.reviews.student')"
          :options="studentOptions"
          required
        />
        <SelectField
          v-model="createCourseId"
          :label="t('admin.reviews.course')"
          :options="courseOptions"
          required
        />
        <SelectField
          v-model="createRating"
          :label="t('admin.reviews.rating')"
          :options="ratingEditOptions"
        />
        <div>
          <label class="mb-1.5 block text-sm font-medium text-slate-700">
            {{ t('admin.reviews.comment') }}
          </label>
          <textarea
            v-model="createComment"
            rows="4"
            class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm transition-all duration-200 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
            :placeholder="t('reviews.commentPlaceholder')"
          ></textarea>
        </div>
        <p class="text-xs text-slate-400">
          Відгук буде створено від імені обраного студента.
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
  </div>
</template>
