import 'reflect-metadata'
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import WriteView from '../views/WriteView.vue'
import ReadView from '../views/ReadView.vue'
import EditView from '@/views/EditView.vue'
import LoginView from '@/views/LoginView.vue'
import BookmarkView from '@/views/BookmarkView.vue'
import ToReadList from '@/components/ToReadList.vue'
import MyPageView from '@/views/MyPageView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/write',
      name: 'write',
      component: WriteView
    },
    {
      path: '/read/:postId', // 해당 postId 값이 보이도록
      name: 'read',
      component: ReadView,
      props: true // read 컴포넌트의 props에서 값을 받을 수 있다
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/posts/:postId',
      name: 'post',
      component: ReadView,
      props: true
    },
    {
      path: '/edit/:postId',
      name: 'edit',
      component: EditView,
      props: true // edit 컴포넌트의 props에서 값을 받을 수 있다
    },
    {
      path: '/users/bookmarks',
      name: 'bookmark',
      component: BookmarkView,
      props: true
    },
    {
      path: '/myPage',
      name: 'MyPage',
      component: MyPageView
    }
  ]
})

export default router
