import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        accessToken: localStorage.getItem('accessToken') || null
    }),
    actions: {
        setAccessToken(token: string) {
            this.accessToken = token
            localStorage.setItem('accessToken', token) // ✅ 새로고침해도 유지
        },
        clearAccessToken() {
            this.accessToken = null
            localStorage.removeItem('accessToken') // 로그아웃 시 삭제
        }
    }
})
