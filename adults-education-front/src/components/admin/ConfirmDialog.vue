<script setup lang="ts">
import { Icon } from '@iconify/vue'
import AppButton from '@/components/common/AppButton.vue'

defineProps<{
  title: string
  message: string
  confirmText?: string
  cancelText?: string
  danger?: boolean
  loading?: boolean
  isOpen: boolean
}>()

const emit = defineEmits<{
  (e: 'confirm'): void
  (e: 'cancel'): void
}>()
</script>

<template>
  <Transition
    enter-active-class="transition duration-200 ease-out"
    enter-from-class="opacity-0 scale-95"
    enter-to-class="opacity-100 scale-100"
    leave-active-class="transition duration-150 ease-in"
    leave-from-class="opacity-100 scale-100"
    leave-to-class="opacity-0 scale-95"
  >
    <div v-if="isOpen" class="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" @click="emit('cancel')"></div>
      
      <div class="relative w-full max-w-md rounded-2xl bg-white shadow-2xl overflow-hidden flex flex-col p-6">
        <div class="flex gap-4">
          <div 
            class="flex-shrink-0 flex items-center justify-center h-10 w-10 rounded-full"
            :class="danger ? 'bg-rose-100 text-rose-500' : 'bg-amber-100 text-amber-500'"
          >
            <Icon :icon="danger ? 'mdi:alert' : 'mdi:help-circle'" class="h-6 w-6" />
          </div>
          <div>
            <h3 class="text-lg font-bold text-slate-900 mb-1">{{ title }}</h3>
            <p class="text-sm text-slate-500 leading-relaxed">{{ message }}</p>
          </div>
        </div>
        
        <div class="mt-8 flex justify-end gap-3">
          <AppButton variant="ghost" @click="emit('cancel')" :disabled="loading">
            {{ cancelText || 'Скасувати' }}
          </AppButton>
          <AppButton 
            :variant="danger ? 'primary' : 'primary'" 
            :class="danger ? 'bg-rose-500 hover:bg-rose-600 focus:ring-rose-500 border-rose-500 text-white' : ''"
            :loading="loading"
            @click="emit('confirm')"
          >
            {{ confirmText || 'Підтвердити' }}
          </AppButton>
        </div>
      </div>
    </div>
  </Transition>
</template>
