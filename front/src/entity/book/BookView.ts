import type { LocalDate, LocalDateTime } from '@js-joda/core'

export default class BookView {
  public bookId = 0
  public title = ''
  public author = ''
  public status = ''
  public startDate: LocalDate | null = null
  public endDate: LocalDate | null = null
  public createdAt: LocalDateTime | null = null

  // constructor(data?: Partial<BookView>) {
  //   if (data) {
  //     this.title = data.title
  //     this.author = data.author
  //     this.status = data.status
  //     this.startDate = data.startDate ? LocalDate.parse(data.startDate) : null // startDate 파싱
  //     this.endDate = data.endDate ? LocalDate.parse(data.endDate) : null // endDate 파싱
  //   }
  // }
}
