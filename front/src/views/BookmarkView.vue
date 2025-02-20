<template>
  <div>
    <Breadcrumb :home="breadcrumbHome" :model="breadcrumbItems" />
  </div>

  <div class="bookmark-wrapper">
    <span class="totalCount">북마크 수: {{ totalCount }} </span>
    <div class="bookmark-container" ref="bookmarkContainer">
      <!--북마크 포스트 리스트-->
      <div v-for="post in state.postList.items" :key="post.postId" class="bookmark-item">
        <router-link :to="{ name: 'read', params: { postId: post.postId } }" class="post-title">
          {{ post.title }}
        </router-link>
        <p class="post-content">{{ post.getShortenContent() }}</p>
        <div class="post-meta">
          <span>작성자: {{ post.userId }}</span>
          <span>작성일: {{ post.getDisplayRegDate() }}</span>
          <span>좋아요: {{ post.likesCount }}</span>
        </div>
      </div>

      <!-- 로딩 인디케이터 -->
      <div v-if="loading" class="loading">
        <p>로딩 중...</p>
      </div>

      <!-- 무한 스크롤이 끝났다는 메시지 -->
      <div v-if="!loading && !state.postList.hasNextPage" class="end-of-list">
        <p>더 이상 불러올 게시물이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { container } from 'tsyringe'
import BookmarkRepository from '@/repository/BookmarkRepository'
import Paging from '@/entity/data/Paging'
import PostView from '@/entity/post/PostView'
import PostViewComponent from '@/components/PostView.vue'

import { useRouter } from 'vue-router'
const router = useRouter()
export default {
  components: {
    PostView: PostViewComponent
  },
  setup() {
    type StateType = {
      postList: Paging<PostView>
    }
    const state = reactive<StateType>({
      postList: new Paging<PostView>()
    })
    const router = useRouter()
    const BOOKMARK_REPOSITORY = container.resolve(BookmarkRepository)
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
        if (items.length) {
          if (pageNumber === 1) {
            state.postList.setItems(items)
          } else {
            state.postList.setItems([...state.postList.items, ...items])
          }
          state.postList.setHasNextPage(hasNextPage)
          page.value += 1
        } else {
          state.postList.setHasNextPage(false)
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
          container.scrollHeight - container.scrollTop <= container.clientHeight + 50 // 오차 허용

        if (bottomOfContainer && !loading.value && state.postList.hasNextPage) {
          fetchBookmarks(page.value)
        }
      }
    }
    const breadcrumbHome = ref({ icon: 'pi pi-home', command: () => router.push('/') })
    const breadcrumbItems = ref([
      { label: '📚 북마크'}])

    onMounted(() => {
      fetchBookmarks(page.value)

      if (bookmarkContainer.value) {
        bookmarkContainer.value.addEventListener('scroll', handleScroll)
      }
    })

    onBeforeUnmount(() => {
      bookmarkContainer.value?.removeEventListener('scroll', handleScroll)
    })

    return {
      state,
      loading,
      totalCount,
      bookmarkContainer,
      breadcrumbHome,
      breadcrumbItems
    }
  }
}
</script>

<style scoped>
.bookmark-wrapper {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
  font-family: Arial, sans-serif;
  color: #333;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.total-count {
  font-size: 14px;
  color: #666;
}

.bookmark-container {
  height: 500px;
  overflow-y: auto;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  padding: 16px;
  border-radius: 8px;
}

.bookmark-item {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.post-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 8px;
  color: #0073e6;
}

.post-content {
  margin: 0 0 8px;
  color: #555;
  font-size: 14px;
  line-height: 1.5;
}

.post-meta {
  font-size: 12px;
  color: #888;
  display: flex;
  justify-content: space-between;
}

.loading,
.end-of-list {
  text-align: center;
  padding: 16px;
  color: #888;
  font-style: italic;
}
</style>
