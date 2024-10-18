<script setup lang="ts">
import type CommentView from '@/entity/comment/CommentView'
import { plainToInstance } from 'class-transformer'

const props = defineProps<{
  comment: any
}>()
// comment를 CommentView 클래스로 변환
const commentView = plainToInstance(CommentView, props.comment)
// function getFormattedRegDate(): string {
//   return props.comment.regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) // 원하는 형식으로 포맷
// }
console.log(commentView)
</script>

<template>
  <div v-if="props.comment">
    <div class="header">
      <div class="section">
        <div class="author">{{ commentView?.author }}</div>
        <div class="regDate">{{ commentView.getFormattedRegDate() }}</div>
      </div>

      <div class="delete">삭제</div>
    </div>
    <div class="content">{{ props.comment.content }}</div>
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
