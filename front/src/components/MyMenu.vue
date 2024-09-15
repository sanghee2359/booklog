<template>
  <el-menu
    default-active="2"
    class="el-menu-vertical-demo"
    :collapse="isCollapse"
    @open="handleOpen"
    @close="handleClose"
  >
    <el-menu-item index="1">
      <el-icon><setting /></el-icon>
      <template #title>사용자 정보 변경</template>
    </el-menu-item>
    <el-menu-item index="2">
      <el-icon><ZoomIn /></el-icon>
      <template #title>읽을 책 리스트</template>
    </el-menu-item>
    <el-menu-item index="3">
      <el-icon><document /></el-icon>
      <template #title>작성글</template>
    </el-menu-item>
    <el-menu-item index="4">
      <el-icon><Star /></el-icon>
      <template #title>완독 기록</template>
    </el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { Document, Setting, ZoomIn, Star } from '@element-plus/icons-vue'

const isCollapse = ref(true)
const handleOpen = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
const handleClose = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<style>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>
