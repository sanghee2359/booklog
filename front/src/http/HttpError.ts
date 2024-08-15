import type { AxiosError } from 'axios'

export default class HttpError {
  // http 에러 담당
  private readonly code: string
  private readonly message: string

  constructor(e: AxiosError) {
    // http status code
    this.code = e.response?.data.code ?? '500'
    this.message = e.response?.data.message ?? `네트워크에 문제가 발생했습니다 ☹`
  }

  public getMessage() {
    return this.message
  }
}
