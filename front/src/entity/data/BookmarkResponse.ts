import { Expose } from 'class-transformer'

export class BookmarkResponse {
  @Expose()
  postId: number

  @Expose()
  status: boolean

  constructor(postId: number, status: boolean) {
    this.postId = postId
    this.status = status
  }
}
