import { apiGet, apiPost, apiPatch, apiDelete } from './client'
import type { Page, ReviewFilter, ReviewRequest, ReviewResponse } from '@/types/api'

export function createReview(data: ReviewRequest): Promise<ReviewResponse> {
  return apiPost<ReviewResponse>('/api/reviews', data)
}

export function getReviewById(id: number): Promise<ReviewResponse> {
  return apiGet<ReviewResponse>(`/api/reviews/${id}`)
}

export function getAllReviews(
  filter?: ReviewFilter,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<ReviewResponse>> {
  return apiGet<Page<ReviewResponse>>('/api/reviews', {
    params: { ...filter, page, size, sort },
  })
}

export function getAllReviewsForAdmin(
  filter?: ReviewFilter,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<ReviewResponse>> {
  return apiGet<Page<ReviewResponse>>('/api/reviews/admin', {
    params: { ...filter, page, size, sort },
  })
}

export function deleteReview(id: number): Promise<void> {
  return apiDelete(`/api/reviews/${id}`)
}

export function setReviewVisibility(
  id: number,
  visible: boolean,
): Promise<ReviewResponse> {
  return apiPatch<ReviewResponse>(`/api/reviews/${id}/visibility`, null, {
    params: { visible },
  })
}
