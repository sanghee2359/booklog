import type { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios'
import axios from 'axios'
import HttpError from '@/http/HttpError'

class AxiosError {}

export default class AxiosHttpClient {
  private readonly client: AxiosInstance = axios.create({
    timeout: 3000,
    timeoutErrorMessage: 'error'
  })
  private async request(config: AxiosRequestConfig) {
    // 에러 요청
    return this.client.request(config).catch((e: AxiosError) => {
      return Promise.reject(new HttpError(e))
    })
  }
  public async get(url: String) {
    return this.request({
      method: 'GET',
      url: url
    })
  }
  public async post(url: String) {
    return this.request({
      method: 'POST',
      url: url
    })
  }
  public async patch(url: String) {
    return this.request({
      method: 'PATCH',
      url: url
    })
  }
}
