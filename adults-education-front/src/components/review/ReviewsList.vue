<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { toast } from 'vue-sonner'
import axios from 'axios'
import type { ReviewResponse, ApiError } from '@/types/api'
import { getAllReviews, createReview, deleteReview } from '@/api/reviews'
import { getMyEducationsAsStudent } from '@/api/educations'
import { useAuthStore } from '@/stores/auth'

import ReviewCard from './ReviewCard.vue'
import StarRating from './StarRating.vue'
import SkeletonLoader from '@/components/common/SkeletonLoader.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppPagination from '@/components/common/AppPagination.vue'

const props = defineProps<{
  courseId: number
}>()

const emit = defineEmits<{
  reviewAdded: []
  reviewDeleted: []
}>()

const { t } = useI18n()
const auth = useAuthStore()

const reviews = ref<ReviewResponse[]>([])
const loading = ref(false)
const totalPages = ref(0)
const currentPage = ref(0)

const isEnrolled = ref(false)
const hasReviewed = ref(false)
const canLeaveReview = computed(() => isEnrolled.value && !hasReviewed.value)

const newReview = ref({ rating: 0, comment: '' })
const submitting = ref(false)
const deletingId = ref<number | null>(null)
const createApiError = ref('')

async function loadReviews(page = 0) {
  loading.value = true
  try {
    const res = await getAllReviews({ courseId: props.courseId }, page, 5, 'id,desc')
    reviews.value = res.content
    totalPages.value = res.totalPages
    currentPage.value = res.number
  } catch {
    console.error('An error occurred')
  } finally {
    loading.value = false
  }
}

async function checkEnrollment() {
  if (!auth.isStudent) return
  try {
    const res = await getMyEducationsAsStudent({ courseId: props.courseId }, 0, 1)
    const education = res.content[0]
    isEnrolled.value =
      res.totalElements > 0 && (education?.status === 'ACTIVE' || education?.status === 'COMPLETED')
  } catch {
    console.error('An error occurred')
  }
}

async function checkHasReviewed() {
  if (!auth.user?.id) return
  try {
    const res = await getAllReviews({ courseId: props.courseId, studentId: auth.user.id }, 0, 1)
    hasReviewed.value = res.totalElements > 0
  } catch {
    // ignore and keep fallback from POST 409
  }
}

onMounted(() => {
  loadReviews()
  checkEnrollment()
  checkHasReviewed()
})

async function submitReview() {
  if (newReview.value.rating === 0) {
    toast.error(t('reviews.ratingRequired'))
    return
  }

  createApiError.value = ''
  submitting.value = true
  try {
    await createReview({
      courseId: props.courseId,
      rating: newReview.value.rating,
      comment: newReview.value.comment || undefined,
    }, { skipToast: true })
    toast.success(t('reviews.success'))
    newReview.value = { rating: 0, comment: '' }
    hasReviewed.value = true
    emit('reviewAdded')
    await loadReviews(0)
  } catch (err) {
    if (axios.isAxiosError(err) && err.response?.status === 409) {
      hasReviewed.value = true
    } else if (axios.isAxiosError(err) && err.response?.data) {
      createApiError.value = (err.response.data as ApiError).message || t('errors.generic')
    } else {
      createApiError.value = t('errors.network')
    }
  } finally {
    submitting.value = false
  }
}

async function removeReview(id: number) {
  deletingId.value = id
  try {
    await deleteReview(id)
    toast.success(t('reviews.deleted'))
    if (auth.user?.id) {
      const deletedOwn = reviews.value.find((r) => r.id === id)?.studentId === auth.user.id
      if (deletedOwn) hasReviewed.value = false
    }
    emit('reviewDeleted')
    await Promise.all([loadReviews(currentPage.value), checkHasReviewed()])
  } catch {
    // Error handled by global interceptor
  } finally {
    deletingId.value = null
  }
}
</script>

<template>
  <div class="space-y-6">
    <!-- Leave review form -->
    <div
      v-if="canLeaveReview"
      class="rounded-2xl border border-amber-100 bg-amber-50 p-6"
    >
      <h3 class="mb-4 text-lg font-semibold text-slate-900">{{ t('reviews.leaveReview') }}</h3>
      <form @submit.prevent="submitReview" class="space-y-4">
        <div v-if="createApiError" class="flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3">
          <Icon icon="mdi:alert-circle-outline" class="mt-0.5 h-5 w-5 shrink-0 text-rose-500" />
          <p class="text-sm text-rose-700">{{ createApiError }}</p>
        </div>
        <div>
          <label class="mb-2 block text-sm font-medium text-slate-700">{{ t('reviews.rating') }}</label>
          <StarRating v-model="newReview.rating" size="lg" />
        </div>
        <div>
          <label class="mb-2 block text-sm font-medium text-slate-700">{{ t('reviews.comment') }}</label>
          <textarea
            v-model="newReview.comment"
            rows="3"
            class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm focus:border-amber-500 focus:outline-none focus:ring-1 focus:ring-amber-500"
            :placeholder="t('reviews.commentPlaceholder')"
          ></textarea>
        </div>
        <AppButton type="submit" :loading="submitting" :disabled="newReview.rating === 0">
          {{ t('actions.submit') }}
        </AppButton>
      </form>
    </div>
    <div v-else-if="hasReviewed" class="rounded-2xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-600">
      {{ t('reviews.alreadyReviewed') }}
    </div>

    <!-- Reviews list -->
    <div v-if="loading" class="space-y-4">
      <SkeletonLoader v-for="i in 3" :key="i" :rows="2" />
    </div>
    
    <div v-else-if="reviews.length === 0" class="py-8 text-center text-slate-500">
      {{ t('reviews.empty') }}
    </div>

    <div v-else class="space-y-4">
      <ReviewCard
        v-for="review in reviews"
        :key="review.id"
        :review="review"
        :deleting="deletingId === review.id"
        @delete="removeReview"
      />
      
      <div class="mt-6 flex justify-center">
        <AppPagination
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="loadReviews"
        />
      </div>
    </div>
  </div>
</template>
