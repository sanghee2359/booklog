<template>
  <span class="totalCount">북마크 수: {{ totalCount }} </span>
  <div class="bookmark-container" ref="bookmarkContainer">
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
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { container } from 'tsyringe'
import BookmarkRepository from '@/repository/BookmarkRepository'
import Paging from '@/entity/data/Paging'
import PostView from '@/entity/data/PostView'
import PostViewComponent from '@/components/PostView.vue'

export default {
  components: {
    PostView: PostViewComponent
  },
  setup() {
    const BOOKMARK_REPOSITORY = container.resolve(BookmarkRepository)
    const paging = ref(new Paging<PostView>())
    const loading = ref(false)
    const page = ref(1)
    const pageSize = 5
    const totalCount = ref(0)

    // DOM 요소에 대한 ref
    const bookmarkContainer = ref<HTMLElement | null>(null)

    const fetchBookmarks = async (pageNumber: number) => {
      loading.value = true
      try {
        const response = await BOOKMARK_REPOSITORY.getBookmarks(pageNumber, pageSize)
        const { items, hasNextPage, totalCount: responseTotalCount } = response
        totalCount.value = responseTotalCount
        console.log('Fetched items:', items)
        console.log('Total Count:', totalCount.value)
        console.log('Has Next Page:', hasNextPage)

        if (items.length) {
          if (pageNumber === 1) {
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
        console.error('Error fetching bookmarks:', error)
      } finally {
        loading.value = false
      }
    }

    const handleScroll = () => {
      if (bookmarkContainer.value) {
        const container = bookmarkContainer.value
        const bottomOfContainer =
          container.scrollHeight - container.scrollTop <= container.clientHeight + 1

        if (bottomOfContainer && !loading.value && paging.value.hasNextPage) {
          fetchBookmarks(page.value)
        }
      }
    }

    onMounted(() => {
      fetchBookmarks(page.value)
      bookmarkContainer.value?.addEventListener('scroll', handleScroll)
    })

    onBeforeUnmount(() => {
      bookmarkContainer.value?.removeEventListener('scroll', handleScroll)
    })

    // watch ref for changes and setup event listeners again if necessary
    watch(
      () => bookmarkContainer.value,
      (newValue, oldValue) => {
        if (oldValue) oldValue.removeEventListener('scroll', handleScroll)
        if (newValue) newValue.addEventListener('scroll', handleScroll)
      }
    )

    return {
      paging,
      loading,
      totalCount,
      bookmarkContainer
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
