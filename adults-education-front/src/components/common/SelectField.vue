<script setup lang="ts">
import { Icon } from '@iconify/vue'

defineProps<{
  modelValue: string | number
  label?: string
  options: { value: string | number; label: string }[]
}>()

defineEmits<{
  'update:modelValue': [value: string | number]
}>()
</script>

<template>
  <div>
    <label v-if="label" class="mb-1.5 block text-sm font-medium text-slate-700">
      {{ label }}
    </label>
    <div class="relative">
      <select
        :value="modelValue"
        class="block w-full appearance-none rounded-lg border border-slate-200 bg-white py-2 pl-3 pr-10 text-sm text-slate-900 shadow-sm transition-all duration-200 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
        @change="$emit('update:modelValue', ($event.target as HTMLSelectElement).value)"
      >

        <option v-for="opt in options" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>
      <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-2">
        <Icon icon="mdi:chevron-down" class="h-5 w-5 text-slate-400" />
      </div>
    </div>
  </div>
</template>
