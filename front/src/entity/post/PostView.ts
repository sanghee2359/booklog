import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
import { Expose, Transform } from 'class-transformer'
import { differenceInHours, differenceInMinutes, format, isYesterday, toDate } from 'date-fns'

export default class PostView {
  public userId = 0
  @Expose() public postId = 0
  public title = ''
  public content = ''
  @Transform(({ value }) => LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME), {
    toClassOnly: true
  })
  public regDate = LocalDateTime.now()
  public likesCount = 0
  public getDisplayRegDate() {
    const now = new Date()

    // LocalDateTime을 Date 객체로 변환
    const regDateAsDate = toDate(this.regDate)

    // 어제인 경우
    if (isYesterday(regDateAsDate)) {
      return '어제'
    }

    // 몇 분 전
    const minuteDiff = differenceInMinutes(now, regDateAsDate)
    if (minuteDiff < 60) return `${minuteDiff}분 전`

    // 몇 시간 전
    const hourDiff = differenceInHours(now, regDateAsDate)
    if (hourDiff < 24) return `${hourDiff}시간 전`

    // 날짜가 지난 경우
    return format(regDateAsDate, 'MM월 dd일')
  }
  public getDisplaySimpleRegDate() {
    return format(toDate(this.regDate), 'yyyy.MM.dd')
  }

  public getShortenContent() {
    if (this.content.length < 150) {
      return this.content
    }
    return this.content.substring(0, 150) + '...'
  }
}
