<script setup lang="ts">
import { computed, onMounted, reactive, watch } from 'vue'
import { container } from 'tsyringe'
import { ElMessage } from 'element-plus'
import UserRepository from '@/repository/UserRepository'
import UserEdit from '@/entity/user/UserEdit'
import type UserProfile from '@/entity/user/UserProfile'

type StateType = {
  profile: UserProfile | null
  edit: UserEdit | null
}

const state = reactive<StateType>({
  profile: null,
  edit: new UserEdit()
})

const USER_REPOSITORY = container.resolve(UserRepository)

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
      ElMessage({ type: 'error', message: `${props.userId}번 유저 조회 실패` })
    })
}

// Function to send edited profile data to the server
function editUserProfile() {
  if (!state.edit) return

  USER_REPOSITORY.edit(state.edit)
    .then(() => {
      ElMessage({ type: 'success', message: '프로필 수정 완료' })
      location.href = `/userSetting`
    })
    .catch(() => {
      ElMessage({ type: 'error', message: `프로필 수정 실패` })
    })
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

watch(
  () => state.profile?.password,
  (newPassword) => {
    if (state.edit) {
      state.edit.password = newPassword || ''
    }
  }
)
</script>

<template>
  <el-row v-if="state.profile && state.edit" class="edit-page">
    <el-col :span="24" class="edit-col">
      <el-card class="edit-card">
        <h2 class="edit-title">프로필 수정</h2>
        <!-- Ensure that el-form is rendered only if state.edit is not null -->
        <el-form v-if="state.edit" :model="state.edit" ref="editForm">
          <el-form-item label="Name" class="form-item">
            <el-input v-model="state.edit.name" placeholder="Enter new name" />
          </el-form-item>
          <el-form-item label="Email" class="form-item">
            <el-input v-model="state.edit.email" placeholder="Enter new email" />
          </el-form-item>
          <el-form-item label="Password" class="form-item">
            <el-input v-model="state.edit.password" placeholder="Enter new password" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="editUserProfile" :disabled="isButtonDisabled">
              Update Profile
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>
<style lang="scss" scoped>
.edit-page {
  padding: 20px;
  display: flex;
  justify-content: center; // Center the content horizontally
}

.edit-col {
  display: flex;
  justify-content: center; // Center the column content horizontally
}

.edit-card {
  width: 100%;
  max-width: 800px; // Set a maximum width for the card
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.edit-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.form-item {
  margin-bottom: 20px;
}

.el-button {
  margin-top: 20px;
}

@media (max-width: 600px) {
  .edit-card {
    padding: 15px;
  }

  .edit-title {
    font-size: 20px;
  }

  .form-item {
    margin-bottom: 15px;
  }

  .el-button {
    margin-top: 15px;
  }
}
</style>
