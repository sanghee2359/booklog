import axios from 'axios'

// Axios 인스턴스 생성
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true // refreshToken 쿠키 포함
})

// Request Interceptor accessToken 자동 추가
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 응답 인터셉터: 401 발생 시 자동으로 토큰 갱신
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      try {
        await refreshAccessToken() // 토큰 갱신 시도
        return api.request(error.config) // 원래 요청 다시 보내기
      } catch {
        localStorage.removeItem('accessToken')
        window.location.href = '/login' // 로그인 페이지로 이동
      }
    }
    return Promise.reject(error)
  }
)

// accessToken 갱신 함수
async function refreshAccessToken() {
  const response = await axios.post('/v1/auth/token/refresh', {}, { withCredentials: true })
  const newToken = response.headers['authorization']?.split('Bearer ')[1]

  if (newToken) {
    localStorage.setItem('accessToken', newToken)
  }
}

export default api
