<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { container } from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import Post from '@/components/PostView.vue'
import Paging from '@/entity/data/Paging'
import type PostView from '@/entity/post/PostView'

type StateType = {
  postList: Paging<PostView>
}
const POST_REPOSITORY = container.resolve(PostRepository)
const state = reactive<StateType>({
  postList: new Paging<PostView>()
})
function getList(page = 1): Paging<PostView> {
  POST_REPOSITORY.getList(page).then((paging) => {
    state.postList = paging
  })
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="content">
    <span class="totalCount">게시글 수: {{ state.postList.totalCount }} </span>
    <ul class="posts">
      <li v-for="post in state.postList.items" :key="post.postId">
        <Post :post="post" />
      </li>
    </ul>
  </div>

  <div class="pagination-container">
    <el-pagination
      v-model:current-page="state.postList.page"
      v-model:page-size="state.postList.size"
      :disabled="false"
      :background="true"
      layout="prev, pager, next, jumper"
      :total="state.postList.totalCount"
      @current-change="(page) => getList(page)"
    />
  </div>
</template>

<style scoped lang="scss">
.content {
  width: 100%; /* 콘텐츠가 부모에 맞게 100% 차지하도록 설정 */
  max-width: 1200px; /* 너무 커지는 걸 방지 */
  margin: 0 auto; /* 가운데 정렬 */
  padding: 1rem; /* 여백 조정 */
}
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 1rem; /* 여백 조절 가능 */
}
.totalCount {
  font-size: 0.88rem;
}
.posts {
  width: 100%; /* 부모 크기에 맞게 조정 */
  max-width: 1200px; /* 최대 크기 제한 */
  display: flex;
  flex-direction: column; /* 세로 정렬 */
  padding: 10px;
}
.posts li {
  width: 100%; /* 리스트 아이템이 부모 너비를 차지하도록 설정 */
  max-width: 100%;
  margin-bottom: 2.2rem;
}
.app-footer {
  width: 100%;
  max-width: 1200px; /* 푸터의 최대 너비를 넓힘 */
  margin: 20px auto;
  padding: 10px 0;
}
</style>
