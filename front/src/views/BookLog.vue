<template>
  <div>
    <Breadcrumb :home="breadcrumbHome" :model="breadcrumbItems" />
  </div>
  <div>
    <!-- 책 리스트 -->
    <el-table :data="state.bookList.items" border style="margin-top: 20px">
      <!-- Title -->
      <el-table-column label="Title" prop="title">
        <template #default="{ row }">
          <BookCard :book="row" field="title" />
        </template>
      </el-table-column>

      <!-- Author -->
      <el-table-column label="Author" prop="author">
        <template #default="{ row }">
          <BookCard :book="row" field="author" />
        </template>
      </el-table-column>

      <!-- Status -->
      <el-table-column label="Status" prop="status">
        <template #default="{ row }">
          <BookCard :book="row" field="status" />
        </template>
      </el-table-column>

      <!-- Start Date -->
      <el-table-column label="Start Date" prop="startDate">
        <template #default="{ row }">
          <BookCard :book="row" field="startDate" />
        </template>
      </el-table-column>

      <!-- End Date -->
      <el-table-column label="End Date" prop="endDate">
        <template #default="{ row }">
          <BookCard :book="row" field="endDate" />
        </template>
      </el-table-column>

      <!-- Actions Column -->
      <el-table-column label="Actions">
        <template #default="{ row }">
          <!--review-->
          <Dialog v-model:visible="display" :breakpoints="{ '960px': '75vw' }" :modal="true" :style="{ width: '30vw' }" header="Review">
            <p class="leading-normal m-0">
              <BookCard :book="selectedBook" field="review" />
            </p>
            <template #footer>
              <Button label="close" @click="close" />
            </template>
          </Dialog>
          <Button  :disabled="!row.review || row.review.trim() === ''"
          icon="pi pi-envelope" raised rounded severity="info" text @click="open(row)" />
          <!--delete-->
          <ConfirmPopup></ConfirmPopup>
          <Button ref="popup" class="mr-2" icon="pi pi-times" raised rounded severity="danger" text @click="deleteBook(row, $event)"></Button>

        </template>
      </el-table-column>
    </el-table>
  </div>
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
import {onMounted, reactive, ref} from 'vue'
import BookCard from '@/components/BookCard.vue'
import type BookView from '@/entity/book/BookView'
import Paging from '@/entity/data/Paging'
import {container} from 'tsyringe'
import BookRepository from '@/repository/BookRepository'
import {ElForm, ElMessage} from 'element-plus'
import {useRouter} from 'vue-router'
import {useConfirm} from 'primevue/useconfirm';
import ConfirmPopup from "primevue/confirmpopup";

export default {
  name: 'ToReadList',
  components: { BookCard, ConfirmPopup },
  setup() {
    type StateType = {
      bookList: Paging<BookView>
    }
    const state = reactive<StateType>({
      bookList: new Paging<BookView>()
    })
    const router = useRouter()
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


    const handlePageChange = (newPage: number) => {
      if (newPage !== page.value) {
        page.value = newPage // 페이지 번호 업데이트
        getBookList(newPage) // 페이지 변경 시, reset 플래그를 true로 설정하여 새 데이터로 교체
      }
    }
    const  deleteBook= (book: BookView, event) => {
      let message = "책을 삭제하시겠습니까?";
      if (book.status === "COMPLETED" && book.isYearBook) {
        message = "이 책은 올해의 책으로 선정된 책입니다. 삭제 시 올해의 책에서도 제거됩니다. 삭제하시겠습니까?";
      }
      confirmPopup.require({
        target: event.target,
        message: message,
        icon: 'pi pi-exclamation-triangle',
        rejectProps: {
          label: 'Cancel',
          severity: 'secondary',
          outlined: true
        },
        acceptProps: {
          label: 'Yes'
        },
        accept: () => {
          BOOK_REPOSITORY.deleteBook(book.bookId).then(() => {
            ElMessage({ type: 'success', message: '성공적으로 삭제되었습니다!' })
            refetchList()  // 삭제 후 목록 새로고침(refetch)
          })
          },
        reject: () => {
          ElMessage({ type: 'info', message: '삭제가 취소되었습니다.' })

        }
      });
    }

    const breadcrumbHome = ref({ icon: 'pi pi-home', command: () => router.push('/') })
    const breadcrumbItems = ref([
      { label: '마이페이지', command: () => router.push('/myPage') },
      { label: '📘 완독 목록'}])


    const display = ref(false);
    const confirmPopup = useConfirm();
    const selectedBook = ref<BookView | null>(null); // selectedBook 타입 수정
    function open(book: BookView) {
      selectedBook.value = book; // 선택한 책을 저장
      display.value = true; // 다이얼로그 표시
    }

    function close() {
      display.value = false;
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
      formRef,
      breadcrumbHome,
      breadcrumbItems,
      confirmPopup,
      display,
      open,
      close,
      selectedBook,
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
