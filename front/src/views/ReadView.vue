<script setup lang="ts">
import { computed, onBeforeMount, onMounted, reactive } from 'vue'
import { container } from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import PostView from '@/entity/post/PostView'
import { ElMessage, ElMessageBox } from 'element-plus'
import Comments from '@/components/Comments.vue'
import BookmarkButton from '@/components/BookmarkButton.vue'
import HeartButton from '@/components/LikeButton.vue'
import { Delete, Edit } from '@element-plus/icons-vue'
import BookmarkRepository from '@/repository/BookmarkRepository'
import UserRepository from '@/repository/UserRepository'
import ProfileRepository from '@/repository/ProfileRepository'
import UserProfile from '@/entity/user/UserProfile'
import { LikeResponse } from '@/entity/data/LikeResponse'
import { plainToInstance } from 'class-transformer'

const props = defineProps<{
  postId: number
}>()
const USER_REPOSITORY = container.resolve(UserRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
const POST_REPOSITORY = container.resolve(PostRepository)
const BOOKMARK_REPOSITORY = container.resolve(BookmarkRepository)

type StateType = {
  profile: UserProfile | null
  post: PostView | null
  isBookmarked: boolean
  likeStatus: LikeResponse | null
  author: String | null
}
const state = reactive<StateType>({
  profile: null,
  post: null,
  isBookmarked: false,
  likeStatus: null,
  author: null
})
// 로그인 여부를 computed로 설정
const isLoggedIn = computed(() => !!state.profile)

onBeforeMount(() => {
  USER_REPOSITORY.getProfile()
    .then((profile) => {
      PROFILE_REPOSITORY.setProfile(profile)
      state.profile = profile
    })
    .catch(() => {
      state.profile = null
    })
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
function checkLikeStatus() {
  POST_REPOSITORY.getLikesCount(props.postId, LikeResponse)
    .then((response: LikeResponse) => {
      console.log('>>isLikeResponse', response)
      state.likeStatus = plainToInstance(LikeResponse, response)
    })
    .catch(() => {
      ElMessage({ type: 'error', message: `좋아요 상태 확인 실패` })
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
        location.href = '/' // home으로 이동
      })
    })
    .catch(() => {
      ElMessage({ type: 'info', message: '삭제가 취소되었습니다.' })
    })
}
function getUserName(postId: number) {
  // POST_REPOSITORY를 통해 postId에 해당하는 유저 이름을 가져옴
  POST_REPOSITORY.getUserName(postId, UserProfile)
    .then((profile) => {
      state.author = profile.name
    })
    .catch((error) => {
      console.error(state.author)
      console.error(error)
      return 'Unknown User'
    })
}

onMounted(() => {
  getUserName(props.postId)
  checkBookmarkStatus()
  checkLikeStatus()
  getPost()
})
</script>

<template>
  <el-container>
    <el-header class="header">
      <h1 class="title">{{ state.post?.title }}</h1>
      <div class="regDate">{{ state.post?.getDisplayRegDate() }}</div>
      <div class="author">Posted by {{ state.author }}</div>
    </el-header>

    <el-main class="content">
      <div class="post-content">{{ state.post?.content }}</div>
    </el-main>

    <el-footer class="footer">
      <div class="bookmark-container">
        <BookmarkButton
          :postId="Number(props.postId)"
          :initialStatus="state.isBookmarked"
          :isLoggedIn="isLoggedIn"
        />
      </div>
      <div class="radius-container">
        <HeartButton
          :postId="Number(props.postId)"
          :initialStatus="isLoggedIn ? Boolean(state.likeStatus?.liked) : false"
          :count="Number(state.likeStatus?.likesCount)"
          :isLoggedIn="isLoggedIn"
        />
      </div>

      <div
        class="edit"
        v-if="state.profile && state.post && state.profile.id === state.post.userId"
      >
        <router-link :to="{ name: 'edit', params: { postId: props.postId } }" class="edit-button">
          <el-button type="" :icon="Edit" circle />
        </router-link>
        <el-button type="danger" :icon="Delete" circle @click="remove" />
        <!--      <el-button type="danger" @click="remove" class="delete-button">삭제</el-button>-->
      </div>
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
.author {
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
  padding-top: 20px;
  width: 80px; /* 둥근 경계의 너비 */
  height: 35px; /* 둥근 경계의 높이 */
  border: 2px solid #dcdfe6; /* 경계선 색상 */
  border-radius: var(--el-border-radius-round); /* 둥근 경계선 */
  display: flex;
  align-items: center; /* 아이콘 버튼이 가운데 오도록 */
}
</style>
