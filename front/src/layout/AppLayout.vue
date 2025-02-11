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

watch(isSidebarActive, (newVal) => {
  if (newVal) {
    bindOutsideClickListener()
  } else {
    unbindOutsideClickListener()
  }
})

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

function bindOutsideClickListener() {
  if (!outsideClickListener.value) {
    outsideClickListener.value = (event) => {
      if (isOutsideClicked(event)) {
        layoutState.overlayMenuActive = false
        layoutState.staticMenuMobileActive = false
        layoutState.menuHoverActive = false
      }
    }
    document.addEventListener('click', outsideClickListener.value)
  }
}

function unbindOutsideClickListener() {
  if (outsideClickListener.value) {
    document.removeEventListener('click', outsideClickListener)
    outsideClickListener.value = null
  }
}

function isOutsideClicked(event) {
  const sidebarEl = document.querySelector('.layout-sidebar')
  const topbarEl = document.querySelector('.layout-menu-button')

  return !(
    sidebarEl.isSameNode(event.target) ||
    sidebarEl.contains(event.target) ||
    topbarEl.isSameNode(event.target) ||
    topbarEl.contains(event.target)
  )
}
</script>

<template>
  <div :class="containerClass" class="layout-wrapper">
    <div
      class="py-6 px-6 mx-0 md:mx-12 lg:mx-20 lg:px-20 flex items-center justify-between relative lg:static"
    >
      <TopbarWidget></TopbarWidget>
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
