<script setup lang="ts">
import { Icon } from '@iconify/vue'

defineProps<{
  title: string
  isOpen: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
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
      <div class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" @click="emit('close')"></div>
      
      <div class="relative w-full max-w-lg rounded-2xl bg-white shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        <div class="flex items-center justify-between border-b border-slate-100 p-6 bg-slate-50">
          <h3 class="text-xl font-bold text-slate-900">{{ title }}</h3>
          <button @click="emit('close')" class="text-slate-400 hover:text-slate-600 transition-colors">
            <Icon icon="mdi:close" class="h-6 w-6" />
          </button>
        </div>
        
        <div class="flex-1 overflow-y-auto p-6">
          <slot></slot>
        </div>
        
        <div v-if="$slots.footer" class="border-t border-slate-100 p-6 bg-slate-50 flex justify-end gap-3">
          <slot name="footer"></slot>
        </div>
      </div>
    </div>
  </Transition>
</template>
