<template>
  <div class="bookmark-container" @scroll="handleScroll">
    <div v-if="loading" class="loading">Loading...</div>
    <div v-for="bookmark in bookmarks" :key="bookmark.userId">
      <div v-for="post in bookmark.bookmarks" :key="post.id">
        <div class="post">
          <h3>{{ post.title }}</h3>
          <p>{{ post.content }}</p>
        </div>
      </div>
    </div>
    <div v-if="hasNextPage" class="loading-more">Scroll down to load more...</div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

// TypeScript types
interface PostResponse {
  id: number
  title: string
  content: string
}

interface BookMarkResponse {
  userId: number
  bookmarks: PostResponse[]
}

interface PagingResponse<T> {
  page: number
  size: number
  totalCount: number
  items: T[]
  hasNextPage: boolean
}

const bookmarks = ref<BookMarkResponse[]>([])
const page = ref(1)
const size = ref(10)
const hasNextPage = ref(true)
const loading = ref(false)

const fetchBookmarks = async () => {
  if (loading.value || !hasNextPage.value) return

  loading.value = true

  try {
    const response = await axios.get<PagingResponse<BookMarkResponse>>('/api/users/bookmarks', {
      params: {
        page: page.value,
        size: size.value
      }
    })

    const { items, hasNextPage: nextPageAvailable } = response.data

    if (items.length > 0) {
      bookmarks.value.push(...items)
    }

    hasNextPage.value = nextPageAvailable
    page.value += 1
  } catch (error) {
    console.error('Error fetching bookmarks:', error)
  } finally {
    loading.value = false
  }
}

const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement

  if (target.scrollHeight - target.scrollTop <= target.clientHeight) {
    fetchBookmarks()
  }
}

// Fetch initial bookmarks
onMounted(() => {
  fetchBookmarks()
})
</script>

<style scoped>
.bookmark-container {
  height: 100vh;
  overflow-y: auto;
  padding: 1em;
}

.post {
  border-bottom: 1px solid #ddd;
  margin-bottom: 1em;
  padding-bottom: 1em;
}

.loading,
.loading-more {
  text-align: center;
  margin: 1em 0;
}
</style>
