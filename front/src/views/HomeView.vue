<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="content">
    <ul class="posts">
      <li><Post /></li>
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
