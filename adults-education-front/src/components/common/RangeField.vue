<script setup lang="ts">
import { useDebounceFn } from '@vueuse/core'
import { ref, watch } from 'vue'

const props = defineProps<{
  min?: number
  max?: number
  label?: string
  placeholderMin?: string
  placeholderMax?: string
}>()

const emit = defineEmits<{
  'update:min': [value?: number]
  'update:max': [value?: number]
}>()

const localMin = ref<number | ''>(props.min ?? '')
const localMax = ref<number | ''>(props.max ?? '')

// Sync from props
watch(() => props.min, (val) => { localMin.value = val ?? '' })
watch(() => props.max, (val) => { localMax.value = val ?? '' })

const emitMin = useDebounceFn(() => {
  emit('update:min', localMin.value === '' ? undefined : Number(localMin.value))
}, 300)

const emitMax = useDebounceFn(() => {
  emit('update:max', localMax.value === '' ? undefined : Number(localMax.value))
}, 300)
</script>

<template>
  <div>
    <label v-if="label" class="mb-1.5 block text-sm font-medium text-slate-700">
      {{ label }}
    </label>
    <div class="flex items-center gap-2">
      <input
        v-model="localMin"
        type="number"
        min="0"
        :placeholder="placeholderMin || 'Від'"
        class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm transition-all focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
        @input="emitMin"
      />
      <span class="text-slate-400">-</span>
      <input
        v-model="localMax"
        type="number"
        min="0"
        :placeholder="placeholderMax || 'До'"
        class="block w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm transition-all focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
        @input="emitMax"
      />
    </div>
  </div>
</template>
