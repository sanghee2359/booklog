<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const title = ref(" ");
const content = ref(" ");
const router = useRouter()
const write = function() {
  axios.post("/api/posts", {
    title: title.value,
    content: content.value
  }).then(() => {
    router.replace({name:"home"}); // 글 작성 후 home으로 이동(뒤로가기 x)
  })

};
</script>

<template>
  <div>
    <el-input v-model="title" placeholder="제목을 입력해주세요"/>  </div>

  <div class="mt-2">
    <el-input v-model="content" type="textarea" rows="15"></el-input>
  </div>
  <div class="mt-2">
    <el-button type="primary" @click="write()">글 작성 완료</el-button>
  </div>
</template>
<style>

</style>