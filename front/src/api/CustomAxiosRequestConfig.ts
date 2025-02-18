import type { AxiosRequestConfig } from 'axios'

// ✅ `skipAuth` 속성 추가
export interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  skipAuth?: boolean
  _retry?: boolean // 401 재시도를 방지하기 위한 속성
}
