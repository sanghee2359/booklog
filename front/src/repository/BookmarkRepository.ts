import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository'
import PostView from '@/entity/post/PostView'
import type Paging from '@/entity/data/Paging'
import { ElMessage } from 'element-plus'
import type BookmarkResponse from '@/entity/data/BookmarkResponse'

@singleton()
export default class BookmarkRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}
  public toggleBookmark(
    postId: number,
    BookmarkResponse: BookmarkResponse
  ): Promise<BookmarkResponse> {
    // class
    return this.httpRepository.post<BookmarkResponse>(
      {
        path: `/api/bookmarks/${postId}`
      },
      BookmarkResponse
    )
  }
  public getBookmarkStatus(postId: number): Promise<boolean> {
    return this.httpRepository.get({
      path: `/api/users/bookmarks/${postId}`
    })
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
