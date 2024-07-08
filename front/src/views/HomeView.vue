<script setup lang="ts">
import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";
const posts = ref([]); // 반응형 데이터로 받기
const router = useRouter();

axios.get("/api/posts?page=1&size=5")
.then(response => {
  response.data.forEach((r: any) => {
    posts.value.push(r); // 값을 출력
  })
})
const moveToRead = () => {
  router.push((r));
}
</script>

<template>
  <ul>
    <li v-for="post in posts" :key="post.id">
      <div>
        <router-link :to="{name:'read', params: {postId :post.id}}">{{post.title}}</router-link>
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
