import type { AxiosError, AxiosInstance, AxiosResponse } from 'axios'
import axios from 'axios'
import HttpError from '@/http/HttpError'
import { singleton } from 'tsyringe'

export type HttpRequestConfig = {
  method?: 'GET' | 'POST' | 'PATCH' | 'PUT' | 'DELETE'
  path: string
  params?: any
  body?: any
}
@singleton()
export default class AxiosHttpClient {
  private readonly client: AxiosInstance = axios.create({
    timeout: 3000,
    timeoutErrorMessage: 'error'
  })
  public async request(config: HttpRequestConfig) {
    // 에러 요청
    return this.client
      .request({
        method: config.method,
        url: config.path,
        params: config.params,
        data: config.body
      })
      .then((response: AxiosResponse) => {
        return response.data
      })
      .catch((e: AxiosError) => {
        return Promise.reject(new HttpError(e))
      })
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
