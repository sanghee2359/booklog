<template>
  <form @submit.prevent="onSubmit" class="add-book-form">
    <div>
      <label for="title">Title</label>
      <input id="title" v-model="book.title" required />
    </div>
    <div>
      <label for="author">Author</label>
      <input id="author" v-model="book.author" required />
    </div>
    <div>
      <label for="status">Status</label>
      <select id="status" v-model="book.status" required>
        <option value="NOT_STARTED">To Read</option>
        <option value="READING">Reading</option>
        <option value="COMPLETED">Completed</option>
      </select>
    </div>
    <button type="submit">Add Book</button>
  </form>
</template>

<script lang="ts">
import { defineComponent, reactive } from 'vue'

interface Book {
  title: string
  author: string
  status: string
  startDate: string
}

export default defineComponent({
  name: 'AddBookForm',
  emits: ['addBook'],
  setup(_, { emit }) {
    const book = reactive<Book>({
      title: '',
      author: '',
      status: 'NOT_STARTED'
    })

    const onSubmit = () => {
      emit('addBook', { ...book })
      resetForm()
    }

    const resetForm = () => {
      book.title = ''
      book.author = ''
      book.status = 'NOT_STARTED'
    }

    return { book, onSubmit }
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
