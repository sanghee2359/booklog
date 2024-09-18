<script lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import PostRepository from '@/repository/PostRepository'
import { LikeResponse } from '@/entity/data/LikeResponse'
import { container } from 'tsyringe'
import { plainToInstance } from 'class-transformer'
import { ElMessage } from 'element-plus'

export default {
  props: {
    postId: {
      type: Number,
      required: true
    },
    initialStatus: {
      type: Boolean,
      required: true
    },
    count: {
      type: Number,
      required: true
    },
    isLoggedIn: {
      type: Boolean,
      required: true
    }
  },
  setup(props) {
    const likeResponse = ref<LikeResponse>(
      new LikeResponse(props.postId, { liked: props.initialStatus }, props.count)
    )
    const POST_REPOSITORY = container.resolve(PostRepository)
    const loading = ref(true)

    // 이미지 경로 설정
    const false_like = '/images/heart/false_heart.png'
    const true_like = '/images/heart/true_heart.PNG'

    // likesCount를 포맷팅하여 k로 표시
    const formattedLikesCount = computed(() => {
      const likes = likeResponse.value.likesCount
      if (likes >= 1000) {
        return (likes / 1000).toFixed(1).replace(/\.0$/, '') + 'k'
      }
      return likes.toString()
    })

    // 좋아요 상태에 따른 이미지 선택
    const currentImage = computed(() => {
      if (!props.isLoggedIn) {
        return false_like
      }
      return likeResponse.value.liked ? true_like : false_like
    })

    // 좋아요 상태와 개수를 가져오는 함수
    const fetchLikesCount = async () => {
      try {
        const response = await POST_REPOSITORY.getLikesCount(props.postId, LikeResponse)
        likeResponse.value = plainToInstance(LikeResponse, response)
      } catch (error) {
        console.error('Error fetching likes count:', error)
      } finally {
        loading.value = false // 데이터 로드 후 로딩 상태를 false로 설정
      }
    }
    const toggleLike = async () => {
      // 로그인하지 않은 경우 liked를 false로 설정
      if (!props.isLoggedIn) {
        ElMessage({ type: 'error', message: `로그인이 필요합니다.` })
        location.href = '/login'
        return
      }
      loading.value = true

      try {
        const response = await POST_REPOSITORY.toggleLike(props.postId, LikeResponse)
        likeResponse.value = plainToInstance(LikeResponse, response)
      } catch (error) {
        console.error('Error toggling like:', error)
        // Rollback state if error occurs
        ElMessage({ type: 'error', message: `좋아요 변경 실패` })

        likeResponse.value = new LikeResponse(
          props.postId,
          !likeResponse.value.liked,
          likeResponse.value.likesCount
        )
      } finally {
        loading.value = false
      }
    }

    // 컴포넌트가 마운트 될 때 좋아요 상태를 초기화
    onMounted(() => {
      fetchLikesCount()
    })

    // props의 initialStatus 변경 감지
    watch(
      () => props.initialStatus,
      (newStatus) => {
        if (likeResponse.value) {
          likeResponse.value.liked = newStatus
        }
      }
    )

    return {
      likeResponse,
      loading,
      toggleLike,
      currentImage,
      formattedLikesCount
    }
  }
}
</script>

<template>
  <div @click="toggleLike" class="image-toggle-button" :class="{ loading: loading }">
    <div v-if="!loading" class="like-container">
      <img :src="currentImage" alt="Toggle Like" class="like-image" />
      <span class="like-count">{{ formattedLikesCount }}</span>
    </div>
    <span v-else>Loading...</span>
  </div>
</template>

<style scoped>
.image-toggle-button {
  cursor: pointer;
}

.image-toggle-button img {
  height: 25px;
}

.image-toggle-button.loading {
  opacity: 0.5;
  pointer-events: none;
}

.like-container {
  display: inline-flex;
  align-items: center;
}

.like-image {
  display: inline-block;
}

.like-count {
  display: inline-block;
  margin-left: 6px; /* 오른쪽으로 이동 */
  vertical-align: middle; /* 이미지와 수직 정렬 */
  position: relative;
  top: -2px; /* 위쪽으로 이동 */
}
</style>
