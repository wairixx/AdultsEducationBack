<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import { Icon } from '@iconify/vue'
import type { CourseResponse } from '@/types/api'
import StarRating from '@/components/review/StarRating.vue'

defineProps<{
  course: CourseResponse
}>()

const { t } = useI18n()
</script>

<template>
  <RouterLink
    :to="`/courses/${course.id}`"
    class="group flex h-full flex-col overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-slate-100 transition-all hover:shadow-xl hover:ring-amber-500/30"
  >
    <!-- Cover -->
    <div class="relative aspect-video w-full overflow-hidden bg-slate-100">
      <img
        v-if="course.coverUrl"
        :src="course.coverUrl"
        :alt="course.title"
        class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-105"
      />
      <div
        v-else
        class="flex h-full w-full items-center justify-center bg-gradient-to-br from-amber-400 via-amber-500 to-violet-600 transition-transform duration-500 group-hover:scale-105"
      >
        <Icon icon="mdi:school-outline" class="h-16 w-16 text-white/50" />
      </div>

      <!-- Topic badge -->
      <div class="absolute left-3 top-3 rounded-full bg-white/90 px-2.5 py-1 text-xs font-semibold text-slate-800 backdrop-blur-sm">
        {{ t(`topics.${course.topic}`) }}
      </div>
    </div>

    <!-- Body -->
    <div class="flex flex-1 flex-col p-5">
      <h3 class="line-clamp-2 text-lg font-bold leading-tight text-slate-900 group-hover:text-amber-600">
        {{ course.title }}
      </h3>
      
      <p class="mt-2 line-clamp-2 text-sm text-slate-500">
        {{ course.description }}
      </p>

      <div class="mt-auto pt-4">
        <!-- Teacher & Rating -->
        <div class="flex items-center justify-between border-b border-slate-100 pb-4">
          <div class="flex items-center gap-2">
            <div class="flex h-6 w-6 overflow-hidden items-center justify-center rounded-full bg-slate-100 text-xs font-semibold text-slate-600">
              <img v-if="course.teacherAvatarUrl" :src="course.teacherAvatarUrl" alt="" class="h-full w-full object-cover" />
              <span v-else>{{ course.teacherFullName.charAt(0).toUpperCase() }}</span>
            </div>
            <span class="text-xs font-medium text-slate-600">{{ course.teacherFullName }}</span>
          </div>
          <div class="flex items-center gap-1">
            <StarRating :model-value="course.averageRating" readonly size="sm" />
            <span class="text-xs font-medium text-amber-500">{{ course.averageRating.toFixed(1) }}</span>
            <span class="text-xs text-slate-400">({{ course.reviewsCount }})</span>
          </div>
        </div>

        <!-- Footer stats -->
        <div class="mt-4 flex items-center justify-between">
          <div class="text-lg font-bold text-slate-900">
            {{ course.price === 0 ? t('course.free') : `${course.price} ${t('course.price')}` }}
          </div>
          <div class="flex items-center gap-3 text-xs text-slate-500">
            <span class="flex items-center gap-1">
              <Icon icon="mdi:clock-outline" class="h-4 w-4" />
              {{ course.durationHours }} {{ t('course.hours') }}
            </span>
            <span class="flex items-center gap-1">
              <Icon icon="mdi:format-list-bulleted" class="h-4 w-4" />
              {{ course.lessonsCount }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </RouterLink>
</template>
