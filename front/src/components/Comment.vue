<script setup lang="ts">
import CommentView from '@/entity/comment/CommentView'
import { ref } from 'vue'
import { DateTimeFormatter, LocalDateTime } from '@js-joda/core'
import { container } from 'tsyringe'
import CommentRepository from '@/repository/CommentRepository'
import {ElMessage, ElMessageBox} from 'element-plus'

const props = defineProps<{
  comment: any
  curUserId: number | null
}>()
const COMMENT_REPOSITORY = container.resolve(CommentRepository)
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
const emit = defineEmits(['commentDeleted']) // 부모에게 이벤트 전달
const isAuthenticated = props.curUserId != null
const showPasswordPopup = ref(false)
const password = ref('')

function deleteIfUser() {
  ElMessageBox.confirm('댓글을 삭제하시겠습니까?', '경고', {
    title: 'Confirmation',
    cancelButtonText: '취소',
    confirmButtonText: '삭제',
    type: 'warning'
  }).then(()=> {
    COMMENT_REPOSITORY.deleteComment(props.comment.postId, props.comment.commentId, isAuthenticated)
        .then(() => {
          ElMessage({ type: 'success', message: '댓글이 삭제되었습니다' })
          emit('commentDeleted') // 댓글 삭제 후 부모에게 새로고침 요청
        })
        .catch((err) => {
          console.error('삭제 실패:', err)
        })
  }).catch(() => {
    ElMessage({ type: 'info', message: '삭제가 취소되었습니다.' })
  })

}

function deleteIfGuest() {
  if (!password.value) {
    alert('비밀번호를 입력해주세요.')
    return
  }
  COMMENT_REPOSITORY.deleteComment(
    props.comment.postId,
    props.comment.commentId,
    isAuthenticated,
    password.value
  )
    .then(() => {
      ElMessage({ type: 'success', message: '댓글이 삭제되었습니다' })
      showPasswordPopup.value = false
      password.value = ''
      emit('commentDeleted') // 댓글 삭제 후 부모에게 새로고침 요청
    })
    .catch((err) => {
      console.error('삭제 실패:', err)
      ElMessage({ type: 'error', message: '비밀번호가 틀렸거나 삭제할 수 없습니다.' })
    })
}
// CommentView 인스턴스 생성
const commentView = ref<CommentView>(
  new CommentView(
    props.comment.userId,
    props.comment.postId,
    props.comment.commentId,
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

      <!-- 로그인된 경우 -->
      <div v-if="isAuthenticated && curUserId === comment.userId" class="delete">
        <a href="#" @click.prevent="deleteIfUser()">삭제</a>
      </div>

      <!-- 비로그인 상태일 경우 -->
      <div v-else-if="!isAuthenticated" class="delete">
        <a href="#" @click.prevent="showPasswordPopup = true">삭제</a>
      </div>
    </div>

    <div class="content">{{ commentView.content }}</div>

    <!-- 비로그인 시 비밀번호 입력 다이얼로그 -->
    <el-dialog v-model="showPasswordPopup" title="비밀번호 입력" @close="password = ''">
      <template #default>
        <div>
          <el-input
            v-model="password"
            autofocus
            placeholder="삭제를 위한 비밀번호 입력"
            type="password"
          />
        </div>
      </template>
      <template #footer>
        <el-button @click="showPasswordPopup = false">취소</el-button>
        <el-button type="primary" @click="deleteIfGuest">확인</el-button>
      </template>
    </el-dialog>
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
.el-dialog {
  z-index: 9999 !important; /* 더 높은 z-index 설정 */
}
</style>
