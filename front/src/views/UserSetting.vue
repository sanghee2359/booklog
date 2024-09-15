<template>
  <div class="user-form">
    <h2>사용자 정보 수정</h2>
    <form @submit.prevent="submitForm">
      <div class="form-group">
        <label for="blogName">블로그 이름</label>
        <input
          v-model="form.blogName"
          type="text"
          id="blogName"
          placeholder="블로그 이름을 입력하세요"
        />
      </div>

      <div class="form-group">
        <label for="nickname">블로그 닉네임</label>
        <input
          v-model="form.nickname"
          type="text"
          id="nickname"
          placeholder="닉네임을 입력하세요"
        />
      </div>

      <div class="form-group">
        <label for="description">블로그 설명</label>
        <textarea
          v-model="form.description"
          id="description"
          placeholder="블로그 설명을 입력하세요"
        ></textarea>
      </div>

      <button type="submit">수정하기</button>
    </form>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive } from 'vue'
import axios from 'axios'

export default defineComponent({
  setup() {
    const form = reactive({
      blogName: '하루하루 쌓아가기', // 초기값 설정
      nickname: 'hehe', // 초기값 설정
      description: '전공과 관련된 포스트를 작성합니다..' // 초기값 설정
    })

    const submitForm = async () => {
      try {
        // 서버에 데이터를 전송하는 API 요청
        const response = await axios.put('/api/users/update', {
          blogName: form.blogName,
          nickname: form.nickname,
          description: form.description
        })
        alert('수정 완료!')
      } catch (error) {
        console.error('수정 오류: ', error)
        alert('수정 중 문제가 발생했습니다.')
      }
    }

    return {
      form,
      submitForm
    }
  }
})
</script>
