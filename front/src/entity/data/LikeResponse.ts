import { Expose } from 'class-transformer'

export class LikeResponse {
  @Expose()
  postId = 0

  @Expose()
  liked = false

  @Expose()
  likesCount = 0

  constructor(postId: number, isLiked: boolean, likesCount: number) {
    this.postId = postId
    this.liked = isLiked
    this.likesCount = likesCount
  }
}
