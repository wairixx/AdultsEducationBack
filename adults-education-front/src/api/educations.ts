import { apiGet, apiPost, apiPatch, apiDelete } from './client'
import type {
  EducationFilter,
  EducationResponse,
  Page,
  UpdateEducationByTeacherRequest,
  UpdateProgressRequest,
} from '@/types/api'

export function getEducationById(id: number): Promise<EducationResponse> {
  return apiGet<EducationResponse>(`/api/educations/${id}`)
}

export function getAllEducations(
  filter?: EducationFilter,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<EducationResponse>> {
  return apiGet<Page<EducationResponse>>('/api/educations', {
    params: { ...filter, page, size, sort },
  })
}

export function getMyEducationsAsStudent(
  filter?: Omit<EducationFilter, 'studentId' | 'teacherId'>,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<EducationResponse>> {
  return apiGet<Page<EducationResponse>>('/api/educations/my-as-student', {
    params: { ...filter, page, size, sort },
  })
}

export function getMyEducationsAsTeacher(
  filter?: Omit<EducationFilter, 'teacherId'>,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<EducationResponse>> {
  return apiGet<Page<EducationResponse>>('/api/educations/my-as-teacher', {
    params: { ...filter, page, size, sort },
  })
}

export function updateProgress(
  id: number,
  data: UpdateProgressRequest,
): Promise<EducationResponse> {
  return apiPatch<EducationResponse>(`/api/educations/${id}/progress`, data)
}

export function completeCourse(id: number): Promise<EducationResponse> {
  return apiPost<EducationResponse>(`/api/educations/${id}/complete`)
}

export function updateEducationByTeacher(
  id: number,
  data: UpdateEducationByTeacherRequest,
): Promise<EducationResponse> {
  return apiPatch<EducationResponse>(`/api/educations/${id}/teacher`, data)
}

export function markLessonCompleted(
  educationId: number,
  lessonId: number,
): Promise<EducationResponse> {
  return apiPost<EducationResponse>(
    `/api/educations/${educationId}/lessons/${lessonId}/complete`,
  )
}

export function unmarkLessonCompleted(
  educationId: number,
  lessonId: number,
): Promise<EducationResponse> {
  return apiDelete<EducationResponse>(
    `/api/educations/${educationId}/lessons/${lessonId}/complete`,
  )
}

export function getCompletedLessonIds(educationId: number): Promise<number[]> {
  return apiGet<number[]>(`/api/educations/${educationId}/completed-lessons`)
}
