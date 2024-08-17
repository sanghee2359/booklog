<script lang="ts" setup>
import { onBeforeMount, reactive, ref, toRefs } from 'vue'
import { ElButton, ElDrawer, ElMessage } from 'element-plus'
import { CircleCloseFilled } from '@element-plus/icons-vue'
import UserProfile from '@/entity/user/UserProfile'
import { container } from 'tsyringe'
import UserRepository from '@/repository/UserRepository'
import ProfileRepository from '@/repository/ProfileRepository'

const USER_REPOSITORY = container.resolve(UserRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
const squareUrl = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png'
type StateType = {
  profile: UserProfile | null
}

const state = reactive<StateType>({
  profile: null
})
onBeforeMount(() => {
  USER_REPOSITORY.getProfile().then((profile) => {
    console.log(profile)
    PROFILE_REPOSITORY.setProfile(profile)
    state.profile = profile
  })
})
function logout() {
  ElMessage({ type: 'message', message: '로그아웃 되었습니다.' })
  PROFILE_REPOSITORY.clear()
  location.href = '/api/logout'
}
// 서랍의 열림 상태 관리
const visible = ref(false)
</script>

<template>
  <div>
    <el-button type="info" plain @click="visible = true">Info</el-button>
    <!-- 사용자 프로필 서랍 -->
    <el-drawer v-model="visible" direction="rtl" :show-close="false" size="30%">
      <!-- 서랍의 크기 설정 -->
      <template #header="{ close }">
        <h4>User Profile</h4>
        <el-button type="danger" @click="close">
          <el-icon class="el-icon--left"><CircleCloseFilled /></el-icon>
          Close
        </el-button>
      </template>

      <div v-if="state.profile === null">
        <router-link to="/login">로그인</router-link>
      </div>

      <div v-else>
        <el-col :span="12">
          <div class="block">
            <el-avatar shape="square" :size="100" :src="squareUrl" />
          </div>
        </el-col>
        <p><strong>ID:</strong> {{ state.profile.id }}</p>
        <p><strong>Name:</strong> {{ state.profile.name }}</p>
        <a href="#" @click="logout()"> {{ state.profile!.name }} 로그아웃 </a>
      </div>
    </el-drawer>
  </div>
</template>
<style scoped>
/* 서랍 스타일 커스터마이징 */
.el-drawer__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.demo-basic {
  text-align: center;
}
.demo-basic .sub-title {
  margin-bottom: 10px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}
.demo-basic .demo-basic--circle,
.demo-basic .demo-basic--square {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.demo-basic .block:not(:last-child) {
  border-right: 1px solid var(--el-border-color);
}
.demo-basic .block {
  flex: 1;
}
.demo-basic .el-col:not(:last-child) {
  border-right: 1px solid var(--el-border-color);
}
</style>
