<template>
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
import { onMounted, reactive, ref } from 'vue'
import BookCard from '@/components/BookCard.vue'
import BookView from '@/entity/book/BookView'
import Paging from '@/entity/data/Paging'
import { container } from 'tsyringe'
import BookRepository from '@/repository/BookRepository'
import { ElForm } from 'element-plus'

export default {
  name: 'ToReadList',
  components: { BookCard },
  setup() {
    type StateType = {
      bookList: Paging<BookView>
    }
    const state = reactive<StateType>({
      bookList: new Paging<BookView>()
    })
    const formRef = ref<InstanceType<typeof ElForm>>() // ElForm 타입을 명시적으로 지정
    // 페이지네이션 처리
    const pageSize = 6
    const page = ref(1)
    const loading = ref(false) // 로딩 상태 관리
    const BOOK_REPOSITORY = container.resolve(BookRepository)

    const getBookList = async (pageNumber: number): Promise<Paging<BookView>> => {
      if (loading.value) return // 로딩 중이면 반환
      loading.value = true
      try {
        const response = await BOOK_REPOSITORY.getCompletedBooks(pageNumber, pageSize)
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

    // 책 삭제
    const deleteBook = (bookId: number) => {
      state.bookList.content = state.bookList.content.filter((b) => b.id !== bookId)
    }
    const handlePageChange = (newPage: number) => {
      if (newPage !== page.value) {
        page.value = newPage // 페이지 번호 업데이트
        getBookList(newPage) // 페이지 변경 시, reset 플래그를 true로 설정하여 새 데이터로 교체
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
      deleteBook,
      handlePageChange,
      loading,
      formRef
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
