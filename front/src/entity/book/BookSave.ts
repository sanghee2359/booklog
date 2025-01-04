import { LocalDate } from '@js-joda/core'

export default class BookSave {
  public title = ''
  public author = ''
  public status = ''
  public startDate: Date | null = null

  // 생성자 추가
  constructor(data?: Partial<BookSave>) {
    if (data) {
      this.title = data.title
      this.author = data.author
      this.status = data.status
      this.startDate = data.startDate || null
    }
  }

  // Date -> LocalDate 변환
  toLocalDate(): LocalDate | null {
    if (this.startDate) {
      return LocalDate.of(
        this.startDate.getFullYear(),
        this.startDate.getMonth() + 1,
        this.startDate.getDate()
      )
    }
    return null
  }
}
