<script setup lang="ts">
import axios from "axios";
import {ref} from "vue";
const posts = ref([]); // 반응형 데이터로 받고

axios.get("/api/posts?page=1&size=5")
.then(response => {
  response.data.forEach((r: any) => {
    posts.value.push(r); // 값을 출력
  })
})
</script>

<template>
  <ul>
    <li v-for="post in posts" :key="post.id">
      <div>
        {{post.title}}
      </div>
      <div>
        {{post.content}}
      </div>
    </li>
  </ul>
  <main>
  </main>
</template>
<style scoped>
li {
  margin-bottom: 1rem;
}
li:last-child {
  margin-bottom: 0;
}
</style>
