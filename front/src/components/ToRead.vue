<template>
  <div>
    <el-form :model="book" ref="form">
      <el-form-item label="Title">
        <el-input v-model="book.title" />
      </el-form-item>
      <el-form-item label="Author">
        <el-input v-model="book.author" />
      </el-form-item>
      <el-form-item label="Status">
        <el-select v-model="book.status">
          <el-option label="Not Started" value="NOT_STARTED" />
          <el-option label="Reading" value="READING" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="book.status !== 'NOT_STARTED'" label="Start Date">
        <el-date-picker v-model="book.startDate" type="date" placeholder="Select date" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">Submit</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="books" style="margin-top: 20px">
      <el-table-column prop="title" label="Title" />
      <el-table-column prop="author" label="Author" />
      <el-table-column prop="startDate" label="Start Date" />
      <el-table-column prop="price" label="Price" />
      <el-table-column prop="status" label="Status" />
    </el-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, reactive } from 'vue'
import { Book, createBook, getBooks } from '@/entity/data/Book.ts'

export default defineComponent({
  setup() {
    // Reactive book object
    const book = reactive<Book>({
      title: '',
      author: '',
      startDate: null,
      status: 'NOT_STARTED'
    })

    const books = ref<Book[]>([])

    // Fetch books from the backend
    const fetchBooks = async () => {
      try {
        books.value = await getBooks()
      } catch (error) {
        console.error('Error fetching books:', error)
      }
    }

    // Submit the book data to the backend
    const submit = async () => {
      try {
        const savedBook = await createBook(book) // Send the reactive book object
        console.log('Book saved successfully:', savedBook)
        // Reset the form after submission
        book.title = ''
        book.author = ''
        book.startDate = null
        book.price = 0
        book.status = 'NOT_STARTED'
        await fetchBooks() // Refresh the list
      } catch (error) {
        console.error('Error saving book:', error)
      }
    }

    onMounted(fetchBooks) // Fetch books on component mount

    return {
      book,
      books,
      submit
    }
  }
})
</script>

<style scoped>
/* Add your styles here */
</style>
