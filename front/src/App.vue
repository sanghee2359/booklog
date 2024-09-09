<script setup lang="ts">
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import Menu from '@/components/Menu.vue'
import Content from '@/components/Content.vue'
import UserProfile from '@/components/UserProfile.vue'
</script>

<template>
  <div class="drawer-button">
    <el-row>
      <UserProfile />
    </el-row>
  </div>

  <el-row>
    <el-col :span="22" :offset="1">
      <Header />
    </el-col>
  </el-row>

  <el-row>
    <el-col :span="22" :offset="1">
      <Menu />
    </el-col>
  </el-row>

  <el-row class="content-wrapper">
    <el-col
      :span="22"
      :offset="1"
      :sm="{ span: 20, offset: 2 }"
      :md="{ span: 16, offset: 4 }"
      :lg="{ span: 14, offset: 5 }"
    >
      <Content />
    </el-col>
  </el-row>
  <el-row class="footer-wrapper">
    <el-col>
      <Footer />
    </el-col>
  </el-row>
</template>

<style scoped>
body {
  font-family:
    'Pretendard Variable',
    Pretendard,
    -apple-system,
    BlinkMacSystemFont,
    system-ui,
    Roboto,
    'Helvetica Neue',
    'Segoe UI',
    'Apple SD Gothic Neo',
    'Noto Sans KR',
    'Malgun Gothic',
    'Apple Color Emoji',
    'Segoe UI Emoji',
    'Segoe UI Symbol',
    sans-serif;
}

.drawer-button {
  position: absolute;
  top: 0;
  right: 0;
  margin: 16px; /* 버튼과 서랍 사이의 여백 */
}
.content-wrapper {
  flex-grow: 1; /* 남는 공간을 차지 */
  margin-bottom: 40px; /* Content와 Footer 사이의 여백 */
}

.footer-wrapper {
  margin-top: auto; /* footer를 하단에 위치시킴 */
}
</style>
