<template>
  <div class="book-card">
    <!-- 필드에 따라 데이터 출력 -->
    <span v-if="field === 'title'">{{ book?.title || 'No Title' }}</span>
    <span v-else-if="field === 'author'">{{ book?.author || 'No Author' }}</span>
    <span v-else-if="field === 'status'">{{ book?.status || 'No Status' }}</span>
    <span v-else-if="field === 'startDate'">{{ book?.startDate || '-' }}</span>
    <span v-else-if="field === 'endDate'">{{ book?.endDate || '-' }}</span>
    <span v-else>Invalid Field</span>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import type BookView from '@/entity/book/BookView'

export default defineComponent({
  name: 'BookCard',
  props: {
    book: {
      type: Object as PropType<BookView>,
      required: true
    },
    field: {
      type: String,
      required: true
    }
  }
})
</script>

<style scoped>
.book-card {
  padding: 5px;
  font-size: 14px;
  color: #333;
}
</style>
