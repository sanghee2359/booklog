<template>
  <div @click="toggleImage" class="image-toggle-button">
    <img :src="currentImage" alt="Toggle Image" />
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'ImageToggleButton',
  setup() {
    const false_bookmark = '../../public/images/heart/false_heart.png' // 첫 번째 이미지 경로
    const true_bookmark = '../../public/images/heart/true_heart.png' // 두 번째 이미지 경로

    // 현재 보여지는 이미지 상태를 관리합니다.
    const isImage1 = ref(true)

    // 이미지 토글 함수
    const toggleImage = () => {
      isImage1.value = !isImage1.value
    }

    // 현재 보여지는 이미지의 경로를 반환합니다.
    const currentImage = computed(() => {
      return isImage1.value ? false_bookmark : true_bookmark
    })

    return {
      currentImage,
      toggleImage
    }
  }
})
</script>
<style scoped>
.image-toggle-button {
  cursor: pointer;
}

.image-toggle-button img {
  width: 30px; /* 원하는 크기로 조절하세요 */
  height: auto;
}
</style>
