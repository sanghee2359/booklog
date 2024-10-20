import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
import { Transform } from 'class-transformer'

export default class CommentView {
  public postId: number
  public author: string
  public content: string

  @Transform(
    ({ value }) => {
      if (value) {
        // 밀리초를 포함한 ISO 형식을 지원하도록 처리
        const parsedDate = LocalDateTime.parse(
          value,
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        )
        return parsedDate
      }
      return LocalDateTime.now() // 기본값
    },
    {
      toClassOnly: true
    }
  )
  public regDate: LocalDateTime

  constructor(
    postId: number = 0,
    author: string = '',
    content: string = '',
    regDate?: LocalDateTime
  ) {
    this.postId = postId
    this.author = author
    this.content = content
    this.regDate = regDate ? regDate : LocalDateTime.now()
  }

  public getFormattedRegDate() {
    return this.regDate.format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss'))
  }
}
