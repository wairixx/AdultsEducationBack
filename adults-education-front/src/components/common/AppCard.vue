<script setup lang="ts">
withDefaults(
  defineProps<{
    padding?: boolean
  }>(),
  { padding: true },
)
</script>

<template>
  <div class="rounded-2xl border border-slate-100 bg-white shadow-sm">
    <div v-if="$slots.header" class="border-b border-slate-100 px-6 py-4">
      <slot name="header" />
    </div>
    <div :class="padding ? 'p-6' : ''">
      <slot />
    </div>
    <div v-if="$slots.footer" class="border-t border-slate-100 px-6 py-4">
      <slot name="footer" />
    </div>
  </div>
</template>
