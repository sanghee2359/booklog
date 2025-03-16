<script lang="ts" setup>
import {reactive, ref} from 'vue';
import {container} from "tsyringe";
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Checkbox from 'primevue/checkbox';
import Message from 'primevue/message';
import {ElMessage} from "element-plus";
import type HttpError from '@/http/HttpError';
import SignUp from '@/entity/user/SignUp';
import UserRepository from '@/repository/UserRepository';

const state = reactive({
  signUp: new SignUp(),
})
const USER_REPOSITORY = container.resolve(UserRepository)


const confirmPassword = ref('');
const rememberMe = ref(false);
const emailError = ref(false);
const passwordError = ref(false);
const confirmPasswordError = ref(false);
const formError = ref(false);
const isSubmitting = ref(false);

const submitForm = async () => {
  if (isSubmitting.value) return; // 이미 요청 중이면 무시
  isSubmitting.value = true;
  // 초기화
  formError.value = false;
  emailError.value = false;
  confirmPasswordError.value = false;

  // 이메일 형식 검증
  if (!state.signUp.email.includes("@") || !state.signUp.email.includes(".")) {
    emailError.value = true;
  }

  // 비밀번호 확인
  if (state.signUp.password !== confirmPassword.value) {
    confirmPasswordError.value = true;
  }

  // 오류가 있을 경우
  if (emailError.value || confirmPasswordError.value) {
    formError.value = true;
    return;
  }

  // 폼 제출 로직
  console.log('Form submitted');
  try {
    await signUpUser(); // 회원가입 요청
  } catch (e) {
    console.error("회원가입 실패:", e);
  } finally {
    isSubmitting.value = false;
  }
};

async function signUpUser() {
  USER_REPOSITORY.signup(state.signUp)
      .then(() => {
        // 성공했을 때
        ElMessage({ type: 'success', message: '회원가입이 완료되었습니다! 😊' })
        location.href = '/' // home으로 이동
      })
      .catch((e: HttpError) => {
        // 실패했을 때
        console.log(e.response?.data.message)
        ElMessage({ type: 'error', message: e.getMessage() })
      })
}
</script>

<template>
  <div class="mt-7 bg-white border border-gray-200 rounded-xl shadow-sm dark:bg-neutral-900 dark:border-neutral-700">
    <div class="p-4 sm:p-7">
      <div class="text-center">
        <h1 class="block text-2xl font-bold text-gray-800 dark:text-white">Sign up</h1>
        <p class="mt-2 text-sm text-gray-600 dark:text-neutral-400">
          Already have an account?
          <router-link class="text-blue-600 decoration-2 hover:underline focus:outline-none focus:underline font-medium dark:text-blue-500" to="/login">
            Sign in here
          </router-link>
        </p>
      </div>

      <div class="mt-5">
        <Button class="w-full p-3" icon="pi pi-google" label="Sign up with Google" />
        <div class="py-3 flex items-center text-xs text-gray-400 uppercase before:flex-1 before:border-t before:border-gray-200 before:me-6 after:flex-1 after:border-t after:border-gray-200 after:ms-6 dark:text-neutral-500 dark:before:border-neutral-600 dark:after:border-neutral-600">Or</div>

        <!-- 폼 오류 메시지 -->
        <Message v-if="formError" class="mb-4" severity="error" text="Please correct the errors above." />

        <form @submit.prevent="submitForm">
          <div class="grid gap-y-4">
            <div>
              <label class="block text-sm mb-2 dark:text-white" for="name">Name</label>
              <InputText id="name" v-model="state.signUp.name" class="w-full" required />
            </div>
            <div>
              <label class="block text-sm mb-2 dark:text-white" for="email">Email address</label>
              <InputText id="email" v-model="state.signUp.email" class="w-full" required type="email" />
              <Message v-if="emailError" class="mt-1" severity="error" text="Please enter a valid email address." />
            </div>
            <div>
              <label class="block text-sm mb-2 dark:text-white" for="password">Password</label>
              <InputText id="password" v-model="state.signUp.password" class="w-full" required type="password" />
            </div>
            <div>
              <label class="block text-sm mb-2 dark:text-white" for="confirm-password">Confirm Password</label>
              <InputText id="confirm-password" v-model="confirmPassword" class="w-full" required type="password" />
              <Message v-if="confirmPasswordError" class="mt-1" severity="error" text="Passwords do not match." />
            </div>

            <div class="flex items-center">
              <Checkbox id="remember-me" v-model="rememberMe" class="mr-3" />
              <label class="text-sm dark:text-white" for="remember-me">
                I accept the
                <a class="text-blue-600 decoration-2 hover:underline focus:outline-none focus:underline font-medium dark:text-blue-500" href="#">Terms and Conditions</a>
              </label>
            </div>

            <Button :disabled="isSubmitting" class="w-full p-3 mt-4" label="Sign up" type="submit" />
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 기본 Message 컴포넌트 스타일 수정 */
.p-message-error {
  background-color: #f8d7da !important;
  color: #721c24 !important;
  border-color: #f5c6cb !important;
}

.p-message-error .p-message-text {
  color: #721c24 !important;
}

/* 필요시 더 추가 스타일 */
</style>
