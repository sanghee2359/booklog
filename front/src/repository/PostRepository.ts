import HttpRepository from '@/repository/HttpRepository'
import { inject, singleton } from 'tsyringe'
import { plainToClass, plainToInstance } from 'class-transformer'
import type PostWrite from '@/entity/post/PostWrite'
import PostView from '@/entity/post/PostView'
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
        path: `/api/posts/${postId}`
      },
      PostView
    )
  }

  public getList(page: number) {
    return this.httpRepository.getList<PostView>(
      {
        path: `/api/posts?page=${page}&size=3`
      },
      PostView
    )
  }

  delete(postId: number) {
    return this.httpRepository.delete({
      path: `/api/posts/${postId}`
    })
  }
}
