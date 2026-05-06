<script setup lang="ts">
import AppSpinner from './AppSpinner.vue'

withDefaults(
  defineProps<{
    variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
    size?: 'sm' | 'md' | 'lg'
    loading?: boolean
    disabled?: boolean
    type?: 'button' | 'submit' | 'reset'
    fullWidth?: boolean
  }>(),
  {
    variant: 'primary',
    size: 'md',
    loading: false,
    disabled: false,
    type: 'button',
    fullWidth: false,
  },
)
</script>

<template>
  <button
    :type="type"
    :disabled="disabled || loading"
    class="inline-flex items-center justify-center gap-2 rounded-lg font-medium transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
    :class="[
      fullWidth ? 'w-full' : '',
      // Size
      size === 'sm' ? 'px-3 py-1.5 text-xs' : size === 'lg' ? 'px-6 py-3 text-base' : 'px-4 py-2.5 text-sm',
      // Variant
      variant === 'primary'
        ? 'bg-amber-500 text-white shadow-sm hover:bg-amber-600 hover:shadow-md focus:ring-amber-500'
        : variant === 'secondary'
          ? 'border border-slate-200 bg-white text-slate-700 shadow-sm hover:border-slate-300 hover:bg-slate-50 focus:ring-slate-400'
          : variant === 'danger'
            ? 'bg-rose-500 text-white shadow-sm hover:bg-rose-600 focus:ring-rose-500'
            : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900 focus:ring-slate-400',
    ]"
  >
    <AppSpinner
      v-if="loading"
      size="sm"
      :color="variant === 'primary' || variant === 'danger' ? 'text-white' : 'text-slate-500'"
    />
    <slot />
  </button>
</template>
