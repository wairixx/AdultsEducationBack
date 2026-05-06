<script setup lang="ts">
import { computed } from 'vue'
import { Icon } from '@iconify/vue'

const props = defineProps<{
  currentPage: number
  totalPages: number
}>()

const emit = defineEmits<{
  pageChange: [page: number]
}>()

const pages = computed(() => {
  const current = props.currentPage
  const total = props.totalPages
  
  if (total <= 7) {
    return Array.from({ length: total }, (_, i) => i)
  }

  if (current <= 3) {
    return [0, 1, 2, 3, 4, '...', total - 1]
  }

  if (current >= total - 4) {
    return [0, '...', total - 5, total - 4, total - 3, total - 2, total - 1]
  }

  return [0, '...', current - 1, current, current + 1, '...', total - 1]
})
</script>

<template>
  <div v-if="totalPages > 1" class="flex items-center justify-center space-x-1">
    <!-- Prev -->
    <button
      type="button"
      :disabled="currentPage === 0"
      class="inline-flex h-9 w-9 items-center justify-center rounded-lg border border-slate-200 text-slate-500 transition-colors hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
      @click="emit('pageChange', currentPage - 1)"
    >
      <Icon icon="mdi:chevron-left" class="h-5 w-5" />
    </button>

    <!-- Pages -->
    <template v-for="p in pages" :key="p">
      <span
        v-if="p === '...'"
        class="inline-flex h-9 w-9 items-center justify-center text-slate-400"
      >
        ...
      </span>
      <button
        v-else
        type="button"
        class="inline-flex h-9 w-9 items-center justify-center rounded-lg text-sm font-medium transition-colors"
        :class="
          currentPage === p
            ? 'bg-amber-500 text-white shadow-sm'
            : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
        "
        @click="emit('pageChange', p as number)"
      >
        {{ (p as number) + 1 }}
      </button>
    </template>

    <!-- Next -->
    <button
      type="button"
      :disabled="currentPage >= totalPages - 1"
      class="inline-flex h-9 w-9 items-center justify-center rounded-lg border border-slate-200 text-slate-500 transition-colors hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
      @click="emit('pageChange', currentPage + 1)"
    >
      <Icon icon="mdi:chevron-right" class="h-5 w-5" />
    </button>
  </div>
</template>
