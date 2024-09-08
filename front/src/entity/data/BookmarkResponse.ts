import { Expose } from 'class-transformer'

export class BookmarkResponse {
  @Expose() status: boolean
  @Expose() postId: number

  constructor(postId: number, status: boolean) {
    this.postId = postId
    this.status = status
  }
}
