<script setup lang="ts">
import type PostView from '@/entity/post/PostView'

const props = defineProps<{
  post: PostView
}>()
</script>
<template>
  <div class="title">
    <router-link :to="{ name: 'post', params: { postId: props.post.id } }">
      {{ props.post.title }}
    </router-link>
  </div>
  <div class="regDate">{{ props.post.getDisplaySimpleRegDate() }}</div>
  <div class="content">
    {{ props.post.getShortenContent() }}
  </div>
  <div class="readMore">
    <router-link :to="{ name: 'post', params: { postId: props.post.id } }"> Read more </router-link>
  </div>
</template>

<style scoped lang="scss">
.title {
  a {
    color: inherit;
    text-decoration: none;
    font-size: 1.4rem;
    font-weight: 400;

    &:hover {
      text-decoration: underline;
    }
  }
}
.regDate {
  margin-top: 0.5rem;
  font-size: 0.78rem;
  font-weight: 300;
}
.content {
  margin-top: 0.88rem;
  word-break: normal;
  white-space: break-spaces;
}
</style>
