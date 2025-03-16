<script lang="ts" setup>
import { useLayout } from '@/layout/composables/layout'
import { computed, onBeforeMount, reactive, ref, watch } from 'vue'
import { container } from 'tsyringe'
import UserRepository from '@/repository/UserRepository'
import TopbarWidget from '@/components/landing/TopbarWidget.vue'
import AppFooter from '@/layout/AppFooter.vue'
import { useAuthStore } from '@/stores/auth'

type StateType = {
  isAuthenticated: boolean
}
const state = reactive<StateType>({
  isAuthenticated: false
})

const authStore = useAuthStore()
const USER_REPOSITORY = container.resolve(UserRepository)

const {layoutConfig, layoutState} = useLayout()
const outsideClickListener = ref(null)
const containerClass = computed(() => {
  return {
    'layout-overlay': layoutConfig.menuMode === 'overlay',
    'layout-static': layoutConfig.menuMode === 'static',
    'layout-static-inactive':
        layoutState.staticMenuDesktopInactive && layoutConfig.menuMode === 'static',
    'layout-overlay-active': layoutState.overlayMenuActive,
    'layout-mobile-active': layoutState.staticMenuMobileActive
  }
})

// 초기 로그인 여부 확인
onBeforeMount(() => {
  state.isAuthenticated = USER_REPOSITORY.isAuthenticated()
})
// 전역적으로 authStore의 accessToken 변화를 감시하여 로그인 상태 변화 처리
watch(() => authStore.accessToken, (newToken, oldToken) => {
  if (newToken && newToken !== oldToken) {
    window.location.reload();
  }
})
</script>

<template>
  <div :class="containerClass" class="layout-wrapper">
    <div
      class="py-6 px-6 mx-0 md:mx-12 lg:mx-20 lg:px-20 flex items-center justify-between relative lg:static"
    >
      <TopbarWidget :isAuthenticated="Boolean(state.isAuthenticated)" ></TopbarWidget>
    </div>
    <div class="layout-main-container">
      <div class="layout-main">
        <router-view></router-view>
      </div>
      <app-footer></app-footer>
    </div>
    <div class="layout-mask animate-fadein"></div>
  </div>
  <Toast />
</template>

<style scoped>
.layout-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center; /* 콘텐츠를 수평 중앙 정렬 */
  justify-content: flex-start;
  width: 100%;
  height: 100vh; /* 페이지 전체 높이 */
}
.topbar-wrapper {
  position: fixed; /* topbar 고정 */
  top: 0;
  left: 0;
  right: 0;
  z-index: 20;
  background-color: #fff; /* 배경색을 추가 */
  display: flex;
  justify-content: space-between; /* 메뉴와 버튼을 양쪽으로 배치 */
  padding: 16px 40px; /* 상단 바 여백 */
  width: 100%;
}


.layout-main-container {
  display: flex;
  flex-direction: column;
  align-items: center; /* 콘텐츠를 수평 중앙 정렬 */
  justify-content: flex-start;
  width: 100%;
  max-width: 1200px; /* 최대 너비 설정 */
  margin: 0 auto; /* 중앙 정렬을 위한 margin */
  padding: 50px 1rem 0; /* topbar 높이만큼 여백 추가 */
}

.layout-main {
  width: 100%;
  max-width: 1200px; /* 최대 너비 설정 */
  margin: 0 auto; /* 중앙 정렬 */
  padding: 0 1rem; /* 양옆 여백 */
}

app-footer {
  width: 100%;
  max-width: 1200px; /* 최대 너비 설정 */
  margin: 20px auto;
  padding: 10px 0;
}

.layout-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 10;
}

.layout-main-container {
  margin-top: 30px; /* topbar가 고정되었으므로 이만큼 밀어야 실제 내용이 topbar와 겹치지 않음 */
}


</style>
