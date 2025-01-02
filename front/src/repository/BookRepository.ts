import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository'
import type BookSave from '@/entity/book/BookSave'
import type BookView from '@/entity/book/BookView'
import type BookEdit from '@/entity/book/BookEdit'
import type Paging from '@/entity/data/Paging'

@singleton()
export default class BookRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}
  public saveBook(request: BookSave) {
    return this.httpRepository.post({
      path: `/api/users/bookList`,
      body: request
    })
  }
  public getCompletedBooks(
    page: number,
    size: number,
    bookView: BookView
  ): Promise<Paging<BookView>> {
    return this.httpRepository.getList<BookView>(
      {
        path: `/api/users/completedBookList?page=${page}&size=${size}`
      },
      bookView
    )
  }
  public getPendingBooks(
    page: number,
    size: number,
    bookView: BookView
  ): Promise<Paging<BookView>> {
    return this.httpRepository.getList<BookView>(
      {
        path: `/api/users/pendingBookList?page=${page}&size=${size}`
      },
      bookView
    )
  }
  public editBookStatus(bookId: number, request: BookEdit) {
    return this.httpRepository.patch({
      path: `api/users/bookList/${bookId}`,
      body: request
    })
  }
}
