import { LocalDate } from '@js-joda/core'

export default class BookEdit {
  public bookId = 0
  public startDate: Date | null = null
  public endDate: Date | null = null
  public review = '' // endDate 설정 후 review 등록
  public isYearBook: boolean = false

  constructor(init?: Partial<BookEdit>) {
    if (init) {
      Object.assign(this, init)
    }
  }
  // Date -> LocalDate 변환
  private toLocalDate(date: Date | null): LocalDate | null {
    if (date) {
      return LocalDate.of(date.getFullYear(), date.getMonth() + 1, date.getDate())
    }
    return null
  }

  // startDate를 LocalDate로 변환
  toStartDateLocalDate(): LocalDate | null {
    return this.toLocalDate(this.startDate)
  }

  // endDate를 LocalDate로 변환
  toEndDateLocalDate(): LocalDate | null {
    return this.toLocalDate(this.endDate)
  }
}
