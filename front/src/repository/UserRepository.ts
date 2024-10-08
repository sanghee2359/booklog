import HttpRepository from '@/repository/HttpRepository'
import type Login from '@/entity/user/Login'
import { inject, singleton } from 'tsyringe'
import UserProfile from '@/entity/user/UserProfile'
import type UserEdit from '@/entity/user/UserEdit'
@singleton()
export default class UserRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}
  public login(request: Login) {
    // class
    return this.httpRepository.post({
      path: '/api/auth/login',
      body: request
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

  edit(request: UserEdit) {
    return this.httpRepository.patch({
      path: `/api/users/setting`,
      body: request
    })
  }
}
