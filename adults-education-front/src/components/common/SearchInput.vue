<script setup lang="ts">
import { ref, watch } from 'vue'
import { Icon } from '@iconify/vue'
import { useDebounceFn } from '@vueuse/core'
import { useI18n } from 'vue-i18n'

const props = defineProps<{
  modelValue: string
  placeholder?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const { t } = useI18n()
const localValue = ref(props.modelValue)

// Sync external changes
watch(
  () => props.modelValue,
  (newVal) => {
    localValue.value = newVal
  },
)

const emitUpdate = useDebounceFn((val: string) => {
  emit('update:modelValue', val)
}, 300)

function onInput() {
  emitUpdate(localValue.value)
}

function clear() {
  localValue.value = ''
  emit('update:modelValue', '')
}
</script>

<template>
  <div class="relative">
    <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
      <Icon icon="mdi:magnify" class="h-5 w-5 text-slate-400" />
    </div>
    <input
      v-model="localValue"
      type="text"
      :placeholder="placeholder || t('actions.search')"
      class="block w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-10 text-sm text-slate-900 shadow-sm transition-all duration-200 placeholder:text-slate-400 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-amber-500"
      @input="onInput"
    />
    <button
      v-if="localValue"
      type="button"
      class="absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400 transition-colors hover:text-slate-600"
      @click="clear"
      aria-label="Очистити пошук"
    >
      <Icon icon="mdi:close-circle" class="h-5 w-5" />
    </button>
  </div>
</template>
