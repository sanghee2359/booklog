<script setup lang="ts">
import CommentView from '@/entity/comment/CommentView'
import { ref } from 'vue'
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'

const props = defineProps<{
  comment: any
}>()

// CommentView 인스턴스 생성
const commentView = ref<CommentView>(
  new CommentView(
    props.comment.postId,
    props.comment.author,
    props.comment.content,
    props.comment.regDate
      ? LocalDateTime.parse(props.comment.regDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
      : LocalDateTime.now()
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
