<template>
  <div class="book-card">
    <!-- 필드에 따라 데이터 출력 -->
    <span v-if="field === 'title'">{{ book?.title || 'No Title' }}</span>
    <span v-else-if="field === 'author'">{{ book?.author || 'No Author' }}</span>
    <span v-else-if="field === 'status'">{{ book?.status || 'No Status' }}</span>
    <!--    <span v-else-if="field === 'startDate'">{{ book?.startDate || '-' }}</span>-->
    <!--    <span v-else-if="field === 'endDate'">{{ book?.endDate || '-' }}</span>-->

    <!-- startDate 필드일 때 DatePicker -->
    <div v-if="field === 'startDate'">
      <template v-if="book?.startDate">
        <!-- startDate 값 출력 -->
        {{ book.startDate.toString() }}
      </template>
      <template v-else>
        <!-- startDate가 없을 경우 DatePicker 표시 -->
        <el-date-picker
          v-model="editableStartDate"
          type="date"
          placeholder="읽기 시작일"
          @change="notifyChange('startDate', editableStartDate)"
          size="small"
          class="date-picker"
        />
      </template>
    </div>
    <!-- endDate 필드 -->
    <div v-else-if="field === 'endDate'">
      <template v-if="book?.endDate">
        <!-- endDate 값 출력 -->
        {{ book.endDate.toString() }}
      </template>
      <template v-else>
        <!-- endDate가 없을 경우 DatePicker 표시 -->
        <el-date-picker
          v-model="editableEndDate"
          type="date"
          placeholder="완독일"
          @change="notifyChange('endDate', editableEndDate)"
          size="small"
          class="date-picker"
        />
      </template>
    </div>
  </div>
</template>

<script lang="ts">
import type { PropType } from 'vue'
import { defineComponent, ref } from 'vue'
import type BookView from '@/entity/book/BookView'

export default defineComponent({
  name: 'BookCard',
  props: {
    book: {
      // 부모로부터 받는 book data
      type: Object as PropType<BookView>,
      required: true
    },
    field: {
      type: String,
      required: true
    }
  },
  setup(props, { emit }) {
    const editableStartDate = ref<Date | null>(null)
    const editableEndDate = ref<Date | null>(null)

    const notifyChange = (field: string, value: Date | null) => {
      emit('update-book', { id: props.book.bookId, field, value })
    }

    return {
      editableStartDate,
      editableEndDate,
      notifyChange
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
/* Date-picker 크기 조정 */
::v-deep(.el-date-editor--small) {
  height: 28px; /* 높이 조정 */
  width: 50px; /* 너비 조정 (필요에 따라 변경) */
  font-size: 12px; /* 글자 크기 */
}

::v-deep(.el-picker-panel) {
  font-size: 12px; /* 캘린더 글자 크기 */
}
</style>
