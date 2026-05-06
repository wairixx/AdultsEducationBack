<script setup lang="ts">
defineProps<{
  type?: 'card' | 'text' | 'avatar'
  rows?: number
}>()
</script>

<template>
  <div class="animate-pulse flex flex-col gap-4 w-full">
    <template v-if="type === 'card'">
      <div class="h-48 bg-slate-200 rounded-2xl w-full"></div>
      <div class="h-6 bg-slate-200 rounded-lg w-3/4 mt-2"></div>
      <div class="h-4 bg-slate-200 rounded-lg w-full"></div>
      <div class="h-4 bg-slate-200 rounded-lg w-5/6"></div>
    </template>
    
    <template v-else-if="type === 'avatar'">
      <div class="flex items-center gap-4">
        <div class="h-12 w-12 rounded-full bg-slate-200 flex-shrink-0"></div>
        <div class="flex-1 space-y-2">
          <div class="h-4 bg-slate-200 rounded w-1/3"></div>
          <div class="h-3 bg-slate-200 rounded w-1/4"></div>
        </div>
      </div>
    </template>
    
    <template v-else>
      <div v-for="i in (rows || 3)" :key="i" class="h-4 bg-slate-200 rounded w-full"></div>
    </template>
  </div>
</template>
