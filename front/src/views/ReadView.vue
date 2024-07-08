<script setup lang="ts">
import {onMounted, ref} from "vue";
import axios from "axios";

const props = defineProps({
  postId: {
    type: [Number,String], // url path로 넘어가서 String 변환됨
    require: true,
  },
});

const post = ref({ // 데이터 초기화
  id : 0,
  title: "",
  content: "",
});

onMounted(() => {
  axios.get(`/api/posts/${props.postId}`).then(response => {
      post.value = response.data;
      });
});

</script>
<template>
  <h2>{{post.title}}</h2>
  <div>{{ post.content }}</div>
</template>