import type { HttpRequestConfig } from '@/http/AxiosHttpClient'
import AxiosHttpClient from '@/http/AxiosHttpClient'
import { inject, singleton } from 'tsyringe'
import { plainToInstance } from 'class-transformer'
import Paging from '@/entity/data/Paging'
import Null from '@/entity/data/Null'
import List from '@/entity/data/List'

@singleton()
export default class HttpRepository {
  constructor(@inject(AxiosHttpClient) private readonly httpClient: AxiosHttpClient) {}

  public get<T>(config: HttpRequestConfig, clazz: { new (...args: any[]) }): Promise<T> {
    return this.httpClient
      .request({ ...config, method: 'GET' })
      .then((response) => plainToInstance(clazz, response))
  }
  public getArray<T>(config: HttpRequestConfig, clazz: { new (...args: any[]) }): Promise<List<T>> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => {
      // console.log('Full Response:', response) // 전체 응답 확인
      // 응답이 비어있는지 또는 예상된 형식인지 확인
      if (!response || !Array.isArray(response)) {
        console.warn('Empty or invalid response received.')
        return new List<T>() // 빈 리스트를 반환
      }
      const list = new List<T>()
      list.setItems(plainToInstance(clazz, response))
      // console.log('list Data:', list) // 응답 데이터 확인

      return list
    })
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

  public post<T>(
    config: HttpRequestConfig,
    classes: { new (...args: any[]): T } | null = null
  ): Promise<T> {
    return this.httpClient.request({ ...config, method: 'POST' }).then((response) => {
      if (classes) {
        return plainToInstance(classes, response)
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
