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
function getList(page = 1): post {
  POST_REPOSITORY.getList(page).then((paging) => {
    state.postList = paging
    console.log('>>>', state.postList)
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
      <li v-for="post in state.postList.items" :key="post.id">
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
  padding: 0 1rem 0 1rem;
  margin-bottom: 2rem;
}
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 1px; /* 여백 조절 가능 */
}
.totalCount {
  font-size: 0.88rem;
}
.posts {
  list-style: none;
  padding: 20px;
  li {
    margin-bottom: 2.2rem;
    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>
