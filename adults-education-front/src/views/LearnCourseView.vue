<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import confetti from 'canvas-confetti'
import DefaultLayout from '@/components/layout/DefaultLayout.vue'
import AppButton from '@/components/common/AppButton.vue'
import { getEducationById, getCompletedLessonIds, markLessonCompleted, unmarkLessonCompleted } from '@/api/educations'
import { getLessonsFull } from '@/api/lessons'
import type { EducationResponse, LessonResponse } from '@/types/api'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const educationId = Number(route.params.id)

const education = ref<EducationResponse | null>(null)
const lessons = ref<LessonResponse[]>([])
const completedLessonIds = ref<Set<number>>(new Set())

const activeLessonId = ref<number | null>(null)
const loading = ref(true)
const toggleLoading = ref(false)

const activeLesson = computed(() => lessons.value.find(l => l.id === activeLessonId.value) || null)
const activeLessonIndex = computed(() => lessons.value.findIndex(l => l.id === activeLessonId.value))

const progressStats = computed(() => {
  return {
    completed: completedLessonIds.value.size,
    total: lessons.value.length,
    percentage: lessons.value.length ? Math.round((completedLessonIds.value.size / lessons.value.length) * 100) : 0
  }
})

async function loadData() {
  loading.value = true
  try {
    const edRes = await getEducationById(educationId)
    education.value = edRes
    
    // Load lessons and completions in parallel
    const [lRes, cRes] = await Promise.all([
      getLessonsFull(edRes.courseId),
      getCompletedLessonIds(educationId)
    ])
    
    lessons.value = lRes.sort((a, b) => a.orderNumber - b.orderNumber)
    completedLessonIds.value = new Set(cRes)
    
    // Select first uncompleted lesson, or first lesson
    if (lessons.value.length > 0) {
      const firstUncompleted = lessons.value.find(l => !completedLessonIds.value.has(l.id))
      activeLessonId.value = firstUncompleted ? firstUncompleted.id : (lessons.value[0]?.id ?? null)
    }
    
  } catch {
    router.replace('/404')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

function getEmbedUrl(url: string) {
  if (!url) return ''
  const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/
  const match = url.match(regExp)
  if (match && match[2] && match[2].length === 11) {
    return `https://www.youtube.com/embed/${match[2]}`
  }
  return url
}

async function toggleCompletion() {
  if (!activeLesson.value || !education.value || toggleLoading.value) return
  
  toggleLoading.value = true
  const lessonId = activeLesson.value.id
  const isCurrentlyCompleted = completedLessonIds.value.has(lessonId)
  
  try {
    if (isCurrentlyCompleted) {
      await unmarkLessonCompleted(educationId, lessonId)
      completedLessonIds.value.delete(lessonId)
    } else {
      await markLessonCompleted(educationId, lessonId)
      completedLessonIds.value.add(lessonId)
      
      // Check for celebration
      if (completedLessonIds.value.size === lessons.value.length && education.value.status !== 'COMPLETED') {
        triggerCelebration()
        // Reload education to get COMPLETED status
        education.value = await getEducationById(educationId)
      } else if (activeLessonIndex.value < lessons.value.length - 1) {
        // Auto advance
        activeLessonId.value = lessons.value[activeLessonIndex.value + 1]?.id ?? null
      }
    }
  } catch {
    console.error('An error occurred')
  } finally {
    toggleLoading.value = false
  }
}

const showCelebration = ref(false)

function triggerCelebration() {
  showCelebration.value = true
  const duration = 3000
  const end = Date.now() + duration

  ;(function frame() {
    confetti({
      particleCount: 5,
      angle: 60,
      spread: 55,
      origin: { x: 0 },
      colors: ['#f59e0b', '#8b5cf6', '#10b981']
    })
    confetti({
      particleCount: 5,
      angle: 120,
      spread: 55,
      origin: { x: 1 },
      colors: ['#f59e0b', '#8b5cf6', '#10b981']
    })

    if (Date.now() < end) {
      requestAnimationFrame(frame)
    }
  })()
}

function goPrev() {
  if (activeLessonIndex.value > 0) {
    activeLessonId.value = lessons.value[activeLessonIndex.value - 1]?.id ?? null
  }
}

function goNext() {
  if (activeLessonIndex.value < lessons.value.length - 1) {
    activeLessonId.value = lessons.value[activeLessonIndex.value + 1]?.id ?? null
  }
}
</script>

<template>
  <DefaultLayout>
    <div v-if="loading" class="flex min-h-[60vh] items-center justify-center">
      <div class="h-12 w-12 animate-spin rounded-full border-4 border-slate-200 border-t-amber-500"></div>
    </div>

    <div v-else-if="education" class="flex flex-col lg:flex-row min-h-[calc(100vh-64px)] bg-slate-50">
      
      <!-- Sidebar Navigation -->
      <aside class="w-full lg:w-80 flex-shrink-0 border-r border-slate-200 bg-white shadow-sm flex flex-col h-auto lg:h-[calc(100vh-64px)] lg:sticky lg:top-16">
        <div class="p-5 border-b border-slate-100">
          <RouterLink to="/my-courses" class="inline-flex items-center text-sm font-medium text-slate-500 hover:text-amber-600 mb-4 transition-colors">
            <Icon icon="mdi:arrow-left" class="mr-1 h-4 w-4" />
            {{ t('nav.myCourses') }}
          </RouterLink>
          <h2 class="text-lg font-bold text-slate-900 leading-tight">{{ education.courseTitle }}</h2>
          
          <div class="mt-4">
            <div class="flex items-center justify-between text-xs text-slate-500 mb-1.5">
              <span>{{ t('myCourses.progressStats', { completed: progressStats.completed, total: progressStats.total }) }}</span>
              <span class="font-semibold text-slate-900">{{ progressStats.percentage }}%</span>
            </div>
            <div class="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
              <div 
                class="h-full rounded-full transition-all duration-500"
                :class="progressStats.percentage === 100 ? 'bg-emerald-500' : 'bg-amber-500'"
                :style="{ width: `${progressStats.percentage}%` }"
              ></div>
            </div>
          </div>
          
          <AppButton
            v-if="education.status === 'COMPLETED'"
            variant="ghost"
            class="mt-4 w-full text-emerald-600 bg-emerald-50 hover:bg-emerald-100"
            @click="router.push(`/certificates/by-education/${educationId}`)"
          >
            <Icon icon="mdi:certificate-outline" class="mr-2 h-5 w-5" />
            {{ t('myCourses.viewCertificate') }}
          </AppButton>
        </div>
        
        <div class="flex-1 overflow-y-auto p-3 space-y-1">
          <button
            v-for="(lesson, idx) in lessons"
            :key="lesson.id"
            class="w-full text-left flex items-start gap-3 p-3 rounded-xl transition-all duration-200"
            :class="activeLessonId === lesson.id ? 'bg-amber-50 text-amber-900 ring-1 ring-amber-200 shadow-sm' : 'hover:bg-slate-50 text-slate-700'"
            @click="activeLessonId = lesson.id"
          >
            <div class="flex-shrink-0 mt-0.5">
              <Icon v-if="completedLessonIds.has(lesson.id)" icon="mdi:check-circle" class="h-5 w-5 text-emerald-500" />
              <Icon v-else-if="activeLessonId === lesson.id" icon="mdi:play-circle" class="h-5 w-5 text-amber-500" />
              <div v-else class="h-5 w-5 rounded-full border-2 border-slate-200 flex items-center justify-center">
                <span class="text-[10px] font-bold text-slate-400">{{ idx + 1 }}</span>
              </div>
            </div>
            <span class="text-sm font-medium leading-snug line-clamp-2" :class="activeLessonId === lesson.id ? 'text-amber-900' : 'text-slate-600'">
              {{ lesson.title }}
            </span>
          </button>
        </div>
      </aside>

      <!-- Main Content Area -->
      <main class="flex-1 flex flex-col bg-slate-50 relative">
        <div v-if="activeLesson" class="flex-1 overflow-y-auto w-full max-w-4xl mx-auto p-6 lg:p-10 pb-32">
          
          <h1 class="text-2xl sm:text-3xl font-bold text-slate-900 mb-8">{{ activeLesson.title }}</h1>
          
          <!-- Video Player -->
          <div v-if="activeLesson.videoUrl" class="mb-10 rounded-2xl overflow-hidden shadow-lg ring-1 ring-slate-200 bg-black aspect-video">
            <iframe
              v-if="getEmbedUrl(activeLesson.videoUrl)"
              :src="getEmbedUrl(activeLesson.videoUrl)"
              class="w-full h-full"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
            ></iframe>
            <div v-else class="flex h-full items-center justify-center text-white p-4 text-center">
              <p>Відео не підтримується для вбудовування. <a :href="activeLesson.videoUrl" target="_blank" class="text-amber-400 underline">Переглянути</a></p>
            </div>
          </div>
          
          <!-- Lesson Content (Prose) -->
          <div v-if="activeLesson.content" class="prose prose-slate prose-amber max-w-none prose-img:rounded-xl">
            <div v-html="activeLesson.content"></div>
          </div>
          
        </div>
        
        <!-- Bottom Action Bar -->
        <div v-if="activeLesson" class="fixed bottom-0 right-0 left-0 lg:left-80 bg-white border-t border-slate-200 p-4 shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.05)] z-40">
          <div class="max-w-4xl mx-auto flex items-center justify-between">
            <AppButton
              variant="secondary"
              :disabled="activeLessonIndex === 0"
              @click="goPrev"
              class="hidden sm:inline-flex"
            >
              <Icon icon="mdi:chevron-left" class="mr-1 h-5 w-5" />
              {{ t('actions.back') }}
            </AppButton>
            <AppButton
              variant="secondary"
              class="sm:hidden !px-2"
              :disabled="activeLessonIndex === 0"
              @click="goPrev"
            >
              <Icon icon="mdi:chevron-left" class="h-6 w-6" />
            </AppButton>

            <AppButton
              :variant="completedLessonIds.has(activeLesson.id) ? 'ghost' : 'primary'"
              :loading="toggleLoading"
              class="min-w-[180px]"
              :class="completedLessonIds.has(activeLesson.id) ? 'text-slate-500 hover:text-slate-700 bg-slate-100' : ''"
              @click="toggleCompletion"
            >
              <Icon v-if="completedLessonIds.has(activeLesson.id)" icon="mdi:undo" class="mr-2 h-5 w-5" />
              <Icon v-else icon="mdi:check-circle" class="mr-2 h-5 w-5" />
              {{ completedLessonIds.has(activeLesson.id) ? t('myCourses.markIncomplete') : t('myCourses.markCompleted') }}
            </AppButton>

            <AppButton
              variant="secondary"
              :disabled="activeLessonIndex === lessons.length - 1"
              @click="goNext"
              class="hidden sm:inline-flex"
            >
              {{ t('actions.next') }}
              <Icon icon="mdi:chevron-right" class="ml-1 h-5 w-5" />
            </AppButton>
            <AppButton
              variant="secondary"
              class="sm:hidden !px-2"
              :disabled="activeLessonIndex === lessons.length - 1"
              @click="goNext"
            >
              <Icon icon="mdi:chevron-right" class="h-6 w-6" />
            </AppButton>
          </div>
        </div>
      </main>
      
      <!-- Celebration Modal -->
      <Transition
        enter-active-class="transition duration-300 ease-out"
        enter-from-class="opacity-0 scale-95"
        enter-to-class="opacity-100 scale-100"
        leave-active-class="transition duration-200 ease-in"
        leave-from-class="opacity-100 scale-100"
        leave-to-class="opacity-0 scale-95"
      >
        <div v-if="showCelebration" class="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm" @click="showCelebration = false"></div>
          <div class="relative w-full max-w-sm rounded-3xl bg-white p-8 shadow-2xl text-center">
            <div class="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-amber-100 text-amber-500 mb-6">
              <Icon icon="mdi:trophy-variant" class="h-10 w-10" />
            </div>
            <h3 class="text-2xl font-bold text-slate-900 mb-2">{{ t('myCourses.congratulations') }}</h3>
            <p class="text-slate-500 mb-8">{{ t('myCourses.courseFinished') }}</p>
            <AppButton variant="primary" full-width size="lg" @click="router.push(`/certificates/by-education/${educationId}`)">
              <Icon icon="mdi:certificate" class="mr-2 h-5 w-5" />
              {{ t('myCourses.viewCertificate') }}
            </AppButton>
            <button class="mt-4 text-sm text-slate-400 hover:text-slate-600" @click="showCelebration = false">
              {{ t('actions.close') }}
            </button>
          </div>
        </div>
      </Transition>

    </div>
  </DefaultLayout>
</template>
