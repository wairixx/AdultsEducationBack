import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

interface PaginationOptions {
  defaultSize?: number
  defaultSort?: string
}

export function usePagination(options: PaginationOptions = {}) {
  const router = useRouter()
  const route = useRoute()

  const page = ref(Number(route.query.page) || 0)
  const size = ref(Number(route.query.size) || options.defaultSize || 10)
  const sort = ref((route.query.sort as string) || options.defaultSort || 'id,desc')

  const totalElements = ref(0)
  const totalPages = ref(0)

  // Update URL to make state shareable
  function syncToUrl(extraQuery: Record<string, any> = {}) {
    const query: Record<string, any> = {
      ...route.query,
      ...extraQuery,
      page: page.value > 0 ? page.value : undefined,
      size: size.value !== 10 ? size.value : undefined,
      sort: sort.value !== 'id,desc' ? sort.value : undefined,
    }

    // Clean up undefined values
    Object.keys(query).forEach((key) => query[key] === undefined && delete query[key])

    router.replace({ query })
  }

  function setPage(newPage: number) {
    page.value = newPage
    syncToUrl()
  }

  function setSort(newSort: string) {
    sort.value = newSort
    page.value = 0 // Reset to first page when sorting changes
    syncToUrl()
  }

  function setTotal(totalElem: number, totalPg: number) {
    totalElements.value = totalElem
    totalPages.value = totalPg
  }

  function reset(keepSizeAndSort = false) {
    page.value = 0
    if (!keepSizeAndSort) {
      size.value = options.defaultSize || 10
      sort.value = options.defaultSort || 'id,desc'
    }
  }

  return {
    page,
    size,
    sort,
    totalElements,
    totalPages,
    setPage,
    setSort,
    setTotal,
    reset,
    syncToUrl,
  }
}
