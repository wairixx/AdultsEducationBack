<script setup lang="ts">
import { Icon } from '@iconify/vue'

const props = withDefaults(
  defineProps<{
    modelValue?: number
    readonly?: boolean
    size?: 'sm' | 'md' | 'lg'
  }>(),
  {
    modelValue: 0,
    readonly: false,
    size: 'md',
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: number]
}>()

function setRating(val: number) {
  if (props.readonly) return
  emit('update:modelValue', val)
}
</script>

<template>
  <div class="flex items-center gap-0.5">
    <button
      v-for="i in 5"
      :key="i"
      type="button"
      :disabled="readonly"
      class="text-amber-400 transition-colors"
      :class="[
        readonly ? 'cursor-default' : 'cursor-pointer hover:text-amber-500',
        size === 'sm' ? 'h-4 w-4' : size === 'lg' ? 'h-8 w-8' : 'h-5 w-5',
      ]"
      @click="setRating(i)"
    >
      <Icon
        :icon="i <= modelValue ? 'mdi:star' : 'mdi:star-outline'"
        class="h-full w-full"
      />
    </button>
  </div>
</template>
