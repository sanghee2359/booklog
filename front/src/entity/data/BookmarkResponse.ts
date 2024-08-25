import { Expose } from 'class-transformer'

export class BookmarkResponse {
  @Expose()
  postId: number

  @Expose()
  status: boolean

  constructor(status: boolean) {
    this.status = status
  }
}
