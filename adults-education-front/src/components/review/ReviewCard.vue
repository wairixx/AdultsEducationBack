<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import StarRating from './StarRating.vue'
import AppButton from '@/components/common/AppButton.vue'
import type { ReviewResponse } from '@/types/api'
import { useAuthStore } from '@/stores/auth'

const props = defineProps<{
  review: ReviewResponse
  deleting?: boolean
}>()

const emit = defineEmits<{
  delete: [id: number]
}>()

useI18n()
const auth = useAuthStore()

const canDelete = props.review.studentId === auth.user?.id || auth.isAdmin

function initials(name: string) {
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .substring(0, 2)
    .toUpperCase()
}
</script>

<template>
  <div class="rounded-xl border border-slate-100 bg-white p-5 shadow-sm">
    <div class="flex items-start justify-between">
      <div class="flex items-center gap-3">
        <!-- Avatar placeholder -->
        <div
          class="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100 text-sm font-semibold text-slate-600"
        >
          {{ initials(review.studentFullName) }}
        </div>
        <div>
          <h4 class="text-sm font-medium text-slate-900">{{ review.studentFullName }}</h4>
          <div class="mt-0.5 flex items-center gap-2">
            <StarRating :model-value="review.rating" readonly size="sm" />
            <span class="text-xs text-slate-400">
              {{ new Date(review.createdAt).toLocaleDateString() }}
            </span>
          </div>
        </div>
      </div>

      <AppButton
        v-if="canDelete"
        variant="ghost"
        size="sm"
        :loading="deleting"
        class="!p-1.5 text-rose-500 hover:bg-rose-50 hover:text-rose-600"
        @click="emit('delete', review.id)"
      >
        <Icon icon="mdi:trash-can-outline" class="h-4.5 w-4.5" />
      </AppButton>
    </div>

    <p v-if="review.comment" class="mt-4 text-sm text-slate-600 whitespace-pre-line">
      {{ review.comment }}
    </p>
  </div>
</template>
