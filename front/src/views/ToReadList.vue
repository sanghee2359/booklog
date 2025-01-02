<template>
  <div class="to-read-list">
    <!-- 책 추가 -->
    <AddBookForm @addBook="addBook" />
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
import { onMounted, reactive, ref } from 'vue'
import BookCard from '@/components/BookCard.vue'
import AddBookForm from '@/components/AddBookForm.vue'
import type BookView from '@/entity/book/BookView'
import type BookSave from '@/entity/book/BookVBookSaveiew'
import Paging from '@/entity/data/Paging'
import { container } from 'tsyringe'
import BookRepository from '@/repository/BookRepository'

export default {
  name: 'ToReadList',
  components: { BookCard, AddBookForm },
  setup() {
    type StateType = {
      bookList: Paging<BookView>
    }

    const state = reactive<StateType>({
      bookList: new Paging<BookView>()
    })

    // 페이지네이션 처리
    const pageSize = 6
    const page = ref(1)

    const BOOK_REPOSITORY = container.resolve(BookRepository)

    // Fetch books from the backend
    const getBookList = async (pageNumber: number): Promise<Paging<BookView>> => {
      try {
        const response = await BOOK_REPOSITORY.getPendingBooks(pageNumber, pageSize)
        const { items, hasNextPage, totalCount } = response
        // 댓글 목록 업데이트
        state.bookList.items.push(...items)

        // 상태 업데이트
        state.bookList.setHasNextPage(hasNextPage)
        state.bookList.setTotalCount(totalCount)

        if (hasNextPage) {
          page.value += 1
        }
      } catch (error) {
        console.error('Error fetching book list:', error)
        // Handle the error by possibly showing an error message to the user
        state.bookList = new Paging<BookView>() // Return empty paging in case of error
      }
    }

    // 책 추가 -> ToReadList의 bookList 상태에 추가
    // 자식 컴포넌트의 데이터를 부모 컴포넌트와 동기화
    const addBook = async (newBook: BookSave) => {
      try {
        const savedBook = await BOOK_REPOSITORY.saveBook(newBook)
        state.bookList.items.push(savedBook)
      } catch (error) {
        console.error('Error adding book:', error)
      }
    }

    // 책 상태 업데이트
    const handleUpdateStatus = (book: BookView) => {
      if (book.status === 'NOT_STARTED') {
        book.status = 'READING'
        book.startDate = new Date().toISOString().split('T')[0] // Start Date 설정
      } else if (book.status === 'READING') {
        book.status = 'COMPLETED'
        book.endDate = new Date().toISOString().split('T')[0] // End Date 설정
      }
    }

    // 책 삭제
    const deleteBook = (bookId: number) => {
      state.bookList.content = state.bookList.content.filter((b) => b.id !== bookId)
    }
    // Handle pagination change
    const handlePageChange = (newPage: number) => {
      page.value = newPage
      getBookList(newPage)
    }

    onMounted(() => {
      getBookList(page.value)
    })

    return {
      state,
      pageSize,
      page,
      getBookList,
      addBook,
      handleUpdateStatus,
      deleteBook,
      handlePageChange
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
