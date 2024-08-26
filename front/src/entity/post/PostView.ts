import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
import { Transform } from 'class-transformer'

export default class PostView {
  public userId = 0
  public postId = 0
  public title = ''
  public content = ''
  @Transform(({ value }) => LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME), {
    toClassOnly: true
  })
  public regDate = LocalDateTime.now()
  public likesCount = 0
  public getDisplayRegDate() {
    // 몇 분 전
    let minute = this.regDate.minute()
    let now_minute = LocalDateTime.now().minute()
    let minute_result = now_minute - minute

    // 몇 시간 전
    let hour = this.regDate.hour()
    let now_hour = LocalDateTime.now().hour()
    let hour_result = now_hour - hour

    // 몇 일
    let day = this.regDate.dayOfMonth()
    let now_day = LocalDateTime.now().dayOfMonth()
    let day_result = now_day - day
    if (day_result == 0 && hour_result == 0 && minute_result < 60) return minute_result + '분'
    if (day_result == 0 && hour_result > 0 && hour_result < 24) return hour_result + '시간'
    if (day_result == 1) return '어제'

    return this.regDate.format(DateTimeFormatter.ofPattern('MM월 dd일'))
  }
  public getDisplaySimpleRegDate() {
    return this.regDate.format(DateTimeFormatter.ofPattern('yyyy.MM.dd'))
  }

  public getShortenContent() {
    if (this.content.length < 150) {
      return this.content
    }
    return this.content.substring(0, 150) + '...'
  }
}
