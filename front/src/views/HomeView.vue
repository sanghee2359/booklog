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
      <div class="title">
        <router-link :to="{name:'read', params: {postId :post.id}}">{{post.title}}</router-link>
      </div>
      <div class="content">
        {{post.content}}
      </div>
      <div class="sub d-flex ">
        <div class="category">개발</div>
        <div class="regDate">2024-07-09</div>
      </div>
    </li>
  </ul>
  <main>
  </main>
</template>
<style scoped lang="scss">

ul {
  list-style: none;
  padding: 0;
}
li {
   margin-bottom: 2rem;
   .title {
     a {
       font-size: 1.2rem;
       color: #222324;
       text-decoration: none;
     }
   }
   .content {
     font-size : 0.85rem;
     margin-top : 8px;
     margin-left : 3px;
     color :#7e7e7e;
   }

   &:last-child {
    margin-bottom: 0;
  }

  .sub{
    margin-top: 9px;
    font-size: 0.78rem;

    .regDate {
      margin-left: 10px;
      color :#6b6b6b;
    }
  }
 }

</style>
