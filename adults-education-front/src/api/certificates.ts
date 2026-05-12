import { apiGet, type CustomRequestConfig } from './client'
import apiClient from './client'
import type { CertificateResponse } from '@/types/api'

const BASE = 'http://localhost:8080'

export function getCertificateByNumber(number: string): Promise<CertificateResponse> {
  return apiGet<CertificateResponse>(`/api/certificates/verify/${number}`)
}

export function getMyCertificateByEducation(
  educationId: number,
): Promise<CertificateResponse> {
  return apiGet<CertificateResponse>(`/api/certificates/by-education/${educationId}`)
}

/** Fetch the certificate HTML content as a string (avoids X-Frame-Options issues) */
export async function getCertificateHtml(number: string): Promise<string> {
  const response = await apiClient.get(`/api/certificates/verify/${number}/view`, {
    responseType: 'text',
    headers: { Accept: 'text/html' },
  })
  return response.data as string
}

/** URL for rendering the certificate as HTML page */
export function getCertificateHtmlUrl(number: string): string {
  return `${BASE}/api/certificates/verify/${number}/view`
}

/** URL for downloading the certificate as PDF */
export function getCertificatePdfUrl(number: string): string {
  return `${BASE}/api/certificates/verify/${number}/pdf`
}
