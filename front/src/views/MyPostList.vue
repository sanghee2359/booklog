<template>
  내가 작성한 글 리스트
  <span class="totalCount">글 수: {{ totalCount }} </span>
  <div class="bookmark-container" @scroll="handleRoll">
    <!-- 북마크 포스트 리스트 -->
    <PostView v-for="post in paging.items" :key="post.postId" :post="post" />

    <!-- 로딩 인디케이터 -->
    <div v-if="loading" class="loading">
      <p>Loading...</p>
    </div>

    <!-- 무한 스크롤이 끝났다는 메시지 -->
    <div v-if="!loading && !paging.hasNextPage" class="end-of-list">
      <p>No more posts to load</p>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
import { container } from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import Paging from '@/entity/data/Paging'
import { PostView } from '@/entity/data/PostView'

// PostView 컴포넌트 import
import PostViewComponent from '@/components/PostView.vue'

export default {
  components: {
    PostView: PostViewComponent
  },
  setup() {
    const POST_REPOSITORY = container.resolve(PostRepository)
    const paging = ref(new Paging<PostView>())
    const loading = ref(false)
    const page = ref(1)
    const pageSize = 5
    const totalCount = ref(0) // Define totalCount as a ref

    const fetchList = async (pageNumber: number) => {
      loading.value = true
      try {
        const response = await POST_REPOSITORY.getListByUser(pageNumber, pageSize)
        const { items, hasNextPage, totalCount: responseTotalCount } = response
        totalCount.value = responseTotalCount // Update totalCount
        console.log(hasNextPage)
        if (items.length) {
          if (pageNumber === 0) {
            paging.value.setItems(items)
          } else {
            paging.value.setItems([...paging.value.items, ...items])
          }
          paging.value.setHasNextPage(hasNextPage)
          page.value += 1
        } else {
          paging.value.setHasNextPage(false)
        }
      } catch (error) {
        console.error('Error fetching fetchList:', error)
      } finally {
        loading.value = false
      }
    }

    const handleRoll = () => {
      const bottomOfWindow =
        window.innerHeight + window.scrollY >= document.documentElement.scrollHeight
      if (bottomOfWindow && !loading.value && paging.value.hasNextPage) {
        fetchList(page.value)
      }
    }

    onMounted(() => {
      fetchList(page.value)
      window.addEventListener('scroll', handleRoll)
    })

    return {
      paging,
      loading,
      totalCount,
      handleRoll
    }
  }
}
</script>

<style scoped>
.bookmark-container {
  height: 100vh;
  overflow-y: auto; /* Scrollable container */
}

.loading {
  text-align: center;
  padding: 16px;
}

.end-of-list {
  text-align: center;
  padding: 16px;
  color: #888;
}
</style>
