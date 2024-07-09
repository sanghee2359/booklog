<script setup lang="ts">
import {onMounted, ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const router = useRouter();

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

const moveToEdit = () => {
  router.push({name: "edit", params: {postId: props.postId}})
}

onMounted(() => {
  axios.get(`/api/posts/${props.postId}`).then(response => {
    post.value = response.data;
  });
});

</script>
<template>
  <el-row>
    <el-col>
    <h2 class="title">{{post.title}}</h2>
      <div class="sub d-flex ">
          <div class="category">개발</div>
          <div class="regDate">2024-07-09</div>
      </div>
    </el-col>
  </el-row>

  <el-row class="mt-3">
    <el-col>
    <div class="content">{{ post.content }}</div>
    </el-col>
  </el-row>

  <el-row class="mt-3">
    <el-col>
      <div class="d-flex justify-content-end">
      <el-button type="warning" @click="moveToEdit">수정</el-button>
      </div>
    </el-col>
  </el-row>
</template>`

<style scoped lang="scss">
.title{
  font-size: 1.6rem;
  font-weight: 600;
  color:#222324;
  margin: 0;
}
.content {
  font-size : 0.95rem;
  margin-top : 12px;
  margin-left : 3px;
  color: #616161;
  white-space: break-spaces; // 띄어쓰기 , 줄간격 등
  line-height: 1.5;
}
.sub{
  margin-top: 6px;
  font-size: 0.78rem;

  .regDate {
    margin-left: 10px;
    color :#6b6b6b;
  }
}
</style>