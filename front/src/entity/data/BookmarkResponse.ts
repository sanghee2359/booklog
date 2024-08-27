import { Expose } from 'class-transformer'

export class BookmarkResponse {
  @Expose()
  postId = 0

  @Expose()
  status = false

  constructor(postId: number, status: boolean) {
    this.postId = postId
    this.status = status
  }
}
