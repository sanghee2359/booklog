import type { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import HttpError from '@/http/HttpError'
import { singleton } from 'tsyringe'
import type { CustomAxiosRequestConfig } from '@/api/CustomAxiosRequestConfig'
import {useAuthStore} from "@/stores/auth";

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
        const authStore = useAuthStore()

        // Request Interceptor: skipAuth가 false인 경우 토큰 추가
        this.client.interceptors.request.use(
            (config: InternalAxiosRequestConfig<any>) => {
                const updatedConfig = config as CustomAxiosRequestConfig
                if (!updatedConfig.skipAuth) {
                    const token = authStore.accessToken
                    if (token) {
                        updatedConfig.headers['Authorization'] = `Bearer ${token}`
                    }
                }
                return updatedConfig
            },
            (error) => Promise.reject(error)
        )

        // Response Interceptor: 401 발생 시 토큰 자동 갱신
        this.client.interceptors.response.use(
            (response) => {
                const authHeader = response.headers['authorization']
                if (authHeader) {
                    const newToken = authHeader.split('Bearer ')[1]
                    if (newToken) {
                        authStore.setAccessToken(newToken) // 서버에서 받은 토큰을 스토어에 저장
                        this.client.defaults.headers.common['Authorization'] = `Bearer ${newToken}` // 요청에 즉시 적용
                    }
                }
                return response
            },
            async (error: AxiosError) => {
                const config = error.config as CustomAxiosRequestConfig
                if (error.response?.status === 401 && !config._retry) {
                    config._retry = true
                    try {
                        authStore.clearAccessToken()
                        const newToken = await this.refreshAccessToken(authStore)
                        if (newToken) {
                            config.headers['Authorization'] = `Bearer ${newToken}`
                            return this.client.request(config)
                        }
                    } catch {
                        console.error('❌ Refresh token expired or invalid.')
                        authStore.clearAccessToken()

                    }
                }
                return Promise.reject(new HttpError(error))
            }
        )
    }

    public async request(config: HttpRequestConfig) {
        return this.client
            .request({
                method: config.method,
                url: config.path,
                params: config.params,
                data: config.body,
                skipAuth: config.skipAuth
            } as CustomAxiosRequestConfig)
            .then((response: AxiosResponse) => response.data)
            .catch((e: AxiosError) => Promise.reject(new HttpError(e)))
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

    // Access Token 갱신 함수
    private async refreshAccessToken(authStore: any) {
        try {
            const response = await this.client.post('/api/v1/auth/token/refresh', {}, { withCredentials: true })
            console.log("새토큰생성확인:{}",response)
            const newToken = response.headers['authorization']?.split('Bearer ')[1]
            if (newToken) {
                authStore.setAccessToken(newToken) // 갱신된 토큰을 스토어에 저장
                this.client.defaults.headers.common['Authorization'] = `Bearer ${newToken}` // 즉시 반영
                return newToken
            }
            return null
        } catch (error) {
            if (error.response?.status === 401 || error.response?.status === 400) {
                console.error('❌ Refresh token expired. Logging out...')
                authStore.clearAccessToken()
            }
            throw error
        }
    }
}