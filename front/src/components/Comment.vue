<script setup lang="ts">
import CommentView from '@/entity/comment/CommentView'
import { ref } from 'vue'
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'

const props = defineProps<{
  comment: any
}>()
// Helper 함수: 날짜 문자열 파싱
// 입력 문자열을 파싱하기 위한 헬퍼 함수
function parseDate(dateInput: string | LocalDateTime): LocalDateTime {
  if (typeof dateInput === 'string') {
    try {
      // 소수점 이하 초의 길이를 조정
      const normalizedDateString = dateInput.replace(/\.\d+/, (match) => match.slice(0, 4)) // 최대 3자리까지만 유지
      return LocalDateTime.parse(normalizedDateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    } catch (error) {
      console.error('Failed to parse date string:', dateInput, error)
      return LocalDateTime.now()
    }
  } else if (dateInput instanceof LocalDateTime) {
    return dateInput // 이미 LocalDateTime 객체인 경우 그대로 반환
  } else {
    console.warn('Invalid date input, using current time:', dateInput)
    return LocalDateTime.now() // 예상치 못한 타입일 경우 현재 시간 반환
  }
}
// CommentView 인스턴스 생성
const commentView = ref<CommentView>(
  new CommentView(
    props.comment.postId,
    props.comment.author,
    props.comment.content,
    parseDate(props.comment.regDate || LocalDateTime.now())
  )
)
</script>

<template>
  <div v-if="commentView">
    <div class="header">
      <div class="section">
        <div class="author">{{ commentView.author }}</div>
        <div class="regDate">{{ commentView.getFormattedRegDate() }}</div>
      </div>

      <div class="delete">삭제</div>
    </div>
    <div class="content">{{ commentView.content }}</div>
  </div>
</template>

<style scoped lang="scss">
.comment {
  width: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
}

.section {
  display: flex;
  flex-direction: column;
}

.author {
  font-weight: 600;
  font-size: 1.2rem;
}

.regDate {
  margin-top: 5px;
  color: #797979;
  font-size: 0.88rem;
}

.content {
  margin-top: 0.8rem;
}

.delete {
  font-size: 0.78rem;
  color: red;
}
</style>
