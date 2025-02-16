import type { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import HttpError from '@/http/HttpError'
import { singleton } from 'tsyringe'
import type { CustomAxiosRequestConfig } from '@/api/CustomAxiosRequestConfig'

export type HttpRequestConfig = {
  method?: 'GET' | 'POST' | 'PATCH' | 'PUT' | 'DELETE'
  path: string
  params?: any
  body?: any
  skipAuth?: boolean
}

@singleton()
export default class AxiosHttpClient {
  private readonly client: AxiosInstance = axios.create({
    timeout: 3000,
    timeoutErrorMessage: 'error',
    withCredentials: true
  })

  constructor() {
    // ✅ Request Interceptor: skipAuth가 false인 경우 토큰 추가
    this.client.interceptors.request.use(
        (config: InternalAxiosRequestConfig<any>) => {
          // config를 CustomAxiosRequestConfig 타입으로 변환
          const updatedConfig = config as InternalAxiosRequestConfig<any> & CustomAxiosRequestConfig

          if (!updatedConfig.skipAuth) {
            const token = localStorage.getItem('accessToken')
            if (token) {
              updatedConfig.headers['Authorization'] = `Bearer ${token}`
            }
          }
          return updatedConfig
        },
        (error) => Promise.reject(error)
    )

    // ✅ Response Interceptor: 401 발생 시 토큰 자동 갱신
    this.client.interceptors.response.use(
        (response) => {
          // 응답에서 Authorization 헤더가 있는지 확인하고, 있으면 토큰을 갱신하여 localStorage에 저장
          const authHeader = response.headers['authorization']
          if (authHeader) {
            const newToken = authHeader.split('Bearer ')[1]
            if (newToken) {
              localStorage.setItem('accessToken', newToken)
              this.client.defaults.headers.common['Authorization'] = `Bearer ${newToken}` // ✅ 즉시 반영
            }
          }
          return response
        },
        async (error: AxiosError) => {
          if (error.response?.status === 401) {
            console.log('Error response:', error.response) // response가 제대로 들어오는지 확인
            try {
              const newToken = await this.refreshAccessToken() // ✅ 갱신된 토큰 가져오기

              if (newToken && error.config) {
                console.log('🔄 Refresh successful, retrying request')

                // ✅ 원래 요청에 갱신된 토큰 적용 후 재시도
                error.config.headers = error.config.headers || {}
                error.config.headers.Authorization = `Bearer ${newToken}`

                return this.client.request(error.config as CustomAxiosRequestConfig)
              }
            } catch {
              console.error('❌ Refresh token expired or invalid.')
              localStorage.removeItem('accessToken')

              // ✅ GET 요청은 로그인 페이지 이동 X, 비로그인 상태 유지
              if (error.config.method?.toUpperCase() !== 'GET') {
                window.location.href = '/login'
              }
              return Promise.reject(new HttpError(error))
            }
          }
          // refreshToken이 없으면 로그인 페이지로 이동하지 않음 (비로그인 상태 유지)
          return Promise.reject(new HttpError(error))
        }
    )
  }

  public async request(config: HttpRequestConfig) {
    // 에러 요청
    return this.client
        .request({
          method: config.method,
          url: config.path,
          params: config.params,
          data: config.body,
          skipAuth: config.skipAuth
        } as CustomAxiosRequestConfig) // 확장한 타입을 사용
        .then((response: AxiosResponse) => {
          return response.data
        })
        .catch((e: AxiosError) => {
          return Promise.reject(new HttpError(e))
        })
  }

  // ✅ Access Token 갱신 함수
  private async refreshAccessToken() {
    console.log('🔄 Refreshing accessToken...')
    try {
      const response = await this.client.post(
          '/api/v1/auth/token/refresh', // 절대 경로로 수정
          {},
          { withCredentials: true }
      )

      const authHeader = response.headers['authorization']
      if (authHeader) {
        const newToken = authHeader.split('Bearer ')[1]
        console.log('New access token:', newToken)
        if (newToken) {
          localStorage.setItem('accessToken', newToken)
          this.client.defaults.headers.common['Authorization'] = `Bearer ${newToken}`
          console.log('💾 New accessToken saved to localStorage')
          window.location.reload()
          return newToken
        }
      }
      return null
    } catch (error) {
      if (error.response?.status === 401) {
        console.error('❌ Refresh token expired. Logging out...')
        localStorage.removeItem('accessToken')
        return null
      }
      throw error
    }
  }

  public async get(config: HttpRequestConfig) {
    return this.request({ ...config, method: 'GET' })
  }

  public async post(config: HttpRequestConfig) {
    return this.request({ ...config, method: 'POST' })
  }

  public async patch(config: HttpRequestConfig) {
    return this.request({ ...config, method: 'PATCH' })
  }

  public async delete(config: HttpRequestConfig) {
    return this.request({ ...config, method: 'DELETE' })
  }
}