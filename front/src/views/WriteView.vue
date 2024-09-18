<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import PostWrite from '@/entity/post/PostWrite'
import { container } from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import { ElForm, ElMessage } from 'element-plus'
import type HttpError from '@/http/HttpError'

const state = reactive({
  postWrite: new PostWrite()
})

const POST_REPOSITORY = container.resolve(PostRepository)
const formRef = ref<InstanceType<typeof ElForm>>()

function write() {
  POST_REPOSITORY.write(state.postWrite)
    .then(() => {
      ElMessage({ type: 'success', message: '글 등록 완료' })
      location.href = '/'
    })
    .catch((e: HttpError) => {
      ElMessage({ type: 'error', message: e.getMessage() })
    })
}

const rules = {
  title: [
    { required: true, message: '제목을 입력해주세요.', trigger: 'blur' },
    { pattern: /.+/, message: '제목을 입력해주세요.', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '내용을 입력해주세요.', trigger: 'blur' },
    { pattern: /.+/, message: '내용을 입력해주세요.', trigger: 'blur' }
  ]
}

// 버튼의 활성화 상태를 업데이트
function updateButtonState() {
  const form = formRef.value
  if (form) {
    form.validate((valid: boolean) => {
      buttonDisabled.value = !valid
    })
  }
}
// ref로 버튼 상태를 관리
const buttonDisabled = ref(true)

// title과 content의 변경 감지하여 updateButtonState 호출
watch(() => state.postWrite.title, updateButtonState)
watch(() => state.postWrite.content, updateButtonState)
</script>

<template>
  <el-form label-position="top" :model="state.postWrite" ref="formRef" :rules="rules">
    <el-form-item label="제목" prop="title">
      <el-input v-model="state.postWrite.title" size="large" placeholder="제목을 입력해주세요" />
    </el-form-item>

    <el-form-item label="내용" prop="content">
      <el-input
        v-model="state.postWrite.content"
        type="textarea"
        rows="15"
        placeholder="내용을 입력해주세요"
      />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" style="width: 100%" @click="write" :disabled="buttonDisabled">
        등록완료
      </el-button>
    </el-form-item>
  </el-form>
</template>
