<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { container } from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import Post from '@/components/Post.vue'

const POST_REPOSITORY = container.resolve(PostRepository)
const state = reactive({
  postList: []
})
function getList(): post {
  POST_REPOSITORY.getList().then((postList) => {
    state.postList = postList
    console.log('>>>', state.postList)
  })
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="content">
    <ul class="posts">
      <li v-for="post in state.postList" :key="post.id">
        <Post :post="post" />
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
.content {
  height: calc(100vh - 60px - 2rem - 20px - 1.5rem);
  padding: 0;
}
.posts {
  list-style: none;
  padding: 0;
  li {
    margin-bottom: 2.2rem;
    &:last-child {
      margin-bottom: 0;
    }
  }
}
</style>
