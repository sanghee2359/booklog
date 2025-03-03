import type { LocalDate, LocalDateTime } from '@js-joda/core'

export default class BookView {
  public bookId = 0
  public title = ''
  public author = ''
  public status = ''
  public startDate: LocalDate | null = null
  public endDate: LocalDate | null = null
  public createdAt: LocalDateTime | null = null
  public isYearBook: boolean = false
  public review:string | null = null;

  // 생성자 추가
  constructor(data?: Partial<BookView>) {
    if (data) {
      this.title = data.title ?? ''
      this.author = data.author ?? ''
      this.status = data.status ?? ''
      this.startDate = data.startDate ?? null
      this.endDate = data.endDate ?? null
      this.createdAt = data.createdAt ?? null
      this.isYearBook = data.isYearBook ?? false // undefined인 경우 false 처리
      this.review = data.review ?? null // undefined인 경우 빈 문자열 처리
    }
  }
}
