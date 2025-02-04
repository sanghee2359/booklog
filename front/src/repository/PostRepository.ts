import HttpRepository from '@/repository/HttpRepository'
import { inject, singleton } from 'tsyringe'
import type PostWrite from '@/entity/post/PostWrite'
import type PostEdit from '@/entity/post/PostEdit'
import PostView from '@/entity/post/PostView'
import UserProfile from '@/entity/user/UserProfile'
import LikeResponse from '@/entity/LikeResponse'
import Paging from '@/entity/data/Paging'

@singleton()
export default class PostRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}
  public write(request: PostWrite) {
    // class
    return this.httpRepository.post({
      path: '/api/posts',
      body: request
    })
  }

  public get(postId: number) {
    return this.httpRepository.get<PostView>(
      {
        path: `/api/posts/${postId}`,
        skipAuth: true
      },
      PostView
    )
  }
  public getUserName(postId: number): Promise<UserProfile> {
    return this.httpRepository.get<UserProfile>(
      {
        path: `/api/posts/${postId}/getuser`,
        skipAuth: true
      },
      UserProfile
    )
  }

  public getList(page: number) {
    return this.httpRepository.getList<PostView>(
      {
        path: `/api/posts?page=${page}&size=3`,
        skipAuth: true
      },
      PostView
    )
  }

  public getListByUser(page: number, size: number): Promise<Paging<PostView>> {
    return this.httpRepository.getList<PostView>(
      {
        path: `/api/posts/myPage?page=${page}&size=${size}`
      },
      PostView
    )
  }

  delete(postId: number) {
    return this.httpRepository.delete({
      path: `/api/posts/${postId}`
    })
  }
  edit(postId: number, request: PostEdit) {
    return this.httpRepository.patch({
      path: `/api/posts/${postId}`,
      body: request
    })
  }
  public async getLikesCount(postId: number, isAuthenticated: boolean): Promise<LikeResponse> {
    const path = isAuthenticated ? `/api/posts/${postId}/like` : `/api/posts/${postId}/like/count`
    return this.httpRepository.get<LikeResponse>(
      {
        path: path,
        skipAuth: !isAuthenticated // 비로그인 상태면 skipAuth: true
      },
      LikeResponse
    )
  }

  // 기존 메서드 수정: 좋아요 상태와 개수를 포함한 `LikeResponse`를 반환
  public async toggleLike(postId: number): Promise<LikeResponse> {
    return this.httpRepository.post<LikeResponse>(
      {
        path: `/api/posts/${postId}/like`
      },
      LikeResponse
    )
  }
}
