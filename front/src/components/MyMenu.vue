<template>
  <el-menu
    default-active="2"
    class="el-menu-vertical-demo"
    :collapse="isCollapse"
    @open="handleOpen"
    @close="handleClose"
  >
    <el-menu-item index="1">
      <router-link to="/userSetting">
        <el-icon><setting /></el-icon>
        <template #title>사용자 정보 변경</template>
      </router-link>
    </el-menu-item>
    <el-menu-item index="2">
      <router-link to="/toReadList">
        <el-icon><ZoomIn /></el-icon>
        <template #title>읽을 책 리스트</template>
      </router-link>
    </el-menu-item>
    <el-menu-item index="3">
      <router-link to="/myPostList">
        <el-icon><document /></el-icon>
        <template #title>작성글</template>
      </router-link>
    </el-menu-item>
    <el-menu-item index="4">
      <router-link to="/bookLog">
        <el-icon><Star /></el-icon>
        <template #title>완독 기록</template>
      </router-link>
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

<style></style>
