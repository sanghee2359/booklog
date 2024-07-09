<script setup lang="ts">
import {ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const router = useRouter();
const post = ref({ // 데이터 초기화
  id : 0,
  title: "",
  content: "",
});

const props = defineProps({
  postId: {
    type: [Number,String], // url path로 넘어가서 String 변환됨
    require: true,
  },
});
// 수정 전 post의 값 그대로 두기 위함
axios.get(`/api/posts/${props.postId}`).then(response => {
  post.value = response.data;
});
const edit = () => {
  axios.patch(`/api/posts/${props.postId}`, post.value).then(response => {
    router.replace({name: "home"})
  });
}

</script>

<template>
  <div>
<!--    초기값   -->
    <el-input v-model="post.title"/>
  </div>

  <div class="mt-2">
    <el-input v-model="post.content" type="textarea" rows="15"></el-input>
  </div>
  <div class="mt-2 d-flex justify-content-end">
    <el-button type="warning" @click="edit()">수정완료</el-button>
  </div>
</template>
<style>

</style>