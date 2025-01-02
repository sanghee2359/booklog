import type { LocalDate } from '@js-joda/core'

export default class BookSave {
  public title = ''
  public author = ''
  public status = ''
  public startDate: LocalDate | null = null
  public endDate: LocalDate | null = null
}
