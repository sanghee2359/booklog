<script setup lang="ts">
import {ref} from "vue";
const count = ref(0);
</script>

<template>
  <div>
    <input type="text" placeholder="제목을 입력해주세요"/>
  </div>

  <div>
    <textarea rows="15"></textarea>
  </div>
  <button>글 작성 완료</button>
</template>
<style>

</style>