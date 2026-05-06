<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppButton from '@/components/common/AppButton.vue'
import AppCard from '@/components/common/AppCard.vue'
import { getMyCertificateByEducation, getCertificateHtml, getCertificatePdfUrl } from '@/api/certificates'
import type { CertificateResponse } from '@/types/api'
import { adaptCertificateHtmlForEmbed } from '@/utils/certificateHtml'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const educationId = Number(route.params.educationId)
const certificate = ref<CertificateResponse | null>(null)
const loading = ref(true)

const htmlContent = ref('')

async function loadData() {
  loading.value = true
  try {
    const res = await getMyCertificateByEducation(educationId)
    certificate.value = res
    // Fetch HTML as text to avoid X-Frame-Options issue
    htmlContent.value = adaptCertificateHtmlForEmbed(
      await getCertificateHtml(res.certificateNumber),
    )
  } catch {
    router.replace('/404')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function downloadPdf() {
  if (!certificate.value) return
  window.open(getCertificatePdfUrl(certificate.value.certificateNumber), '_blank')
}

function copyVerifyLink() {
  if (!certificate.value) return
  const url = `${window.location.origin}/cert/${certificate.value.certificateNumber}`
  navigator.clipboard.writeText(url).then(() => {
    toast.success(t('certificate.linkCopied'))
  })
}
</script>

<template>
  <DefaultLayout>
    <div class="bg-slate-50 py-8 min-h-[calc(100vh-64px)]">
      <div v-if="loading" class="flex min-h-[60vh] items-center justify-center">
        <div class="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
      </div>
      
      <div v-else-if="certificate" class="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8">
        <div class="mb-6">
          <RouterLink to="/my-courses" class="inline-flex items-center text-sm font-medium text-slate-500 hover:text-amber-600 mb-4 transition-colors">
            <Icon icon="mdi:arrow-left" class="mr-1 h-4 w-4" />
            {{ t('nav.myCourses') }}
          </RouterLink>
          <h1 class="text-3xl font-bold tracking-tight text-slate-900">{{ t('certificate.title') }}</h1>
          <p class="mt-2 text-sm text-slate-500">
            {{ t('certificate.subtitle', { course: certificate.courseTitle }) }}
          </p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          <!-- Certificate rendered from fetched HTML -->
          <div class="lg:col-span-2 rounded-2xl bg-white p-4 shadow-sm ring-1 ring-slate-200 overflow-hidden">
            <div class="h-[72vh] min-h-[520px] w-full bg-slate-100 rounded-xl overflow-hidden relative">
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
          
          <!-- Metadata and Actions -->
          <div class="lg:col-span-1 space-y-6">
            <AppCard>
              <div class="space-y-6">
                <div>
                  <h3 class="text-sm font-medium text-slate-500 mb-1">{{ t('certificate.number') }}</h3>
                  <p class="text-lg font-mono font-bold text-slate-900">{{ certificate.certificateNumber }}</p>
                </div>
                
                <div class="flex items-center justify-between border-t border-slate-100 pt-4">
                  <span class="text-sm text-slate-500">{{ t('certificate.issueDate') }}</span>
                  <span class="text-sm font-medium text-slate-900">{{ new Date(certificate.issueDate).toLocaleDateString() }}</span>
                </div>
                
                <div class="flex items-center justify-between border-t border-slate-100 pt-4">
                  <span class="text-sm text-slate-500">{{ t('certificate.duration') }}</span>
                  <span class="text-sm font-medium text-slate-900">{{ certificate.durationHours }} {{ t('course.hours') }}</span>
                </div>
                
                <div class="flex flex-col border-t border-slate-100 pt-4">
                  <span class="text-sm text-slate-500 mb-1">{{ t('course.teacherLabel') }}</span>
                  <span class="text-sm font-medium text-slate-900">{{ certificate.teacherName }}</span>
                </div>
              </div>
            </AppCard>
            
            <div class="space-y-3">
              <AppButton variant="primary" full-width size="lg" @click="downloadPdf">
                <Icon icon="mdi:download" class="mr-2 h-5 w-5" />
                {{ t('certificate.downloadPdf') }}
              </AppButton>
              
              <AppButton variant="secondary" full-width size="lg" @click="copyVerifyLink">
                <Icon icon="mdi:link-variant" class="mr-2 h-5 w-5" />
                {{ t('certificate.copyLink') }}
              </AppButton>
            </div>
          </div>
          
        </div>
      </div>
    </div>
  </DefaultLayout>
</template>
