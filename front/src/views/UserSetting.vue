<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { container } from 'tsyringe'
import { ElForm, ElMessage } from 'element-plus'
import UserRepository from '@/repository/UserRepository'
import UserEdit from '@/entity/user/UserEdit'
import type UserProfile from '@/entity/user/UserProfile'
import HttpError from '@/http/HttpError'

type StateType = {
  profile: UserProfile | null
  edit: UserEdit | null
}

const state = reactive<StateType>({
  profile: null,
  edit: new UserEdit()
})

const USER_REPOSITORY = container.resolve(UserRepository)

const formRef = ref<InstanceType<typeof ElForm>>()

// Fetch profile data and initialize edit state
function getProfile() {
  USER_REPOSITORY.getProfile()
    .then((profile: UserProfile) => {
      state.profile = profile
      state.edit = new UserEdit({
        name: profile.name,
        email: profile.email,
        password: '' // 비밀번호는 빈 문자열로 초기화
      })
    })
    .catch((e) => {
      console.error(e)
      ElMessage({ type: 'error', message: `유저 조회 실패` })
    })
}

// Function to send edited profile data to the server
function editUserProfile() {
  if (!state.edit) return

  const form = formRef.value

  if (form) {
    form.validate((valid: boolean) => {
      if (valid) {
        USER_REPOSITORY.edit(state.edit)
          .then(() => {
            ElMessage({ type: 'success', message: '프로필 수정 완료' })
            location.href = `/userSetting`
          })
          .catch((e: HttpError) => {
            // 실패했을 때
            ElMessage({ type: 'error', message: e.getMessage() })
          })
      } else {
        ElMessage({ type: 'error', message: '유효성 검사가 실패했습니다.' })
      }
    })
  }
}

// Compute whether the button should be disabled
const isButtonDisabled = computed(() => {
  const edit = state.edit
  return !edit?.name || !edit?.email || !edit?.password
})

// On component mounted, fetch the profile
onMounted(() => {
  getProfile()
})

// Watch for changes in profile content and update edit accordingly
watch(
  () => state.profile?.name,
  (newName) => {
    if (state.edit) {
      state.edit.name = newName || ''
    }
  }
)

watch(
  () => state.profile?.email,
  (newEmail) => {
    if (state.edit) {
      state.edit.email = newEmail || ''
    }
  }
)

// Password validation rule
const passwordRule = [
  { required: true, message: '비밀번호를 입력해 주세요', trigger: 'blur' },
  {
    pattern: /(?=.*[A-Za-z])(?=.*\d)(?=.*[\W_]).{10,}/,
    message: '비밀번호는 최소 8자 이상이어야 하며, 특수문자, 영문자, 숫자를 포함해야 합니다.',
    trigger: 'blur'
  }
]
</script>

<template>
  <el-row v-if="state.profile && state.edit" class="edit-page">
    <el-col :span="24" class="edit-col">
      <el-card class="edit-card">
        <h2 class="edit-title">프로필 수정</h2>
        <el-form
          v-if="state.edit"
          :model="state.edit"
          ref="formRef"
          :rules="{ password: passwordRule }"
        >
          <el-form-item label="Name" class="form-item" prop="name">
            <el-input v-model="state.edit.name" placeholder="Enter new name" />
          </el-form-item>
          <el-form-item label="Email" class="form-item" prop="email">
            <el-input v-model="state.edit.email" placeholder="Enter new email" />
          </el-form-item>
          <el-form-item label="Password" class="form-item" prop="password">
            <el-input
              type="password"
              v-model="state.edit.password"
              placeholder="Enter new password"
            />
          </el-form-item>
          <el-form-item class="button-container">
            <el-button
              class="button"
              type="primary"
              @click="editUserProfile"
              :disabled="isButtonDisabled"
            >
              Update Profile
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
.edit-title {
  padding: 0 10px; /* 열 간의 간격을 위한 패딩 추가 */
  margin-bottom: 15px; /* 위쪽 여백 추가 */
}
.button-container {
  margin-top: 20px; /* 위쪽 여백 추가 */
}
.button {
  padding: 10px;
  margin-top: 15px; /* 위쪽 여백 추가 */
}
</style>
