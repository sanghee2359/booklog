import HttpRepository from '@/repository/HttpRepository'
import type Login from '@/entity/user/Login'

export default class UserRepository extends HttpRepository {
  public login(request: Login) {
    // class
    return this.post({
      path: '/api/auth/login',
      body: request
    })
  }
}
