<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Icon } from '@iconify/vue'
import { toast } from 'vue-sonner'
import AppButton from '@/components/common/AppButton.vue'
import AppInput from '@/components/common/AppInput.vue'
import { enroll } from '@/api/enrollments'
import type { PaymentMethod } from '@/types/api'

const props = defineProps<{
  courseId: number
  courseTitle: string
  price: number
  isOpen: boolean
}>()

const emit = defineEmits<{
  close: []
  success: [educationId: number]
}>()

const { t } = useI18n()

const method = ref<PaymentMethod>(props.price === 0 ? 'FREE' : 'CARD')
const transactionRef = ref('')
const loading = ref(false)

const errors = reactive({
  cardNumber: '',
  bankRef: '',
})

/** Format card number with spaces every 4 digits */
function formatCardInput(value: string): string {
  const digits = value.replace(/\D/g, '').slice(0, 16)
  return digits.replace(/(.{4})/g, '$1 ').trim()
}

function onCardInput(e: Event) {
  const input = e.target as HTMLInputElement
  const formatted = formatCardInput(input.value)
  transactionRef.value = formatted
  // Keep cursor in right position
  input.value = formatted
  errors.cardNumber = ''
}

function validateCard(): boolean {
  const digits = transactionRef.value.replace(/\s/g, '')
  if (!digits) {
    errors.cardNumber = t('validation.required')
    return false
  }
  if (digits.length !== 16 || !/^\d{16}$/.test(digits)) {
    errors.cardNumber = t('validation.cardNumber')
    return false
  }
  errors.cardNumber = ''
  return true
}

function validateBankRef(): boolean {
  if (!transactionRef.value.trim()) {
    errors.bankRef = t('validation.required')
    return false
  }
  errors.bankRef = ''
  return true
}

function validate(): boolean {
  if (props.price === 0) return true
  if (method.value === 'CARD') return validateCard()
  if (method.value === 'BANK_TRANSFER') return validateBankRef()
  return true
}

const isFormValid = computed(() => {
  if (props.price === 0) return true
  if (method.value === 'CARD') {
    const digits = transactionRef.value.replace(/\s/g, '')
    return digits.length === 16 && /^\d{16}$/.test(digits)
  }
  if (method.value === 'BANK_TRANSFER') {
    return transactionRef.value.trim().length > 0
  }
  return true
})

async function submit() {
  if (!validate()) return

  loading.value = true
  try {
    const ref = method.value === 'CARD'
      ? transactionRef.value.replace(/\s/g, '')
      : transactionRef.value.trim()

    const res = await enroll({
      courseId: props.courseId,
      paymentMethod: method.value,
      transactionRef: method.value !== 'FREE' ? ref : undefined
    })
    toast.success(t('course.enrollSuccess'))
    emit('success', res.education.id)
  } catch {
    // Error handled by global interceptor
  } finally {
    loading.value = false
  }
}

function onMethodChange(m: PaymentMethod) {
  method.value = m
  transactionRef.value = ''
  errors.cardNumber = ''
  errors.bankRef = ''
}
</script>

<template>
  <Transition
    enter-active-class="transition duration-200 ease-out"
    enter-from-class="opacity-0 scale-95"
    enter-to-class="opacity-100 scale-100"
    leave-active-class="transition duration-150 ease-in"
    leave-from-class="opacity-100 scale-100"
    leave-to-class="opacity-0 scale-95"
  >
    <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4">
      <div class="fixed inset-0 bg-slate-900/50 backdrop-blur-sm" @click="emit('close')"></div>
      
      <div class="relative w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl">
        <button
          class="absolute right-4 top-4 text-slate-400 hover:text-slate-600"
          @click="emit('close')"
          aria-label="Close dialog"
        >
          <Icon icon="mdi:close" class="h-6 w-6" />
        </button>

        <h3 class="text-xl font-bold text-slate-900">{{ t('course.enrollTitle') }}</h3>
        <p class="mt-1 text-sm text-slate-500">{{ courseTitle }}</p>

        <div class="mt-6 rounded-xl bg-amber-50 p-4 text-center">
          <p class="text-sm font-medium text-amber-800">{{ t('course.priceLabel') }}</p>
          <p class="text-2xl font-bold text-amber-600">
            {{ price === 0 ? t('course.free') : `${price} ${t('course.price')}` }}
          </p>
        </div>

        <form @submit.prevent="submit" class="mt-6 space-y-5">
          <div v-if="price > 0" class="space-y-3">
            <label class="block text-sm font-medium text-slate-700">{{ t('course.paymentMethod') }}</label>
            <div class="grid grid-cols-2 gap-3">
              <label
                class="flex cursor-pointer items-center justify-center rounded-lg border p-3 transition-colors"
                :class="method === 'CARD' ? 'border-amber-500 bg-amber-50 text-amber-700' : 'border-slate-200 hover:bg-slate-50'"
              >
                <input type="radio" :checked="method === 'CARD'" @change="onMethodChange('CARD')" class="sr-only" />
                <Icon icon="mdi:credit-card-outline" class="mr-2 h-5 w-5" />
                <span class="text-sm font-medium">{{ t('course.methodCard') }}</span>
              </label>
              
              <label
                class="flex cursor-pointer items-center justify-center rounded-lg border p-3 transition-colors"
                :class="method === 'BANK_TRANSFER' ? 'border-amber-500 bg-amber-50 text-amber-700' : 'border-slate-200 hover:bg-slate-50'"
              >
                <input type="radio" :checked="method === 'BANK_TRANSFER'" @change="onMethodChange('BANK_TRANSFER')" class="sr-only" />
                <Icon icon="mdi:bank-outline" class="mr-2 h-5 w-5" />
                <span class="text-sm font-medium">{{ t('course.methodBank') }}</span>
              </label>
            </div>
            
            <!-- Card number input with formatting -->
            <div v-if="method === 'CARD'">
              <label class="mb-1.5 block text-sm font-medium text-slate-700">{{ t('course.cardNumber') }}</label>
              <div class="relative">
                <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <Icon icon="mdi:credit-card" class="h-5 w-5 text-slate-400" />
                </div>
                <input
                  type="text"
                  :value="transactionRef"
                  @input="onCardInput"
                  placeholder="0000 0000 0000 0000"
                  maxlength="19"
                  inputmode="numeric"
                  class="block w-full rounded-lg border px-3 py-2.5 pl-10 text-sm tracking-widest transition-colors focus:outline-none focus:ring-2 focus:ring-amber-500"
                  :class="errors.cardNumber ? 'border-rose-300 text-rose-900' : 'border-slate-300 text-slate-900'"
                />
              </div>
              <p v-if="errors.cardNumber" class="mt-1 text-xs text-rose-500">{{ errors.cardNumber }}</p>
              <p v-else class="mt-1 text-xs text-slate-400">{{ t('validation.cardHint') }}</p>
            </div>

            <!-- Bank transfer reference -->
            <div v-if="method === 'BANK_TRANSFER'">
              <AppInput
                v-model="transactionRef"
                :label="t('course.bankRef')"
                :placeholder="t('course.bankRefPlaceholder')"
                icon="mdi:bank-transfer"
                :error="errors.bankRef"
                @update:model-value="errors.bankRef = ''"
              />
            </div>
          </div>

          <AppButton
            type="submit"
            variant="primary"
            size="lg"
            full-width
            :loading="loading"
            :disabled="!isFormValid && price > 0"
          >
            {{ t('course.confirmEnroll') }}
          </AppButton>
        </form>
      </div>
    </div>
  </Transition>
</template>
