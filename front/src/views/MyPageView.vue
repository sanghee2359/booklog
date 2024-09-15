<template>
  <el-row>
    <!-- 메뉴 바가 왼쪽에 위치 -->
    <el-col :span="4" class="menu-column">
      <MyMenu />
    </el-col>

    <!-- 콘텐츠가 오른쪽에 위치 -->
    <el-col :span="20" class="content-column">
      <router-view />
    </el-col>
  </el-row>
</template>

<style>
.menu-column {
  min-height: 100vh; /* 메뉴가 전체 높이를 차지하도록 설정 */
}

.content-column {
  padding: 5px; /* Content의 패딩 */
  background-color: #ffffff; /* Content 배경 색상, 필요에 따라 조정 */
}
</style>
<script setup lang="ts">
import MyMenu from '@/components/MyMenu.vue'
</script>
