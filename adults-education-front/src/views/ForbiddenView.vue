<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'

const { t } = useI18n()
</script>

<template>
  <DefaultLayout>
    <div class="flex min-h-[70vh] items-center justify-center px-4">
      <div class="text-center">
        <div class="mb-6 text-7xl">🚫</div>
        <h1 class="text-7xl font-bold tracking-tight text-slate-200 sm:text-8xl">
          {{ t('pages.forbidden.title') }}
        </h1>
        <h2 class="mt-4 text-2xl font-semibold text-slate-800">
          {{ t('pages.forbidden.message') }}
        </h2>
        <p class="mt-2 text-base text-slate-500">
          {{ t('pages.forbidden.description') }}
        </p>
        <div class="mt-8">
          <RouterLink
            to="/"
            class="inline-flex items-center gap-2 rounded-xl bg-amber-500 px-6 py-3 text-sm font-semibold text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-amber-600 hover:shadow-md"
          >
            <Icon icon="mdi:home-outline" class="h-5 w-5" />
            {{ t('actions.goHome') }}
          </RouterLink>
        </div>
      </div>
    </div>
  </DefaultLayout>
</template>
