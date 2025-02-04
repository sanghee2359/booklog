import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
import { Transform } from 'class-transformer'

export default class CommentView {
  public userId: number | null
  public commentId: number
  public postId: number
  public author: string
  public content: string

  @Transform(
    ({ value }) => {
      if (value) {
        try {
          // 마이크로초 자리를 6자리로 맞추기 위한 처리
          const normalizedValue = value.length === 26 ? value : value + '0' // 5자리일 경우 0을 추가

          // 밀리초와 마이크로초를 포함한 ISO 형식 처리
          const parsedDate = LocalDateTime.parse(
            normalizedValue,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS") // 6자리 마이크로초
          )
          return parsedDate
        } catch (error) {
          console.error('Error parsing date:', error)
          return LocalDateTime.now() // 파싱 실패 시 기본값
        }
      }
      return LocalDateTime.now() // 값이 없으면 기본값
    },
    {
      toClassOnly: true
    }
  )
  public regDate: LocalDateTime

  constructor(
    userId: number = 0 | null,
    commentId: number = 0,
    postId: number = 0,
    author: string = '',
    content: string = '',
    regDate?: LocalDateTime
  ) {
    this.userId = userId
    this.commentId = commentId
    this.postId = postId
    this.author = author
    this.content = content
    this.regDate = regDate ? regDate : LocalDateTime.now()
  }

  public getFormattedRegDate() {
    return this.regDate.format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss'))
  }
}
