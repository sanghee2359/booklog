<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, reactive, watch } from 'vue'
import { container } from 'tsyringe'
import CommentRepository from '@/repository/CommentRepository'
import Paging from '@/entity/data/Paging'
import CommentView from '@/entity/comment/CommentView'
import Comment from '@/components/Comment.vue'
import CommentWrite from '@/entity/comment/CommentWrite' // CommentWrite를 import
import { ElMessage, ElForm } from 'element-plus'

// Props 설정
const props = defineProps<{
  postId: number
  userId?: number // 로그인 상태를 확인하기 위한 userId 추가
}>()
// 기본값을 설정
const userId = props.userId ?? null // null로 기본값 설정
type StateType = {
  commentList: Paging<CommentView>
  commentWrite: CommentWrite
}

const COMMENT_REPOSITORY = container.resolve(CommentRepository)
const formRef = ref<InstanceType<typeof ElForm>>() // Form 참조
const state = reactive<StateType>({
  commentList: new Paging<CommentView>(),
  commentWrite: new CommentWrite() // 댓글 작성 상태 초기화
})

const loading = ref(false)
const page = ref(1)
const pageSize = 5
const buttonDisabled = ref(true) // 버튼 활성화 상태

// 유효성 검사 규칙
const rules = {
  author: [
    { required: true, message: '작성자를 입력해주세요.', trigger: 'blur' },
    { min: 1, max: 8, message: '작성자는 1~8글자로 입력해주세요.', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '비밀번호를 입력해주세요.', trigger: 'blur' },
    { min: 6, max: 30, message: '비밀번호는 6~30글자로 입력해주세요.', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '내용을 입력해주세요.', trigger: 'blur' },
    { min: 10, max: 1000, message: '댓글은 10~1000자 입력해주세요.', trigger: 'blur' }
  ]
}

// DOM 요소에 대한 ref
const commentsContainer = ref<HTMLElement | null>(null)

// 댓글 목록 가져오기
const fetchComments = async (pageNumber: number) => {
  loading.value = true
  try {
    const response = await COMMENT_REPOSITORY.getListByPost(pageNumber, pageSize, props.postId)
    const { items, hasNextPage, totalCount } = response

    // 댓글 목록 업데이트
    if (pageNumber === 1) {
      state.commentList.setItems(items)
    } else {
      state.commentList.setItems([...state.commentList.items, ...items])
    }
    state.commentList.setHasNextPage(hasNextPage)
    state.commentList.totalCount = totalCount
    page.value += 1
  } catch (error) {
    console.error('Error fetching comments:', error)
  } finally {
    loading.value = false
  }
}

// 댓글 작성 메소드
const writeComment = async () => {
  try {
    await COMMENT_REPOSITORY.writeComment(props.postId, state.commentWrite)
    state.commentWrite = new CommentWrite() // 댓글 작성 후 상태 초기화
    ElMessage.success('댓글이 성공적으로 작성되었습니다.')

    fetchComments(1) // 새 댓글이 작성된 후 댓글 목록 새로고침
  } catch (error) {
    console.error('Error writing comment:', error)
    ElMessage.error('댓글 작성에 실패했습니다.')
  }
}

// 버튼 활성화 상태 업데이트
const updateButtonState = () => {
  if (formRef.value) {
    formRef.value.validate((valid: boolean) => {
      buttonDisabled.value = !valid
    })
  }
}

// 각 필드의 변경 감지하여 updateButtonState 호출
watch(() => state.commentWrite.author, updateButtonState)
watch(() => state.commentWrite.password, updateButtonState)
watch(() => state.commentWrite.content, updateButtonState)

// 스크롤 핸들러
const handleScroll = () => {
  if (commentsContainer.value) {
    const container = commentsContainer.value
    const bottomOfContainer =
      container.scrollHeight - container.scrollTop <= container.clientHeight + 50 // 오차 허용

    if (bottomOfContainer && !loading.value && state.commentList.hasNextPage) {
      fetchComments(page.value)
    }
  }
}

onMounted(() => {
  fetchComments(page.value)

  if (commentsContainer.value) {
    commentsContainer.value.addEventListener('scroll', handleScroll)
  }
})

onBeforeUnmount(() => {
  commentsContainer.value?.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div class="totalCount">댓글 {{ state.commentList.totalCount }}개</div>

  <div class="write">
    <el-form label-position="top" :model="state.commentWrite" ref="formRef" :rules="rules">
      <el-form-item v-if="userId == null" label="작성자" prop="author">
        <el-input v-model="state.commentWrite.author" placeholder="작성자를 입력해주세요" />
      </el-form-item>

      <el-form-item v-if="userId == null" label="비밀번호" prop="password">
        <el-input
          type="password"
          v-model="state.commentWrite.password"
          placeholder="비밀번호를 입력해주세요"
        />
      </el-form-item>

      <el-form-item label="내용" prop="content">
        <el-input
          v-model="state.commentWrite.content"
          type="textarea"
          :rows="5"
          :autosize="{ minRows: 5, maxRows: 4 }"
          placeholder="내용을 입력해주세요"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="writeComment" :disabled="buttonDisabled">
          등록하기
        </el-button>
      </el-form-item>
    </el-form>
  </div>

  <ul class="comments" ref="commentsContainer" v-if="state.commentList.items.length">
    <li class="comment" v-for="commentView in state.commentList.items" :key="commentView.id">
      <Comment :comment-view="commentView" />
    </li>
  </ul>

  <!-- 로딩 인디케이터 -->
  <div v-if="loading" class="loading">
    <p>Loading...</p>
  </div>

  <!-- 무한 스크롤이 끝났다는 메시지 -->
  <div v-if="!loading && !state.commentList.hasNextPage" class="end-of-list">
    <p>No more comments to load</p>
  </div>
</template>

<style scoped lang="scss">
.totalCount {
  font-size: 1.4rem;
}

.write {
  margin-top: 20px;
}

.comments {
  margin-top: 3rem;
  list-style: none;
  padding: 0;

  .comment {
    margin-bottom: 2.4rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>
