<script lang="ts" setup>
import { useRouter } from 'vue-router'
import {onBeforeMount, reactive, ref, watch} from 'vue'
import {container} from "tsyringe";
import UserRepository from "@/repository/UserRepository";
import ProfileRepository from "@/repository/ProfileRepository";
import UserProfile from "@/entity/user/UserProfile";
import {ElMessage} from "element-plus";
import {useAuthStore} from "@/stores/auth";

const USER_REPOSITORY = container.resolve(UserRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)
const router = useRouter()
const authStore = useAuthStore()
const isActive = ref(false)
const props = defineProps<{
  isAuthenticated: Boolean
}>()
const toggleMenu = () => {
  isActive.value = !isActive.value
}
type StateType = {
  profile: UserProfile | null
}

const state = reactive<StateType>({
  profile: null
})
// 로그아웃 함수
function logout() {
  if (state.profile) {
    ElMessage({ type: 'success', message: '로그아웃 되었습니다.' })
    PROFILE_REPOSITORY.clear(state.profile.id)
  }
  authStore.clearAccessToken() // client : access token 삭제
  USER_REPOSITORY.logout() // back : refresh token 삭제
  window.location.href = '/'
}
// 프로필 설정
onBeforeMount(async () => {
  if(props.isAuthenticated) {
    try {
      const profile = await USER_REPOSITORY.getProfile()
      if (profile) {
        PROFILE_REPOSITORY.setProfile(profile)
        state.profile = profile
      } else {
        console.error('No profile data found')
      }
    } catch (error) {
      console.error('Error fetching profile:', error)
    }
  }
})
// 페이지 이동 후 메뉴 닫기
watch(
  () => router.currentRoute.value.path,
  () => {
    isActive.value = false
  }
)
</script>

<template>
  <a class="flex items-center" href="#">
    <img alt="logo" class="h-12 mr-2" src="/images/logo_1.png" />
    <span
      class="text-surface-900 dark:text-surface-0 font-medium text-2xl leading-normal mr-20"
      style="
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
      "
    >
      Book Log
    </span>
  </a>

  <Button class="lg:!hidden" rounded severity="secondary" @click="toggleMenu">
    <i class="pi pi-bars !text-2xl"></i>
  </Button>
  <div
    :class="[
      isActive ? 'block animate-scalein' : 'hidden animate-fadeout',
      'items-center bg-surface-0 dark:bg-surface-900 grow justify-between absolute lg:static w-full left-0 top-full px-12 lg:px-0 z-20 rounded-border',
      'lg:block' // lg 이상에서 div를 보이도록 설정
    ]"
  >
    <ul
      class="list-none p-0 m-0 flex lg:items-center select-none flex-col lg:flex-row cursor-pointer gap-8"
    >
      <li>
        <a
          class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium text-xl"
          @click="$router.push('/')"
        >
          <span>Home</span>
        </a>
      </li>
      <li>
        <a
          class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium text-xl"
          @click="$router.push('/landing')"
        >
          <span>소개</span>
        </a>
      </li>
      <li v-show="props.isAuthenticated">
        <a
          class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium text-xl"
          @click="$router.push('/myPage')"
        >
          <span>마이페이지</span>
        </a>
      </li>
      <li  v-show="props.isAuthenticated">
        <a
          class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium text-xl"
          @click="$router.push('/write')"
        >
          <span>글 작성</span>
        </a>
      </li>
      <li v-show="props.isAuthenticated">
        <a
            class="px-0 py-4 text-surface-900 dark:text-surface-0 font-medium text-xl"
            @click="$router.push('/users/bookmarks')"
        >
          <span>북마크</span>
        </a>
      </li>
    </ul>
    <div v-if="!props.isAuthenticated"  class="flex border-t lg:border-t-0 border-surface py-4 lg:py-0 mt-4 lg:mt-0 gap-2" >
      <Button as="router-link" label="Login" rounded text to="/login"></Button>
      <Button label="Register" rounded @click="$router.push('/register')"></Button>
    </div>
    <div v-if="props.isAuthenticated"  class="flex border-t lg:border-t-0 border-surface py-4 lg:py-0 mt-4 lg:mt-0 gap-2">
      <Button label="Logout" outlined severity="contrast" @click="logout()"></Button>
    </div>
  </div>
</template>
