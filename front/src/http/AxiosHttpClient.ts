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
    // ✅ Request Interceptor: `skipAuth`가 false인 경우 토큰 추가
    this.client.interceptors.request.use(
      (config: InternalAxiosRequestConfig<any>) => {
        // `config`를 `CustomAxiosRequestConfig` 타입으로 변환
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
            console.log('💾 New accessToken saved to localStorage')
          }
        }
        return response
      },
      async (error: AxiosError) => {
        if (error.response?.status === 401) {
          console.log('Error response:', error.response) // response가 제대로 들어오는지 확인

          const cookies = document.cookie.split('; ').reduce(
            (acc, cookie) => {
              const [key, value] = cookie.split('=')
              acc[key] = value
              return acc
            },
            {} as Record<string, string>
          )
          console.log('Cookies:', cookies) // refreshToken이 쿠키에 있는지 확인

          if (cookies['refreshToken']) {
            try {
              // 401 에러 발생 시, refreshToken이 존재하면 토큰 갱신
              await this.refreshAccessToken()
              return this.client.request(error.config as CustomAxiosRequestConfig) // 원래 요청 재시도
            } catch {
              localStorage.removeItem('accessToken')
              window.location.href = '/login' // 로그인 페이지로 이동
            }
          }
          // refreshToken이 없으면 로그인 페이지로 이동하지 않음 (비로그인 상태 유지)
        }
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
        '/api/v1/auth/token/refresh',
        {},
        { withCredentials: true }
      )

      const authHeader = response.headers['authorization']
      if (authHeader) {
        const newToken = authHeader.split('Bearer ')[1]
        console.log('New access token:', newAccessToken)
        if (newToken) {
          localStorage.setItem('accessToken', newToken)
        }
      }
    } catch (error) {
      console.error('❌ Refresh token expired or invalid. Logging out...', error)
      localStorage.removeItem('accessToken')
      window.location.href = '/login' // 로그인 페이지로 이동
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
