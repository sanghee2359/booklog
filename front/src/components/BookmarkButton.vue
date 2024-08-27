<script lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { container } from 'tsyringe'
import BookmarkRepository from '@/repository/BookmarkRepository'
import { ElMessage } from 'element-plus'
import { BookmarkResponse } from '@/entity/data/BookmarkResponse'
import { plainToInstance } from 'class-transformer'

export default {
  props: {
    postId: {
      type: Number,
      required: true
    },
    initialStatus: {
      type: Boolean,
      required: true
    }
  },
  setup: function (props) {
    const bookmark = ref<BookmarkResponse>(
      new BookmarkResponse(props.postId, { status: props.initialStatus })
    )
    const loading = ref(true)

    // 현재 북마크 상태에 따라 이미지 선택
    const currentImage = computed(() => {
      return bookmark.value.status ? true_bookmark : false_bookmark
    })

    // 이미지 경로 설정
    const false_bookmark = '/images/bookmark/false_bookmark.png' // 첫 번째 이미지 경로 (절대 경로로 수정)
    const true_bookmark = '/images/bookmark/true_bookmark.png' // 두 번째 이미지 경로 (절대 경로로 수정)

    // BookmarkRepository 인스턴스
    const BOOKMARK_REPOSITORY = container.resolve(BookmarkRepository)

    const fetchBookmarkStatus = async () => {
      try {
        bookmark.value.status = await BOOKMARK_REPOSITORY.getBookmarkStatus(props.postId)
      } catch (error) {
        console.error('Error fetching bookmark:', error)
      } finally {
        loading.value = false
      }
    }
    const toggleBookmark = async () => {
      loading.value = true

      try {
        const response = await BOOKMARK_REPOSITORY.toggleBookmark(props.postId)
        console.log('Response from API:', response)
        // `plainToInstance`로 클래스 인스턴스로 변환 (선택적, 클래스 인스턴스가 이미 반환된 경우 필요 없음)
        const bookmarkResponse = plainToInstance(BookmarkResponse, response)

        // API 응답에서 `status`가 올바르게 정의되었는지 확인
        if (bookmarkResponse && typeof bookmarkResponse.status !== 'undefined') {
          bookmark.value.status = bookmarkResponse.status
          ElMessage({ type: 'success', message: '북마크 상태가 변경되었습니다' })
        } else {
          console.error('Unexpected response format:', response)
          ElMessage({ type: 'error', message: '북마크 상태 변경 실패' })
        }
      } catch (error) {
        console.error('Error occurred during bookmark toggle:', error)
        bookmark.value.status = !bookmark.value.status // Rollback state
        ElMessage({ type: 'error', message: '북마크 상태 변경 실패' })
      } finally {
        loading.value = false
      }
    }
    onMounted(() => {
      fetchBookmarkStatus()
    })

    watch(
      () => props.initialStatus,
      (newStatus) => {
        if (bookmark.value) {
          bookmark.value.status = newStatus
        }
      }
    )
    return {
      bookmark,
      loading,
      currentImage,
      toggleBookmark
    }
  }
}
</script>

<template>
  <div @click="toggleBookmark" class="image-toggle-button" :class="{ loading: loading }">
    <img v-if="!loading" :src="currentImage" alt="Toggle Image" />
    <span v-else>Loading...</span>
  </div>
</template>

<style scoped>
.image-toggle-button {
  cursor: pointer;
}

.image-toggle-button img {
  height: auto;
}
</style>
