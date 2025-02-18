<script lang="ts" setup>
import { useLayout } from '@/layout/composables/layout'
import { computed, onBeforeMount, reactive, ref, watch } from 'vue'
import UserProfile from '@/components/UserProfile.vue'
import { container } from 'tsyringe'
import UserRepository from '@/repository/UserRepository'
import TopbarWidget from '@/components/landing/TopbarWidget.vue'
import AppFooter from '@/layout/AppFooter.vue'

const USER_REPOSITORY = container.resolve(UserRepository)
type StateType = {
  isAuthenticated: boolean
}
const state = reactive<StateType>({
  isAuthenticated: false
})
onBeforeMount(() => {
  state.isAuthenticated = USER_REPOSITORY.isAuthenticated() // 로그인 여부 확인
})

const { layoutConfig, layoutState, isSidebarActive } = useLayout()
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

<style scoped></style>
