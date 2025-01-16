import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository'
import Paging from '@/entity/data/Paging'
import type CommentWrite from '@/entity/comment/CommentWrite'
import CommentView from '@/entity/comment/CommentView'

@singleton()
export default class CommentRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public writeComment(postId: number, request: CommentWrite) {
    // class
    return this.httpRepository.post({
      path: `/api/posts/${postId}/comments`,
      body: request
    })
  }

  public getListByPost(page: number, size: number, postId: number): Promise<Paging<CommentView>> {
    return this.httpRepository.getList<CommentView>(
      {
        path: `/api/posts/${postId}/comments?page=${page}&size=${size}`
      },
      CommentView
    )
  }

  delete(postId: number) {
    return this.httpRepository.delete({
      path: `/api/posts/${postId}`
    })
  }
}
