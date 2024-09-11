<script setup lang="ts">
import { onBeforeMount, reactive } from 'vue'
import { container } from 'tsyringe'
import UserRepository from '@/repository/UserRepository'
import ProfileRepository from '@/repository/ProfileRepository'
import type UserProfile from '@/entity/user/UserProfile'
import { ElMessage } from 'element-plus'
import { Collection, EditPen, House, User } from '@element-plus/icons-vue'

const USER_REPOSITORY = container.resolve(UserRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
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
</script>
<template>
  <ul class="menus">
    <li class="menu">
      <router-link to="/">
        <el-icon><House /></el-icon> HOME</router-link
      >
    </li>

    <li class="menu" v-if="state.profile !== null">
      <router-link to="/write"
        ><el-icon><EditPen /></el-icon> 글 작성</router-link
      >
    </li>

    <li class="menu" v-if="state.profile !== null">
      <router-link to="/users/bookmarks">
        <el-icon><Collection /></el-icon> 북마크</router-link
      >
    </li>

    <li class="menu" v-if="state.profile !== null">
      <router-link to="/myPage">
        <el-icon><User /></el-icon> 마이페이지</router-link
      >
    </li>
  </ul>
</template>
<style scoped lang="scss">
.menus {
  height: 20px;
  list-style: none;
  padding: 0;
  font-size: 0.88rem;
  font-weight: 300;
  text-align: center;
  margin: 0;
}

.menu {
  display: inline;
  margin-right: 1rem;

  &:last-child {
    margin-right: 0;
  }

  a {
    color: inherit;
  }
}
</style>
