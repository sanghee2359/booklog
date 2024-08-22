import HttpRepository from '@/repository/HttpRepository'
import { inject, singleton } from 'tsyringe'
import type PostWrite from '@/entity/post/PostWrite'
import type PostEdit from '@/entity/post/PostEdit'
import PostView from '@/entity/post/PostView'
import UserProfile from '@/entity/user/UserProfile.ts'

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

  public get(postId: number, PostView: PostView) {
    return this.httpRepository.get<PostView>(
      {
        path: `/api/posts/${postId}`
      },
      PostView
    )
  }
  public getUserName(postId: number, UserProfile: UserProfile): Promise<UserProfile> {
    return this.httpRepository.get<UserProfile>(
      {
        path: `/api/posts/${postId}/getuser`
      },
      UserProfile
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
  edit(postId: number, request: PostEdit) {
    return this.httpRepository.patch({
      path: `/api/posts/${postId}`,
      body: request
    })
  }
}
