<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import SearchInput from '@/components/common/SearchInput.vue'
import SelectField from '@/components/common/SelectField.vue'
import RangeField from '@/components/common/RangeField.vue'


defineProps<{
  title: string
  topic: string
  format: string
  minPrice?: number
  maxPrice?: number
  minHours?: number
  maxHours?: number
  sort: string
}>()

const emit = defineEmits<{
  'update:title': [val: string]
  'update:topic': [val: string]
  'update:format': [val: string]
  'update:minPrice': [val?: number]
  'update:maxPrice': [val?: number]
  'update:minHours': [val?: number]
  'update:maxHours': [val?: number]
  'update:sort': [val: string]
  reset: []
}>()

const { t } = useI18n()

const topics = [
  'PROGRAMMING', 'DESIGN', 'MARKETING', 'BUSINESS', 'LANGUAGES',
  'SCIENCE', 'ARTS', 'HEALTH', 'FINANCE', 'OTHER'
]
const formats = ['ONLINE', 'OFFLINE', 'HYBRID', 'SELF_PACED']

const topicOptions = computed(() => [
  { value: '', label: t('filters.all') },
  ...topics.map(tKey => ({ value: tKey, label: t(`topics.${tKey}`) }))
])
const formatOptions = computed(() => [
  { value: '', label: t('filters.all') },
  ...formats.map(fKey => ({ value: fKey, label: t(`formats.${fKey}`) }))
])
const sortOptions = computed(() => [
  { value: 'id,desc', label: t('filters.sortNewest') },
  { value: 'price,asc', label: t('filters.sortPriceAsc') },
  { value: 'price,desc', label: t('filters.sortPriceDesc') },
  { value: 'averageRating,desc', label: t('filters.sortRating') },
])
</script>

<template>
  <div class="space-y-6 rounded-2xl bg-white p-6 shadow-sm ring-1 ring-slate-100">
    <div class="flex items-center justify-between">
      <h3 class="font-bold text-slate-900">{{ t('filters.title') }}</h3>
      <button 
        type="button" 
        class="text-xs font-medium text-amber-600 hover:text-amber-700"
        @click="emit('reset')"
      >
        {{ t('actions.clear') }}
      </button>
    </div>

    <div class="space-y-5">
      <SearchInput
        :model-value="title"
        @update:model-value="emit('update:title', $event)"
        :placeholder="t('filters.search')"
      />

      <SelectField
        :model-value="sort"
        @update:model-value="emit('update:sort', $event as string)"
        :label="t('filters.sortBy')"
        :options="sortOptions"
      />

      <SelectField
        :model-value="topic"
        @update:model-value="emit('update:topic', $event as string)"
        :label="t('filters.topic')"
        :options="topicOptions"
      />

      <SelectField
        :model-value="format"
        @update:model-value="emit('update:format', $event as string)"
        :label="t('filters.format')"
        :options="formatOptions"
      />

      <RangeField
        :label="t('filters.price')"
        :min="minPrice"
        :max="maxPrice"
        @update:min="emit('update:minPrice', $event)"
        @update:max="emit('update:maxPrice', $event)"
      />

      <RangeField
        :label="t('filters.hours')"
        :min="minHours"
        :max="maxHours"
        @update:min="emit('update:minHours', $event)"
        @update:max="emit('update:maxHours', $event)"
      />
    </div>
  </div>
</template>
