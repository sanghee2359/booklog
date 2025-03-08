import HttpRepository from '@/repository/HttpRepository'
import { inject, singleton } from 'tsyringe'
import UserProfile from '@/entity/user/UserProfile'
import type UserEdit from '@/entity/user/UserEdit'
import type Login from '@/entity/user/Login'
import type SignUp from "@/entity/user/SignUp";
@singleton()
export default class UserRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}
  public signup(request: SignUp) {
    // class
    return this.httpRepository.post({
      path: '/api/v1/auth/register',
      body: request,
      skipAuth: true
    })
  }
  public login(request: Login) {
    // class
    return this.httpRepository.post({
      path: '/api/v1/auth/token',
      body: request,
      skipAuth: true
    })
  }
  public logout() {
    // class
    return this.httpRepository.post({
      path: '/api/v1/auth/logout',
      skipAuth: true
    })
  }
  getProfile() {
    return this.httpRepository.get<UserProfile>(
      {
        path: '/api/users/me'
      },
      UserProfile
    )
  }
  getUserProfile(userId: number) {
    return this.httpRepository.get<UserProfile>(
        {
          path: `/api/users/${userId}`,
          skipAuth: true
        },
        UserProfile
    )
  }

  edit(request: UserEdit) {
    return this.httpRepository.patch({
      path: `/api/users/setting`,
      body: request
    })
  }

  // 인증 상태 확인
  public isAuthenticated(): boolean {
    const token = localStorage.getItem('accessToken')
    return !!token // 토큰이 존재하면 true 반환, 아니면 false 반환
  }
}
