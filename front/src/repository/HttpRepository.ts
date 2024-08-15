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

  public get<T>(config: HttpRequestConfig, classes: { new (...args: any[]) }): Promise<T> {
    return this.httpClient
      .request({ ...config, method: 'GET' })
      .then((response) => plainToInstance(classes, response))
  }
  public getList<T>(config: HttpRequestConfig, classes: { new (...args: any[]) }): Promise<T> {
    return this.httpClient.request({ ...config, method: 'GET' }).then((response) => {
      const paging = plainToInstance<Paging<T>, any>(Paging, response)
      const items = plainToInstance<T, any>(classes, response.items)
      paging.setItems(items)
      return paging
    })
  }

  public post<T>(config: HttpRequestConfig, classes: { new (...args: any[]) } | null = null) {
    return this.httpClient
      .request({ ...config, method: 'POST' })
      .then((response) => plainToInstance(classes !== null ? classes : Null, response))
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
