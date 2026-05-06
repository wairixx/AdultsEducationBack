import { createI18n } from 'vue-i18n'
import uk from './uk'
import en from './en'

const LOCALE_KEY = 'locale'

const savedLocale = localStorage.getItem(LOCALE_KEY) || 'uk'

const i18n = createI18n({
  legacy: false,
  locale: savedLocale,
  fallbackLocale: 'uk',
  messages: { uk, en },
})

export function setLocale(locale: 'uk' | 'en') {
  i18n.global.locale.value = locale
  localStorage.setItem(LOCALE_KEY, locale)
}

export function getCurrentLocale(): string {
  return i18n.global.locale.value
}

export default i18n
