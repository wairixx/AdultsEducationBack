<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import AppButton from '@/components/common/AppButton.vue'
import AppCard from '@/components/common/AppCard.vue'
import { getCertificateByNumber, getCertificateHtml, getCertificatePdfUrl } from '@/api/certificates'
import type { CertificateResponse } from '@/types/api'
import { adaptCertificateHtmlForEmbed } from '@/utils/certificateHtml'

const route = useRoute()
const { t } = useI18n()

const number = route.params.number as string
const certificate = ref<CertificateResponse | null>(null)
const loading = ref(true)
const notFound = ref(false)

const htmlContent = ref('')

async function loadData() {
  loading.value = true
  try {
    const res = await getCertificateByNumber(number)
    certificate.value = res
    // Fetch HTML as text to avoid X-Frame-Options issue
    htmlContent.value = adaptCertificateHtmlForEmbed(await getCertificateHtml(number))
  } catch (e: any) {
    if (e.response?.status === 404) {
      notFound.value = true
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function downloadPdf() {
  window.open(getCertificatePdfUrl(number), '_blank')
}
</script>

<template>
  <div class="min-h-screen bg-slate-50 py-12 flex flex-col items-center">
    
    <div v-if="loading" class="flex flex-1 items-center justify-center">
      <div class="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
    </div>
    
    <div v-else-if="notFound" class="w-full max-w-lg p-6">
      <div class="rounded-2xl border border-rose-200 bg-rose-50 p-8 text-center shadow-sm">
        <div class="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-rose-100 text-rose-500 mb-4">
          <Icon icon="mdi:close-circle-outline" class="h-10 w-10" />
        </div>
        <h2 class="text-xl font-bold text-rose-900 mb-2">{{ t('certificate.verifyFailed') }}</h2>
        <p class="text-rose-700">{{ t('certificate.notFound') }}</p>
      </div>
      <div class="mt-8 text-center">
        <RouterLink to="/" class="text-sm font-medium text-amber-600 hover:text-amber-700">
          {{ t('actions.goHome') }}
        </RouterLink>
      </div>
    </div>
    
    <div v-else-if="certificate" class="w-full max-w-5xl px-4 sm:px-6 lg:px-8">
      
      <!-- Logo/Brand -->
      <div class="flex justify-center mb-8">
        <RouterLink to="/" class="flex items-center gap-2 text-2xl font-bold tracking-tight text-slate-900 hover:text-amber-600 transition-colors">
          <span class="text-3xl">🎓</span>
          <span>AL Academy</span>
        </RouterLink>
      </div>

      <!-- Success Banner -->
      <div class="mb-8 flex items-center justify-center gap-3 rounded-2xl bg-emerald-50 py-4 px-6 border border-emerald-100 shadow-sm">
        <Icon icon="mdi:check-decagram" class="h-8 w-8 text-emerald-500" />
        <h2 class="text-lg font-bold text-emerald-900">{{ t('certificate.verifySuccess') }}</h2>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        <!-- Metadata -->
        <div class="lg:col-span-1">
          <AppCard class="sticky top-8">
            <template #header>
              <h3 class="font-semibold text-slate-900">{{ t('certificate.details') }}</h3>
            </template>
            <div class="space-y-5">
              <div>
                <span class="block text-xs text-slate-500 mb-1">{{ t('certificate.student') }}</span>
                <span class="block text-sm font-medium text-slate-900">{{ certificate.studentName }}</span>
              </div>
              <div class="border-t border-slate-100 pt-4">
                <span class="block text-xs text-slate-500 mb-1">{{ t('certificate.course') }}</span>
                <span class="block text-sm font-medium text-slate-900">{{ certificate.courseTitle }}</span>
              </div>
              <div class="flex items-center justify-between border-t border-slate-100 pt-4">
                <span class="text-xs text-slate-500">{{ t('certificate.issueDate') }}</span>
                <span class="text-sm font-medium text-slate-900">{{ new Date(certificate.issueDate).toLocaleDateString() }}</span>
              </div>
              <div class="flex items-center justify-between border-t border-slate-100 pt-4">
                <span class="text-xs text-slate-500">{{ t('certificate.number') }}</span>
                <span class="text-sm font-mono font-bold text-slate-900">{{ certificate.certificateNumber }}</span>
              </div>
            </div>
            <template #footer>
              <AppButton variant="primary" full-width @click="downloadPdf">
                <Icon icon="mdi:download" class="mr-2 h-5 w-5" />
                {{ t('certificate.downloadPdf') }}
              </AppButton>
            </template>
          </AppCard>
        </div>

        <!-- HTML View rendered via srcdoc -->
        <div class="lg:col-span-2">
          <div class="rounded-2xl bg-white p-4 shadow-xl ring-1 ring-slate-200/50 overflow-hidden">
            <div class="h-[72vh] min-h-[520px] w-full bg-slate-50 rounded-xl overflow-hidden relative border border-slate-100">
              <iframe 
                v-if="htmlContent"
                :srcdoc="htmlContent" 
                class="w-full h-full border-0 absolute top-0 left-0"
                sandbox="allow-same-origin"
              ></iframe>
              <div v-else class="w-full h-full flex items-center justify-center text-slate-400">
                <Icon icon="mdi:certificate-outline" class="h-16 w-16" />
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>

  </div>
</template>
