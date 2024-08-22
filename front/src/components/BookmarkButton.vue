<script lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { container } from 'tsyringe'
import BookmarkRepository from '@/repository/BookmarkRepository'
import { ElMessage } from 'element-plus'
import { BookmarkResponse } from '@/entity/data/BookmarkResponse'

export default {
  props: {
    postId: Number,
    initialStatus: Boolean
  },
  setup: function (props) {
    // 초기 상태 관리
    const bookmark = ref<BookmarkResponse>({
      postId: props.postId,
      status: props.initialStatus
    })

    // 현재 북마크 상태에 따라 이미지 선택
    const currentImage = computed(() => {
      return bookmark.value.status ? true_bookmark : false_bookmark
    })

    // 이미지 경로 설정
    const false_bookmark = '/images/bookmark/false_bookmark.png' // 첫 번째 이미지 경로 (절대 경로로 수정)
    const true_bookmark = '/images/bookmark/true_bookmark.png' // 두 번째 이미지 경로 (절대 경로로 수정)

    // BookmarkRepository 인스턴스
    const BOOKMARK_REPOSITORY = container.resolve(BookmarkRepository)

    const toggleBookmark = async () => {
      try {
        const response = await BOOKMARK_REPOSITORY.toggleBookmark(props.postId, bookmark.value)
        console.log('Response from API:', response)
        if (response && typeof response.status !== 'undefined') {
          bookmark.value.status = response.status
          ElMessage({ type: 'success', message: '북마크 상태가 변경되었습니다' })
        } else {
          console.log(response)
        }
      } catch (error) {
        console.error('Error occurred:', error)
        bookmark.value.status = !bookmark.value.status // Rollback state
        ElMessage({ type: 'error', message: '북마크 상태 변경 실패' })
      }
    }

    // props의 initialStatus 변경 감지
    watch(
      () => props.initialStatus,
      (newStatus) => {
        bookmark.value.status = newStatus
      }
    )
    console.log('>>>>', bookmark)
    return {
      bookmark,
      currentImage,
      toggleBookmark
    }
  }
}
</script>

<template>
  <div @click="toggleBookmark" class="image-toggle-button">
    <img :src="currentImage" alt="Toggle Image" />
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
