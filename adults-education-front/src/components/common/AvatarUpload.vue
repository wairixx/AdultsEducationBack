<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import { uploadAvatar } from '@/api/files'
import AppSpinner from './AppSpinner.vue'

defineProps<{
  modelValue?: string
  initials?: string
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const { t } = useI18n()
const uploading = ref(false)
const dragover = ref(false)
const fileInput = ref<HTMLInputElement>()

async function handleFile(file: File) {
  if (!file.type.match(/^image\/(jpeg|png|webp)$/)) {
    toast.error(t('avatar.invalidType'))
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    toast.error(t('avatar.tooLarge'))
    return
  }

  uploading.value = true
  try {
    const response = await uploadAvatar(file)
    emit('update:modelValue', response.url)
    toast.success(t('avatar.success'))
  } catch {
    // Error handled by global interceptor
  } finally {
    uploading.value = false
  }
}

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input.files?.[0]
  if (file) handleFile(file)
  input.value = '' // reset so re-selecting same file works
}

function onDrop(e: DragEvent) {
  dragover.value = false
  const file = e.dataTransfer?.files[0]
  if (file) handleFile(file)
}

function triggerUpload() {
  fileInput.value?.click()
}

function removeAvatar() {
  emit('update:modelValue', '')
  toast.success(t('avatar.removed'))
}
</script>

<template>
  <div class="flex flex-col items-center gap-3">
    <!-- Avatar preview -->
    <div
      class="relative h-24 w-24 cursor-pointer overflow-hidden rounded-full ring-4 ring-slate-100 transition-all duration-200 hover:ring-amber-200"
      :class="{ 'ring-amber-300': dragover }"
      @click="triggerUpload"
      @dragover.prevent="dragover = true"
      @dragleave.prevent="dragover = false"
      @drop.prevent="onDrop"
    >
      <img
        v-if="modelValue"
        :src="modelValue"
        alt="Avatar"
        class="h-full w-full object-cover"
      />
      <div
        v-else
        class="flex h-full w-full items-center justify-center bg-gradient-to-br from-amber-400 to-amber-600 text-2xl font-bold text-white"
      >
        {{ initials || '?' }}
      </div>

      <!-- Overlay -->
      <div
        class="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 transition-opacity duration-200 hover:opacity-100"
      >
        <AppSpinner v-if="uploading" color="text-white" />
        <Icon v-else icon="mdi:camera-outline" class="h-8 w-8 text-white" />
      </div>
    </div>

    <input
      ref="fileInput"
      type="file"
      accept="image/jpeg,image/png,image/webp"
      class="hidden"
      @change="onFileChange"
    />

    <button
      type="button"
      class="text-xs font-medium text-amber-600 transition-colors hover:text-amber-700"
      @click="triggerUpload"
    >
      {{ t('avatar.change') }}
    </button>
    <button
      v-if="modelValue"
      type="button"
      class="text-xs font-medium text-rose-600 transition-colors hover:text-rose-700"
      @click="removeAvatar"
    >
      {{ t('avatar.remove') }}
    </button>
  </div>
</template>
