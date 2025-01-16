import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository'
import PostView from '@/entity/post/PostView'
import BookmarkResponse from '@/entity/BookmarkResponse'
import type Paging from '@/entity/data/Paging'
@singleton()
export default class BookmarkRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}
  public toggleBookmark(postId: number): Promise<BookmarkResponse> {
    // class
    return this.httpRepository.post<BookmarkResponse>(
      {
        path: `/api/bookmarks/${postId}`
      },
      BookmarkResponse
    )
  }
  public getBookmarkStatus(postId: number): Promise<Boolean> {
    return this.httpRepository.get(
      {
        path: `/api/users/bookmarks/${postId}`
      },
      Boolean
    )
  }
  public getBookmarks(page: number, size: number): Promise<Paging<PostView>> {
    return this.httpRepository.getList<PostView>(
      {
        path: `/api/users/bookmarks?page=${page}&size=${size}`
      },
      PostView
    )
  }
}
