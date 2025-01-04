<template>
  <div class="to-read-list">
    <!-- 책 추가 -->
    <el-form :model="state.bookSave" :rules="rules" ref="formRef" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Title" prop="title">
            <el-input v-model="state.bookSave.title" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Author" prop="author">
            <el-input v-model="state.bookSave.author" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="Status" prop="status">
            <el-select v-model="state.bookSave.status">
              <el-option label="Not Started" value="NOT_STARTED" />
              <el-option label="Reading" value="READING" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="state.bookSave.status === 'READING'">
          <el-form-item label="Start Date" prop="startDate">
            <el-date-picker
              v-model="state.bookSave.startDate"
              type="date"
              placeholder="Select date"
            />
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
  </div>

  <!-- 책 리스트 -->
  <el-table :data="state.bookList.items" style="margin-top: 20px" border>
    <!-- Title -->
    <el-table-column prop="title" label="Title">
      <template #default="{ row }">
        <BookCard :book="row" field="title" />
      </template>
    </el-table-column>

    <!-- Author -->
    <el-table-column prop="author" label="Author">
      <template #default="{ row }">
        <BookCard :book="row" field="author" />
      </template>
    </el-table-column>

    <!-- Status -->
    <el-table-column prop="status" label="Status">
      <template #default="{ row }">
        <BookCard :book="row" field="status" />
      </template>
    </el-table-column>

    <!-- Start Date -->
    <el-table-column prop="startDate" label="Start Date">
      <template #default="{ row }">
        <BookCard :book="row" field="startDate" />
      </template>
    </el-table-column>

    <!-- End Date -->
    <el-table-column prop="endDate" label="End Date">
      <template #default="{ row }">
        <BookCard :book="row" field="endDate" />
      </template>
    </el-table-column>

    <!-- Actions Column -->
    <el-table-column label="Actions">
      <template #default="{ row }">
        <el-button type="primary" size="small" @click="handleUpdateStatus(row)">
          Update Status
        </el-button>
        <el-button type="danger" size="small" @click="deleteBook(row.id)">Delete</el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- Pagination -->
  <div class="pagination-wrapper">
    <el-pagination
      background
      :page-size="pageSize"
      :current-page="page"
      :total="state.bookList.totalCount"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import BookCard from '@/components/BookCard.vue'
import BookView from '@/entity/book/BookView'
import BookSave from '@/entity/book/BookSave'
import Paging from '@/entity/data/Paging'
import { container } from 'tsyringe'
import BookRepository from '@/repository/BookRepository'
import { ElForm, ElMessage } from 'element-plus'

export default {
  name: 'ToReadList',
  components: { BookCard },
  setup() {
    type StateType = {
      bookSave: BookSave
      book: BookView
      bookList: Paging<BookView>
    }
    const state = reactive<StateType>({
      bookSave: new BookSave(),
      book: new BookView(),
      bookList: new Paging<BookView>()
    })
    const formRef = ref<InstanceType<typeof ElForm>>() // ElForm 타입을 명시적으로 지정
    // 페이지네이션 처리
    const pageSize = 6
    const page = ref(1)
    const loading = ref(false) // 로딩 상태 관리
    const BOOK_REPOSITORY = container.resolve(BookRepository)
    // validate
    const rules = {
      title: [{ required: true, message: '책 제목을 입력하세요.', trigger: 'blur' }],
      author: [{ required: true, message: '작가를 입력하세요.', trigger: 'blur' }],
      startDate: [{ required: true, message: '시작 날짜를 입력하세요.', trigger: 'change' }]
    }

    // 폼의 필드들이 모두 올바르게 작성되었는지 확인
    const isFormValid = computed(() => {
      // title, author, status, 그리고 status가 'READING'일 때 startDate가 모두 작성되었는지 확인
      return (
        state.bookSave.title &&
        state.bookSave.author &&
        state.bookSave.status &&
        (state.bookSave.status !== 'READING' || state.bookSave.startDate)
      )
    })

    const getBookList = async (pageNumber: number): Promise<Paging<BookView>> => {
      if (loading.value) return // 로딩 중이면 반환
      loading.value = true
      try {
        const response = await BOOK_REPOSITORY.getPendingBooks(pageNumber, pageSize)
        const { items, hasNextPage, totalCount } = response
        // 책 목록 업데이트
        state.bookList.setItems(items) // 현재 페이지에 해당하는 책 목록 데이터로 교체
        // 상태 업데이트
        console.log(response)
        state.bookList.setHasNextPage(hasNextPage)
        state.bookList.setTotalCount(totalCount)
      } catch (error) {
        console.error('Error fetching book list:', error)
      } finally {
        loading.value = false
      }
    }
    // 댓글 목록 초기화 및 새로고침
    const refetchList = async () => {
      await getBookList(page.value) // reset 플래그를 true로 설정하여 새로고침
    }
    // 책 추가 -> ToReadList의 bookList 상태에 추가
    // 자식 컴포넌트의 데이터를 부모 컴포넌트와 동기화
    const submit = async () => {
      // startDate를 LocalDate로 변환
      const bookDataToSend = new BookSave({
        ...state.bookSave,
        startDate: state.bookSave.startDate
          ? state.bookSave.toLocalDate(state.bookSave.startDate) // toLocalDate로 변환한 값을 전달
          : null
      })
      try {
        await BOOK_REPOSITORY.saveBook(bookDataToSend)
        state.bookSave = new BookSave()
        ElMessage.success('읽고 싶은 책이 성공적으로 저장되었습니다.')
        await refetchList() // 현재 페이지를 새로고침
      } catch (error) {
        console.error('Error save Book: ', error)
        ElMessage.error('읽고 싶은 책 저장에 실패했습니다.')
      }
    }

    // 책 상태 업데이트
    const handleUpdateStatus = (book: BookView) => {
      if (book.status === 'NOT_STARTED') {
        book.status = 'READING'
        book.startDate = toLocalDate(new Date()) // Start Date 설정
      } else if (book.status === 'READING') {
        book.status = 'COMPLETED'
        book.endDate = toLocalDate(new Date())
      }
    }
    // Handle paginati

    // 책 삭제
    const deleteBook = (bookId: number) => {
      state.bookList.content = state.bookList.content.filter((b) => b.id !== bookId)
    }
    const handlePageChange = (newPage: number) => {
      if (newPage !== page.value) {
        page.value = newPage // 페이지 번호 업데이트
        getBookList(newPage, true) // 페이지 변경 시, reset 플래그를 true로 설정하여 새 데이터로 교체
      }
    }

    onMounted(() => {
      getBookList(page.value)
    })

    return {
      state,
      pageSize,
      page,
      getBookList,
      submit,
      handleUpdateStatus,
      deleteBook,
      handlePageChange,
      isFormValid,
      loading,
      formRef,
      rules
    }
  }
}
</script>

<style scoped>
.to-read-list {
  padding: 20px;
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
