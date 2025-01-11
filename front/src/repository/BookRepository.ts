import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository'
import type BookSave from '@/entity/book/BookSave'
import type BookView from '@/entity/book/BookView'
import type BookEdit from '@/entity/book/BookEdit'
import type Paging from '@/entity/data/Paging'
import type List from '@/entity/data/List'

@singleton()
export default class BookRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}
  public saveBook(request: BookSave) {
    return this.httpRepository.post({
      path: `/api/users/bookList`,
      body: request
    })
  }
  public getBooksOfYear(year: number, bookView: BookView): Promise<List<BookView>> {
    return this.httpRepository.getArray<BookView>(
      {
        path: `/api/users/year-books/${year}`
      },
      bookView // BookView 타입을 넘겨줍니다.
    )
  }
  public getCompletedBooks(
    page: number,
    size: number,
    bookView: BookView
  ): Promise<Paging<BookView>> {
    return this.httpRepository.getList<BookView>(
      {
        path: `/api/users/bookList/completed?page=${page}&size=${size}`
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
        path: `/api/users/bookList/pending?page=${page}&size=${size}`
      },
      bookView
    )
  }
  public editBookStatus(request: BookEdit) {
    return this.httpRepository.patch({
      path: `api/users/bookList`,
      body: request
    })
  }
}
