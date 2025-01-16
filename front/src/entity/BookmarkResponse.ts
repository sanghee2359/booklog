import { Expose } from 'class-transformer'

export default class BookmarkResponse {
  @Expose() status: Boolean
  @Expose() postId: number

  constructor(postId: number, status: Boolean) {
    this.postId = postId
    this.status = status
  }
}
