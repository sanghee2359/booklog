import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
import { Transform } from 'class-transformer'

export default class CommentView {
  public postId = 0
  public author = ''
  public content = ''

  @Transform(({ value }) => LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME), {
    toClassOnly: true
  })
  public regDate: LocalDateTime = LocalDateTime.now() // 초기화
  public getFormattedRegDate() {
    return this.regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
  }
}
