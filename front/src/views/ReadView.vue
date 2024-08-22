<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { container } from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import PostView from '@/entity/post/PostView'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '@/router'
import Comments from '@/components/Comments.vue'
import BookmarkButton from '@/components/BookmarkButton.vue'
import HeartButton from '@/components/HeartButton.vue'
import { Check, Delete, Edit, Message, Search, Star } from '@element-plus/icons-vue'
import BookmarkRepository from '@/repository/BookmarkRepository'
const props = defineProps<{
  postId: number
}>()

const POST_REPOSITORY = container.resolve(PostRepository)
const BOOKMARK_REPOSITORY = container.resolve(BookmarkRepository)

type StateType = {
  post: PostView | null
  isBookmarked: boolean
}
const state = reactive<StateType>({
  post: null,
  isBookmarked: false
})

function getPost() {
  POST_REPOSITORY.get(props.postId, PostView)
    .then((post: PostView) => {
      state.post = post
    })
    .catch((e) => {
      ElMessage({ type: 'error', message: `${props.postId}번 글 조회 실패` })
    })
}
function checkBookmarkStatus() {
  BOOKMARK_REPOSITORY.getBookmarkStatus(props.postId)
    .then((response) => {
      state.isBookmarked = response
      console.log('>>>isbookmarked: {}', response)
    })
    .catch(() => {
      ElMessage({ type: 'error', message: `북마크 상태 확인 실패` })
    })
}

function remove() {
  ElMessageBox.confirm('삭제하시겠습니까?', '경고', {
    title: '삭제',
    confirmButtonText: '삭제',
    cancelButtonText: '취소',
    type: 'warning'
  })
    .then(() => {
      POST_REPOSITORY.delete(props.postId).then(() => {
        ElMessage({ type: 'success', message: '성공적으로 삭제되었습니다!' })
        router.push('/')
      })
    })
    .catch(() => {
      ElMessage({ type: 'info', message: '삭제가 취소되었습니다.' })
    })
}

onMounted(() => {
  checkBookmarkStatus()
  getPost()
})
</script>

<template>
  <el-container>
    <el-header class="header">
      <h1 class="title">{{ state.post?.title }}</h1>
      <div class="regDate">{{ state.post?.getDisplayRegDate() }}</div>
    </el-header>

    <el-main class="content">
      <div class="post-content">{{ state.post?.content }}</div>
    </el-main>

    <el-footer class="footer">
      <div class="bookmark-container">
        <BookmarkButton :postId="Number(props.postId)" :initialStatus="state.isBookmarked" />
      </div>
      <div class="radius-container">
        <HeartButton />
      </div>

      <router-link :to="{ name: 'edit', params: { postId: props.postId } }" class="edit-button">
        <el-button type="" :icon="Edit" circle />
      </router-link>
      <el-button type="danger" :icon="Delete" circle @click="remove" />
      <!--      <el-button type="danger" @click="remove" class="delete-button">삭제</el-button>-->
    </el-footer>

    <el-main class="comments">
      <Comments />
    </el-main>
  </el-container>
</template>

<style scoped lang="scss">
.header {
  padding: 20px;
  text-align: center;
}

.title {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 10px;
}

.regDate {
  font-size: 0.875rem;
  color: #666;
}

.content {
  margin-top: 4rem;
  padding-left: 50px;
  padding-bottom: 30px;
  font-weight: 300;

  word-break: normal;
  white-space: break-spaces;
  line-height: 1;
  min-height: 5rem;
}

.post-content {
  font-size: 1rem;
  line-height: 1.6;
  color: #333;
  word-break: break-word;
}

.footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  .bookmark-container {
    position: absolute; /* footer 안에서 위치를 고정 */
    left: 30px; /* 왼쪽 아래에 위치 */
    height: 30px; /* footer와 같은 높이 */
    //bottom: 100%;
    display: flex;
    justify-content: flex-start;
  }
}

.edit-button {
  font-size: 0.875rem;
  font-color: black;
  &:hover {
    color: black;
  }
  &:focus {
    color: #000; // 호버 및 포커스 상태에서도 검정색 유지
  }
  &:disabled {
    color: #000; // 비활성화 상태에서도 텍스트 색상 검정색 유지
    background-color: transparent; // 비활성화 상태에서 배경색 유지
    border-color: #000; // 비활성화 상태에서 테두리 색상 변경 (선택 사항)
  }

  /* 버튼의 배경색 및 테두리 색상 설정 (선택 사항) */
  background-color: transparent; // 배경을 투명하게 설정 (기본 배경색을 지우기 위함)
  border-color: #000; //
}

.comments {
  padding: 20px;
  background-color: #fafafa;
  border-top: 1px solid #ddd;
}

.radius-container {
  padding: 10px;
  padding-top: 16px;
  width: 80px; /* 둥근 경계의 너비 */
  height: 35px; /* 둥근 경계의 높이 */
  border: 2px solid #dcdfe6; /* 경계선 색상 */
  border-radius: var(--el-border-radius-round); /* 둥근 경계선 */
  display: flex;
  align-items: center; /* 아이콘 버튼이 가운데 오도록 */
}
</style>
