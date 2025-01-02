import type { LocalDate, LocalDateTime } from '@js-joda/core'

export default class BookView {
  public bookId = 0
  public title = ''
  public author = ''
  public status = ''
  public startDate: LocalDate | null = null
  public endDate: LocalDate | null = null
  public createdAt: LocalDateTime | null = null
}
