import { apiGet, apiPost, apiPut, apiDelete } from './client'
import type { LessonPreviewResponse, LessonRequest, LessonResponse } from '@/types/api'

export function createLesson(data: LessonRequest): Promise<LessonResponse> {
  return apiPost<LessonResponse>('/api/lessons', data)
}

export function getLessonById(id: number): Promise<LessonResponse> {
  return apiGet<LessonResponse>(`/api/lessons/${id}`)
}

export function getLessonsPreview(courseId: number): Promise<LessonPreviewResponse[]> {
  return apiGet<LessonPreviewResponse[]>(
    `/api/lessons/by-course/${courseId}/preview`,
  )
}

export function getLessonsFull(courseId: number): Promise<LessonResponse[]> {
  return apiGet<LessonResponse[]>(`/api/lessons/by-course/${courseId}/full`)
}

export function updateLesson(id: number, data: LessonRequest): Promise<LessonResponse> {
  return apiPut<LessonResponse>(`/api/lessons/${id}`, data)
}

export function deleteLesson(id: number): Promise<void> {
  return apiDelete(`/api/lessons/${id}`)
}
