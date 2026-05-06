import { apiGet, apiPost, apiPut, apiPatch, apiDelete } from './client'
import type { CourseFilter, CourseRequest, CourseResponse, Page } from '@/types/api'

export function createCourse(data: CourseRequest): Promise<CourseResponse> {
  return apiPost<CourseResponse>('/api/courses', data)
}

export function getCourseById(id: number): Promise<CourseResponse> {
  return apiGet<CourseResponse>(`/api/courses/${id}`)
}

export function getAllCourses(
  filter?: CourseFilter,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<CourseResponse>> {
  return apiGet<Page<CourseResponse>>('/api/courses', {
    params: { ...filter, page, size, sort },
  })
}

export function getAllCoursesForAdmin(
  filter?: CourseFilter,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<CourseResponse>> {
  return apiGet<Page<CourseResponse>>('/api/courses/admin', {
    params: { ...filter, page, size, sort },
  })
}

export function getMyCoursesAsTeacher(
  filter?: CourseFilter,
  page = 0,
  size = 10,
  sort?: string,
): Promise<Page<CourseResponse>> {
  return apiGet<Page<CourseResponse>>('/api/courses/my', {
    params: { ...filter, page, size, sort },
  })
}

export function updateCourse(id: number, data: CourseRequest): Promise<CourseResponse> {
  return apiPut<CourseResponse>(`/api/courses/${id}`, data)
}

export function deleteCourse(id: number): Promise<void> {
  return apiDelete(`/api/courses/${id}`)
}

export function setCourseVisibility(
  id: number,
  visible: boolean,
): Promise<CourseResponse> {
  return apiPatch<CourseResponse>(`/api/courses/${id}/visibility`, null, {
    params: { visible },
  })
}
