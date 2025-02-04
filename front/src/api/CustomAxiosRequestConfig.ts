import type { AxiosRequestConfig } from 'axios'

// ✅ `skipAuth` 속성 추가
export interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  skipAuth?: boolean
}
