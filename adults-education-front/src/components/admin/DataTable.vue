<script setup lang="ts" generic="T extends Record<string, any>">


export interface Column {
  key: string
  label: string
}

defineProps<{
  columns: Column[]
  data: T[]
  loading?: boolean
  emptyMessage?: string
  rowKey?: string
}>()
</script>

<template>
  <div class="rounded-2xl bg-white shadow-sm ring-1 ring-slate-200 overflow-hidden relative">
    
    <!-- Loading overlay -->
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-slate-200">
        <thead class="bg-slate-50">
          <tr>
            <th 
              v-for="col in columns" 
              :key="col.key"
              class="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider"
            >
              {{ col.label }}
            </th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-200 bg-white">
          <template v-if="loading">
            <tr v-for="i in 5" :key="i">
              <td v-for="col in columns" :key="col.key" class="px-6 py-4">
                <div class="h-4 bg-slate-100 rounded animate-pulse w-full"></div>
              </td>
            </tr>
          </template>
          <template v-else>
            <tr
              v-for="(item, idx) in data"
              :key="item[rowKey || 'id'] ?? idx"
              class="hover:bg-slate-50 transition-colors"
            >
              <td 
                v-for="col in columns" 
                :key="col.key"
                class="px-6 py-4 whitespace-nowrap text-sm text-slate-900"
                :class="{ 'text-right': col.key === 'actions' }"
              >
                <!-- Slot override for specific column -->
                <slot :name="`col-${col.key}`" :item="item">
                  {{ item[col.key] }}
                </slot>
              </td>
            </tr>
            
            <tr v-if="data.length === 0">
              <td :colspan="columns.length" class="px-6 py-12 text-center text-slate-500">
                {{ emptyMessage || 'Немає даних' }}
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>
