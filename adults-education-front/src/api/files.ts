import { apiPost } from './client'
import type { FileUploadResponse } from '@/types/api'

export function uploadAvatar(file: File): Promise<FileUploadResponse> {
  const formData = new FormData()
  formData.append('file', file)

  return apiPost<FileUploadResponse>('/api/files/avatars', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export function uploadCourseImage(file: File): Promise<FileUploadResponse> {
  const formData = new FormData()
  formData.append('file', file)

  return apiPost<FileUploadResponse>('/api/files/courses', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
