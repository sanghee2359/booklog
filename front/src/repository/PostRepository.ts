import HttpRepository from '@/repository/HttpRepository'
import { inject, singleton } from 'tsyringe'
import { plainToClass, plainToInstance } from 'class-transformer'
import type PostWrite from '@/entity/post/PostWrite'
import PostView from '@/entity/post/PostView'

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

  public get(postId: number): Promise<PostView> {
    return this.httpRepository
      .get({
        path: `/api/posts/${postId}`
      })
      .then((response) => {
        return plainToInstance(PostView, response)
      })
  }

  public getList() {
    return this.httpRepository
      .get({
        path: '/api/posts?page=1&size=3'
      })
      .then((response) => {
        return plainToInstance(PostView, response)
      })
  }
}
