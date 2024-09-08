import HttpRepository from '@/repository/HttpRepository'
import { inject, singleton } from 'tsyringe'
import UserProfile from '@/entity/user/UserProfile'
import { instanceToPlain, plainToInstance } from 'class-transformer'

@singleton()
export default class ProfileRepository {
  private getProfileKey(userId: number) {
    return `profile_${userId}`
  }

  public setProfile(profile: UserProfile) {
    const json = instanceToPlain(profile)
    localStorage.setItem(this.getProfileKey(profile.id), JSON.stringify(json))
  }

  public clear(userId: number) {
    localStorage.removeItem(this.getProfileKey(userId))
  }
}
