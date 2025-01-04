<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, reactive, watch, onBeforeMount, nextTick } from 'vue'
import { container } from 'tsyringe'
import CommentRepository from '@/repository/CommentRepository'
import Paging from '@/entity/data/Paging'
import CommentView from '@/entity/comment/CommentView'
import Comment from '@/components/Comment.vue'
import CommentWrite from '@/entity/comment/CommentWrite' // CommentWrite를 import
import { ElMessage, ElForm } from 'element-plus'
import UserRepository from '@/repository/UserRepository'
import ProfileRepository from '@/repository/ProfileRepository'
import UserProfile from '@/entity/user/UserProfile'
import { debounce } from 'lodash'
// Props 설정
const props = defineProps<{
  postId: number
}>()
// 기본값을 설정
type StateType = {
  profile: UserProfile | null
  commentList: Paging<CommentView>
  commentWrite: CommentWrite
}
const state = reactive<StateType>({
  profile: null,
  commentList: new Paging<CommentView>(),
  commentWrite: new CommentWrite() // 댓글 작성 상태 초기화
})
const USER_REPOSITORY = container.resolve(UserRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
const COMMENT_REPOSITORY = container.resolve(CommentRepository)

const formRef = ref<InstanceType<typeof ElForm>>() // Form 참조
const loading = ref(false)
const page = ref(1)
const pageSize = 6
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

// 댓글 목록 가져오기
const fetchComments = async (reset: boolean = false) => {
  if (loading.value) return // 로딩 중이면 반환
  loading.value = true
  try {
    if (reset) {
      page.value = 1 // reset 시 페이지 번호를 1로 초기화
      state.commentList.setItems([]) // 기존 댓글 리스트 초기화
    }
    const response = await COMMENT_REPOSITORY.getListByPost(page.value, pageSize, props.postId)
    const { items, hasNextPage, totalCount } = response

    // 댓글 목록 업데이트 (더보기)
    state.commentList.items.push(...items)

    // 상태 업데이트
    state.commentList.setHasNextPage(hasNextPage)
    state.commentList.setTotalCount(totalCount)
    if (hasNextPage) {
      page.value += 1
    }
  } catch (error) {
    console.error('Error fetching comments:', error)
  } finally {
    loading.value = false
  }
}

// 댓글 목록 초기화 및 새로고침
const refetchComments = async () => {
  await fetchComments(true) // reset 플래그를 true로 설정하여 새로고침
}

// 추가 댓글 불러오기
const loadMoreComments = async () => {
  if (state.commentList.hasNextPage && !loading.value) {
    await fetchComments() // 추가로 불러오기
  }
}

// 댓글 작성 메소드
const writeComment = async () => {
  try {
    const newComment = await COMMENT_REPOSITORY.writeComment(props.postId, state.commentWrite)
    state.commentWrite = new CommentWrite() // 댓글 작성 후 상태 초기화
    ElMessage.success('댓글이 성공적으로 작성되었습니다.')
    // 댓글 목록 비동기적으로 다시 가져오기
    await refetchComments() // 현재 페이지를 새로고침
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
watch(
  () => [state.commentWrite.author, state.commentWrite.password, state.commentWrite.content],
  updateButtonState
)

onBeforeMount(async () => {
  await USER_REPOSITORY.getProfile()
    .then((profile) => {
      PROFILE_REPOSITORY.setProfile(profile)
      state.profile = profile
    })
    .catch(() => {
      state.profile = null
    })
})
onMounted(() => {
  fetchComments()
})
</script>

<template>
  <div class="totalCount">댓글 {{ state.commentList.totalCount }}개</div>

  <div class="write">
    <el-form label-position="top" :model="state.commentWrite" ref="formRef" :rules="rules">
      <el-form-item v-if="!state.profile" label="작성자" prop="author">
        <el-input v-model="state.commentWrite.author" placeholder="작성자를 입력해주세요" />
      </el-form-item>

      <el-form-item v-if="!state.profile" label="비밀번호" prop="password">
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
      <Comment :comment="commentView" v-if="commentView" />
    </li>
  </ul>

  <!-- 더보기 버튼 -->
  <div class="load-more" v-if="state.commentList.hasNextPage && !loading">
    <el-button type="primary" @click="loadMoreComments"> 더보기 </el-button>
  </div>
  <!-- 로딩 인디케이터 -->
  <div v-if="loading" class="loading">
    <p>Loading...</p>
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
  max-height: 500px; // 적절한 높이 설정
  overflow-y: auto;
  margin-top: 3rem;
  list-style: none;
  padding: 0;

  .comment {
    max-height: none;
    overflow-y: visible;
    margin-bottom: 2.4rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
}
.load-more {
  text-align: center;
  margin-top: 1.5rem;
}
</style>
