import type { HttpRequestConfig } from '@/http/AxiosHttpClient'
import { inject, singleton } from 'tsyringe'
import AxiosHttpClient from '@/http/AxiosHttpClient'
import { plainToInstance } from 'class-transformer'
import { types } from 'sass'
import Null = types.Null
import Paging from '@/entity/data/Paging'

@singleton()
export default class HttpRepository {
  constructor(@inject(AxiosHttpClient) private readonly httpClient: AxiosHttpClient) {}

  public get<T>(
    config: HttpRequestConfig,
    classes: { new (...args: any[]) } | null = null
  ): Promise<T> {
    return this.httpClient
      .request({ ...config, method: 'GET' })
      .then((response) => plainToInstance(classes !== null ? classes : Null, response))
  }
  public getList<T>(
    config: HttpRequestConfig,
    clazz: { new (...args: any[]) }
  ): Promise<Paging<T>> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => {
      const paging = plainToInstance<Paging<T>, any>(Paging, response)
      const items = plainToInstance<T, any>(clazz, response.items)
      paging.setItems(items)
      paging.setHasNextPage(response.hasNextPage)
      return paging
    })
  }

  // public post<T>(
  //   config: HttpRequestConfig,
  //   classes: { new (...args: any[]): T } | null = null
  // ): Promise<T> {
  //   return this.httpClient.request({ ...config, method: 'POST' }).then((response) => {
  //     if (classes) {
  //       return plainToInstance(classes, response)
  //     } else {
  //       return response
  //     }
  //   })
  // }
  public post<T>(
    config: HttpRequestConfig,
    classes: { new (...args: any[]): T } | null = null
  ): Promise<T> {
    return this.httpClient.request({ ...config, method: 'POST' }).then((response) => {
      console.log('Raw response:', response) // 응답 확인
      if (classes) {
        try {
          // 응답이 객체인지 확인
          if (response && typeof response === 'object') {
            return plainToInstance(classes, response)
          } else {
            throw new Error('Invalid response format')
          }
        } catch (error) {
          console.error('Error during plainToInstance conversion:', error)
          throw error // 예외를 다시 던지기
        }
      } else {
        return response
      }
    })
  }

  public patch<T>(config: HttpRequestConfig, classes: { new (...args: any[]) } | null = null) {
    return this.httpClient
      .request({ ...config, method: 'PATCH' })
      .then((response) => plainToInstance(classes !== null ? classes : Null, response))
  }
  public delete<T>(config: HttpRequestConfig, classes: { new (...args: any[]) } | null = null) {
    return this.httpClient
      .request({ ...config, method: 'DELETE' })
      .then((response) => plainToInstance(classes !== null ? classes : Null, response))
  }
}
