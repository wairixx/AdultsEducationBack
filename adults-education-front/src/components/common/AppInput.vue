<script setup lang="ts">
import { ref } from 'vue'
import { Icon } from '@iconify/vue'

const props = withDefaults(
  defineProps<{
    modelValue?: string | number
    label?: string
    type?: string
    placeholder?: string
    error?: string
    required?: boolean
    icon?: string
    hint?: string
    disabled?: boolean
    min?: string | number
  }>(),
  {
    modelValue: '',
    label: '',
    type: 'text',
    placeholder: '',
    error: '',
    required: false,
    icon: '',
    hint: '',
    disabled: false,
    min: undefined,
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: string | number]
}>()

const showPassword = ref(false)
const isPasswordType = props.type === 'password'

function onInput(e: Event) {
  const target = e.target as HTMLInputElement
  emit('update:modelValue', props.type === 'number' ? Number(target.value) : target.value)
}
</script>

<template>
  <div>
    <label v-if="label" class="mb-1.5 block text-sm font-medium text-slate-700">
      {{ label }}
      <span v-if="required" class="text-rose-500">*</span>
    </label>
    <div class="relative">
      <div v-if="icon" class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
        <Icon :icon="icon" class="h-4.5 w-4.5 text-slate-400" />
      </div>
      <input
        :type="isPasswordType && !showPassword ? 'password' : isPasswordType ? 'text' : type"
        :value="modelValue"
        :placeholder="placeholder"
        :required="required"
        :disabled="disabled"
        :min="min"
        class="block w-full rounded-lg border bg-white px-3.5 py-2.5 text-sm text-slate-900 shadow-sm transition-all duration-200 placeholder:text-slate-400 focus:border-transparent focus:outline-none focus:ring-2 disabled:cursor-not-allowed disabled:bg-slate-50 disabled:text-slate-500"
        :class="[
          icon ? 'pl-10' : '',
          isPasswordType ? 'pr-10' : '',
          error
            ? 'border-rose-300 focus:ring-rose-500'
            : 'border-slate-200 focus:ring-amber-500',
        ]"
        @input="onInput"
      />
      <button
        v-if="isPasswordType"
        type="button"
        class="absolute inset-y-0 right-0 flex items-center pr-3 text-slate-400 transition-colors hover:text-slate-600"
        tabindex="-1"
        @click="showPassword = !showPassword"
      >
        <Icon :icon="showPassword ? 'mdi:eye-off-outline' : 'mdi:eye-outline'" class="h-4.5 w-4.5" />
      </button>
    </div>
    <p v-if="error" class="mt-1 text-xs text-rose-500">{{ error }}</p>
    <p v-else-if="hint" class="mt-1 text-xs text-slate-500">{{ hint }}</p>
  </div>
</template>
