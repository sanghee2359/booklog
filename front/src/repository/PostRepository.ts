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

  public get(postId: number): Promise<PostView> {
    return this.httpRepository
      .get({
        path: `/api/posts/${postId}`
      })
      .then((response) => {
        return plainToInstance(PostView, response)
      })
  }

  public getList(page: number): Promise<Paging<PostView>> {
    return this.httpRepository
      .get({
        path: `/api/posts?page=${page}&size=3`
      })
      .then((response) => {
        const paging = plainToInstance<Paging<PostView>, any>(Paging, response)
        const items = plainToInstance<PostView, any[]>(PostView, response.items)
        paging.setItems(items)
        return paging
      })
  }
}
