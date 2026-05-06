<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'

const route = useRoute()
const { t } = useI18n()

const navItems = [
  { name: 'users', path: '/admin/users', icon: 'mdi:account-multiple' },
  { name: 'courses', path: '/admin/courses', icon: 'mdi:school' },
  { name: 'reviews', path: '/admin/reviews', icon: 'mdi:star-circle' },
  { name: 'enrollments', path: '/admin/enrollments', icon: 'mdi:book-open-page-variant' },
]
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 min-h-[calc(100vh-64px)] flex flex-col md:flex-row">
      
      <!-- Sidebar -->
      <aside class="w-full md:w-64 flex-shrink-0 bg-white border-r border-slate-200 md:min-h-[calc(100vh-64px)]">
        <div class="p-6 border-b border-slate-100">
          <h2 class="text-lg font-bold text-slate-900 flex items-center gap-2">
            <Icon icon="mdi:shield-crown" class="h-6 w-6 text-amber-500" />
            {{ t('nav.admin') }}
          </h2>
        </div>
        <nav class="p-4 space-y-1">
          <RouterLink
            v-for="item in navItems"
            :key="item.name"
            :to="item.path"
            class="flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-colors"
            :class="route.path.startsWith(item.path) ? 'bg-amber-50 text-amber-900' : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'"
          >
            <Icon :icon="item.icon" class="h-5 w-5" :class="route.path.startsWith(item.path) ? 'text-amber-500' : 'text-slate-400'" />
            {{ t(`admin.nav.${item.name}`) }}
          </RouterLink>
        </nav>
      </aside>
      
      <!-- Main Content -->
      <main class="flex-1 p-6 md:p-10 overflow-x-hidden">
        <RouterView />
      </main>
      
    </div>
  </DefaultLayout>
</template>
