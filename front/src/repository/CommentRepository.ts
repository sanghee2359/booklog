import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository'
import Paging from '@/entity/data/Paging'
import type CommentWrite from '@/entity/comment/CommentWrite'
import CommentView from '@/entity/comment/CommentView'
import CommentDelete from '@/entity/comment/CommentDelete'

@singleton()
export default class CommentRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public writeComment(postId: number, request: CommentWrite, isAuthenticated: boolean) {
    const path = isAuthenticated
      ? `/api/posts/${postId}/comments`
      : `/api/public/posts/${postId}/comments`
    console.log(isAuthenticated)
    // class
    return this.httpRepository.post({
      path,
      body: request,
      skipAuth: !isAuthenticated // 비로그인 상태면 skipAuth: true
    })
  }

  public getListByPost(page: number, size: number, postId: number): Promise<Paging<CommentView>> {
    return this.httpRepository.getList<CommentView>(
      {
        path: `/api/posts/${postId}/comments?page=${page}&size=${size}`,
        skipAuth: true
      },
      CommentView
    )
  }

  public deleteComment(
    postId: number,
    commentId: number,
    isAuthenticated: boolean,
    password?: string
  ) {
    const path = isAuthenticated
      ? `/api/posts/${postId}/comments/${commentId}/delete`
      : `/api/public/posts/${postId}/comments/${commentId}/delete`

    const body = isAuthenticated ? {} : new CommentDelete()
    if (!isAuthenticated && password) {
      body.password = password // 비회원인 경우 비밀번호 설정
    }

    return this.httpRepository.post({
      path,
      body,
      skipAuth: !isAuthenticated // 비로그인 상태면 skipAuth: true
    })
  }
}
