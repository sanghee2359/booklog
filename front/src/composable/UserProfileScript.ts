import { reactive, onBeforeMount } from 'vue'
import { container } from 'tsyringe' // Dependency Injection Library
import UserRepository from '@/repository/UserRepository'
import ProfileRepository from '@/repository/ProfileRepository'
import UserProfile from '@/entity/user/UserProfile'

const USER_REPOSITORY = container.resolve(UserRepository)
const PROFILE_REPOSITORY = container.resolve(ProfileRepository)

type StateType = {
  profile: UserProfile | null
}

const state = reactive<StateType>({
  profile: null
})

export function useUserProfile() {
  onBeforeMount(() => {
    USER_REPOSITORY.getProfile()
      .then((profile) => {
        console.log('User profile:', profile)
        PROFILE_REPOSITORY.setProfile(profile)
        state.profile = profile
      })
      .catch((error) => {
        console.error('Error fetching profile:', error)
      })
  })

  return {
    profile: state.profile
  }
}
