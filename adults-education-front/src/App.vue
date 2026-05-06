<script setup lang="ts">
import { RouterView } from 'vue-router'
import { Toaster } from 'vue-sonner'
</script>

<template>
  <RouterView v-slot="{ Component }">
    <template v-if="Component">
      <Transition
        name="page"
        mode="out-in"
      >
        <Suspense>
          <template #default>
            <component :is="Component" />
          </template>
          <template #fallback>
            <div class="fixed inset-0 z-50 flex items-center justify-center bg-white">
              <div class="h-10 w-10 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
            </div>
          </template>
        </Suspense>
      </Transition>
    </template>
  </RouterView>
  <Toaster
    position="top-right"
    rich-colors
    expand
    close-button
    :duration="4000"
    :toast-options="{
      style: { fontFamily: 'Inter, system-ui, sans-serif' },
      class: 'sonner-toast-custom',
    }"
  />
</template>

<style>
.page-enter-active,
.page-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
  transform: translateY(4px);
}
</style>
