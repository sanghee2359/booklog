<template>
  <el-form :model="book" :rules="rules" ref="form" label-width="100px">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="Title" prop="title">
          <el-input v-model="book.title" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="Author">
          <el-input v-model="book.author" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="Status" prop="status">
          <el-select v-model="book.status">
            <el-option label="Not Started" value="NOT_STARTED" />
            <el-option label="Reading" value="READING" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12" v-if="book.status === 'READING'">
        <el-form-item label="Start Date" prop="startDate">
          <el-date-picker v-model="book.startDate" type="date" placeholder="Select date" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item>
      <el-button
        type="success"
        @click="submit"
        :loading="loading"
        :disabled="loading || !isFormValid"
      >
        {{ loading ? 'Adding...' : 'Add Book' }}
      </el-button>
    </el-form-item>
  </el-form>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, ref } from 'vue'
import { format } from 'date-fns'
import { ElForm, ElMessage } from 'element-plus'
import { container } from 'tsyringe'
import BookRepository from '@/repository/BookRepository'
import BookSave from '@/entity/book/BookSave'

export default defineComponent({
  name: 'AddBookForm',
  emits: ['addBook'],
  setup(_, { emit }) {
    type StateType = {
      book: BookSave
    }
    const state = reactive<StateType>({
      book: new BookSave()
    })
    const loading = ref(false) // 로딩 상태 관리
    const formRef = ref<InstanceType<typeof ElForm>>(null) // ElForm 타입을 명시적으로 지정
    const BOOK_REPOSITORY = container.resolve(BookRepository)

    // validate
    const rules = {
      title: [{ required: true, message: '책 제목을 입력하세요.', trigger: 'blur' }],
      author: [{ required: true, message: '작가를 입력하세요.', trigger: 'blur' }],
      status: [{ required: true, message: '진행 상태를 선택해주세요.', trigger: 'change' }],
      startDate: [{ required: true, message: '시작 날짜를 입력하세요.', trigger: 'change' }]
    }

    // 폼의 필드들이 모두 올바르게 작성되었는지 확인
    const isFormValid = computed(() => {
      // title, author, status, 그리고 status가 'READING'일 때 startDate가 모두 작성되었는지 확인
      return (
        state.book.title &&
        state.book.author &&
        state.book.status &&
        (state.book.status !== 'READING' || state.book.startDate)
      )
    })
    const resetForm = () => {
      if (formRef.value) {
        formRef.value.resetFields() // 필드 초기화
      }
      state.book = new BookSave() // BookSave 객체로 초기화
    }
    const submit = async () => {
      if (loading.value) return // 이미 로딩 중이면 추가 요청을 막는다.
      loading.value = true // 로딩 시작

      // 선택된 날짜를 LocalDate 형식으로 변환 -> 'date-fns'
      if (state.book.startDate) {
        state.book.startDate = format(state.book.startDate, 'yyyy-MM-dd')
      }
      try {
        await BOOK_REPOSITORY.saveBook(state.book)
        ElMessage.success('읽고 싶은 책이 성공적으로 저장되었습니다.')
        // bookList 배열이 없다면 초기화
        if (!state.bookList) {
          state.bookList = [] // bookList 배열 초기화
        }
        // 새로운 책 추가
        state.bookList = [...state.bookList, state.book] // 배열에 새로운 책을 추가

        // Form 초기화
        resetForm()
        console.log('Form successfully submitted. Emitting addBook event...')

        emit('addBook')
      } catch (error) {
        console.error('Error save Book: ', error)
        ElMessage.error('읽고 싶은 책 저장에 실패했습니다.')
      } finally {
        loading.value = false
      }
    }

    return { book: state.book, submit, loading, formRef, rules, isFormValid }
  }
})
</script>

<style scoped>
.add-book-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
}

label {
  font-weight: bold;
}

input,
select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 0.5rem;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
