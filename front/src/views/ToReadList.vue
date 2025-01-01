<template>
  <div class="to-read-list">
    <!-- 책 추가 -->
    <AddBookForm @addBook="addBook" />
  </div>

  <el-table :data="books" style="margin-top: 20px" border>
    <!-- Title Column -->
    <el-table-column prop="title" label="Title">
      <template #default="{ row }">
        <BookCard :book="row" field="title" />
      </template>
    </el-table-column>

    <!-- Author Column -->
    <el-table-column prop="author" label="Author">
      <template #default="{ row }">
        <BookCard :book="row" field="author" />
      </template>
    </el-table-column>

    <!-- Status Column -->
    <el-table-column prop="status" label="Status">
      <template #default="{ row }">
        <BookCard :book="row" field="status" />
      </template>
    </el-table-column>

    <!-- Start Date Column -->
    <el-table-column prop="startDate" label="Start Date">
      <template #default="{ row }">
        <BookCard :book="row" field="startDate" />
      </template>
    </el-table-column>
    <!-- Start Date Column -->
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
        <el-button type="danger" size="small" @click="deleteBook(row.id)"> Delete </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script lang="ts">
import { ref, computed } from 'vue'
import BookCard from '@/components/BookCard.vue'
import AddBookForm from '@/components/AddBookForm.vue'
interface Book {
  title: string
  author: string
  status: string
  startDate: string
  endDate: string
}
export default {
  name: 'ToReadList',
  components: { BookCard, AddBookForm },
  setup() {
    const books = ref([]) // 서버에서 가져온 책 데이터
    // 책 추가
    const addBook = (newBook: Book) => {
      books.value.push(newBook)
    }

    // 책 상태 업데이트
    const handleUpdateStatus = (book: Book) => {
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
      books.value = books.value.filter((b) => b.id !== bookId)
    }

    return { books, addBook, handleUpdateStatus, deleteBook }
  }
}
</script>

<style scoped>
.to-read-list {
  /* 스타일 정의 */
}
</style>
