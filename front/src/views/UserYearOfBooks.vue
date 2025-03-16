<template>
  <div>
    <Breadcrumb :home="breadcrumbHome" :model="breadcrumbItems" />
  </div>
  <div class="year-of-books">
    <section class="intro-section">
      <h2>📚 {{ state.user.name }}님의 올해의 책</h2>
      <p>한 해 동안 읽은 책 중, 특별히 선정한 책들을 모아둔 공간입니다.</p>

      <template v-if="state.bookList.items.length > 0">
        <p>최대 <strong>10권</strong> 중 <strong>{{ state.bookList.items.length }}</strong>권이 선정되었습니다.</p>
      </template>
      <template v-else>
        <p>아직 올해의 책이 선정되지 않았어요.</p>
        <p v-if="state.isMyPage">마이페이지에서 올해의 책을 등록해 보세요!</p>
      </template>
      <br/>
      <el-date-picker
          v-model="year"
          class="year-picker"
          placeholder="Select Year"
          type="year"
          @update:modelValue="handleYearChange"
      />
    </section>

    <!-- 책 리스트 -->
    <div v-if="state.bookList.getCount() > 0" class="book-list">
      <ul class="book-container">
        <li
          v-for="(book, index) in state.bookList.items"
          :key="book.bookId"
          :style="getBookStyle(index)"
          class="book-item"
          @click="getBookDetail(book.bookId)"
        >
          <img :src="getBookImage(index)" alt="Book image" class="book-image" />
        </li>
      </ul>
    </div>

    <!-- 데이터 없음 -->
    <div v-else-if="!loading && state.bookList.getCount() === 0" class="no-data-message">
     아직 선정된 올해의 책이 없습니다.
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading-indicator">Loading...</div>


  </div>
  <!-- BookView 다이얼로그 -->
  <el-dialog
    v-model="dialogVisible"
    :title="state.bookView.title"
    class="book-dialog"
    width="500px"
    @close="handleClose"
  >
    <div v-if="state.bookView" class="book-dialog-content">
      <!-- 다이얼로그 배경을 책 이미지로 설정 -->
      <div class="custom-dialog-background">
        <div class="dialog-text-content">
          <p class="author-date">
            <strong>Author:</strong> {{ state.bookView.author }}<br />
            <strong>Date:</strong> {{ state.bookView.startDate }} ~ {{ state.bookView.endDate }}
          </p>
          <p class="review"><strong>Review:</strong> {{ state.bookView.review }}</p>
        </div>
      </div>

      <!-- 다이얼로그 하단 버튼 -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">닫기</el-button>
      </span>
    </div>
  </el-dialog>
</template>

<script lang="ts">
import {onBeforeMount, onMounted, reactive, ref} from 'vue'
import BookCard from '@/components/BookCard.vue'
import BookView from '@/entity/book/BookView'
import BookUI from '@/entity/book/BookUI'
import List from '@/entity/data/List'
import { container } from 'tsyringe'
import BookRepository from '@/repository/BookRepository'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import UserProfile from "@/entity/user/UserProfile";
import UserRepository from "@/repository/UserRepository";
import ProfileRepository from "@/repository/ProfileRepository";

export default {
  name: 'ToReadList',
  components: { BookCard },
  setup() {
    const route = useRoute()
    const userId = route.params.userId
    type StateType = {
      bookList: List<BookUI>
      bookView: BookView,
      user: UserProfile
    }
    const state = reactive<StateType>({
      bookList: new List<BookUI>(),
      bookView: new BookView(),
      user: new UserProfile()
    })
    const year = ref(new Date())
    const router = useRouter()
    const loading = ref(false)
    const dialogVisible = ref(false) // 다이얼로그 상태
    const detailBook = '/images/books/detailbook.png' // 첫 번째 이미지 경로 (절대 경로로 수정)

    const BOOK_REPOSITORY = container.resolve(BookRepository)
    const USER_REPOSITORY = container.resolve(UserRepository)
    const PROFILE_REPOSITORY = container.resolve(ProfileRepository)

// 연도별 책 리스트 가져오기
    const handleYearChange = async (newYear) => {
      if (!newYear) {
        console.error('Invalid year:', newYear)
        return
      }
      year.value = newYear // Date 객체로 업데이트
      await getBookList() // 서버에 년도만 전달
    }

    const getBookList = async (): Promise<List<BookUI>> => {
      if (loading.value) return // 이미 로딩 중이라면 무시
      loading.value = true
      try {
        const bookViewList: List<BookView> = await BOOK_REPOSITORY.getUsersBooksOfYear(
          year.value.getFullYear(), userId
        )
        if (bookViewList && bookViewList.items) {
          // 서버에서 데이터 가져오기
          // 기존 리스트를 새로 갱신 (addItem 대신)
          state.bookList.items = bookViewList.items.map((bookView) => new BookUI(bookView))
          loadImagesFromStorage()
        } else throw new Error('Book list is empty or invalid.')
      } catch (error) {
        console.error('Error fetching book list:', error)
        state.bookList = new List<BookUI>() // 빈 리스트로 초기화
        return Promise.reject(error)
      } finally {
        loading.value = false
      }
    }
    /**
     * 책 get, delete
     * @param bookId
     */

    const getBookDetail = async (bookId: number) => {
      try {
        state.bookView = await BOOK_REPOSITORY.getBook(bookId)
        dialogVisible.value = true
        await handleBookClick(state.bookView)
        console.log('Dialog visible state:', dialogVisible.value) // 다이얼로그 상태 출력
      } catch (e) {
        ElMessage({ type: 'error', message: `${bookId}번 책 조회 실패` })
        console.log(e)
      }

    }
    const handleBookClick = async (book: BookView) => {
      console.log('Book clicked:', book) // 클릭된 책 정보 확인
    }

    const handleClose = () => {
      console.log('다이얼로그가 닫혔습니다')
    }

    /**
     * 책 리스트 이미지들을 랜덤으로 출력
     * @param index
     */
    // 랜덤 이미지 반환
    const getRandomBookImageFromFallback = (): string => {
      const bookImages = [
        '/images/books/book1.png',
        '/images/books/book2.png',
        '/images/books/book3.png',
        '/images/books/book4.png',
        '/images/books/book5.png',
        '/images/books/book6.png',
        '/images/books/book7.png',
        '/images/books/book8.png',
        '/images/books/book9.png',
        '/images/books/book10.png',
        '/images/books/book11.png'
      ]
      // index를 사용하지 않고 랜덤으로 선택
      const randomIndex = Math.floor(Math.random() * bookImages.length)
      return bookImages[randomIndex]
    }
    // 로컬스토리지에서 책 이미지 로드
    const loadImagesFromStorage = () => {
      const storedImages = localStorage.getItem('bookImages')
      if (!storedImages) {
        // 로컬스토리지에 책 이미지가 없으면 랜덤한 이미지를 생성
        const images = state.bookList.items.map((bookUI, index) => getRandomBookImage(index))
        localStorage.setItem('bookImages', JSON.stringify(images))
      }

      // 각 책에 대한 이미지를 로컬 스토리지에서 불러오거나 랜덤 이미지를 할당
      state.bookList.items.forEach((bookUI, index) => {
        const savedImage = localStorage.getItem(`book-image-${index}`)
        if (!savedImage) {
          const randomImage = getRandomBookImage(index)
          localStorage.setItem(`book-image-${index}`, randomImage)
          bookUI.image = randomImage // BookUI 객체에 이미지 설정
        } else bookUI.image = savedImage
      })
    }

    // 책 별 이미지 가져오기
    const getRandomBookImage = (index: number): string => {
      const storedImages = JSON.parse(localStorage.getItem('bookImages') || '[]')
      if (storedImages.length > 0) {
        return storedImages[index] || getRandomBookImageFromFallback()
      } else {
        return getRandomBookImageFromFallback()
      }
    }
    //저장된 이미지 URL을 localStorage에서 가져와서 적용
    const getBookImage = (index: number) => {
      const savedImage = localStorage.getItem(`book-image-${index}`)
      return savedImage || getRandomBookImage(index)
    }

    /**
     * 책 이미지 랜덤 변경
     */
    const changeBookImages = async () => {
      // 기존 로컬스토리지 데이터 정리
      const totalBooks = state.bookList.items.length
      for (let i = 0; i < totalBooks; i++) {
        localStorage.removeItem(`book-image-${i}`)
      }
      localStorage.removeItem('bookImages')

      // 1. 랜덤한 책 이미지 목록 생성
      const newImages = state.bookList.items.map((bookUI, index) => getRandomBookImage(index))

      // 2. 로컬스토리지에 저장
      newImages.forEach((image, index) => {
        localStorage.setItem(`book-image-${index}`, image)
      })
      localStorage.setItem('bookImages', JSON.stringify(newImages))
      await getBookList()
    }
    const breadcrumbHome = ref({ icon: 'pi pi-home', command: () => router.push('/') })
    const breadcrumbItems = ref([
      { label: '올해의 책'}])

    onMounted(() => {
      getBookList()
    })
    onBeforeMount(async () => {
      USER_REPOSITORY.getUserProfile(userId)
          .then((profile) => {
            PROFILE_REPOSITORY.setProfile(profile)
            state.user = profile
          })
          .catch(() => {
            state.user = null
          })
    })
    return {
      state,
      year,
      loading,
      getBookList,
      getRandomBookImage,
      getBookStyle,
      getBookImage,
      changeBookImages,
      handleYearChange,
      getBookDetail,
      handleClose,
      dialogVisible,
      detailBook,
      breadcrumbHome,
      breadcrumbItems
    }
    function getBookStyle(index: number) {
      const angle = (Math.random() - 0.5) * 10
      const offset = index * 48
      const zIndex = state.bookList.items.length - index
      return {
        transform: `translateY(${offset}px) rotate(${angle}deg)`,
        zIndex: zIndex,
        transition: 'transform 0.3s ease-in-out'
      }
    }
  }
}
</script>

<style scoped>
.year-of-books {
  padding: 20px;
}
.book-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  margin-bottom: 10px; /* 버튼과 겹치지 않도록 여백 조정 */
  height: calc(100vh - 400px); /* date-picker와 겹치지 않도록 높이 조정 */
  z-index: 1; /* DOM에서 렌더링된 위치가 date-picker과 겹치지 않도록 수정 */
}
/* 책 세부정보 */
.book-dialog-content {
  padding: 0;
}

.custom-dialog-background {
  width: 100%;
  height: 300px; /* 높이를 직접 지정하여 이미지의 높이를 키움 */
  background-image: url('/images/books/detailbook.png'); /* 배경 이미지를 다이얼로그 창으로 설정 */
  background-size: cover;
  background-position: center;
  padding: 20px; /* 배경 이미지 위에 텍스트를 배치할 공간을 확보 */
  border-radius: 15px; /* 모서리를 둥글게 설정 */
}

.book-details {
  width: 100%;
  height: 200px; /* 이미지 높이 */
  background-size: cover;
  background-position: center;
  margin-bottom: 20px;
  border-radius: 10px; /* 이미지 영역의 테두리 모서리를 둥글게 설정 */
}

/*책 쌓는 이미지*/
.book-item {
  position: absolute;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 5px;
}
.book-image {
  width: 300px;
  height: auto;
  margin-bottom: 10px;
}
.book-item img {
  transition: transform 0.3s ease-in-out;
}

.book-item:hover img {
  transform: scale(1.1);
}
.no-data-message {
  text-align: center;
  margin-top: 20px;
  color: #888;
}
.loading-indicator {
  text-align: center;
  top: 100%; /* 화면 중앙에서 수직 정렬 */
}

/* 년도별 북리스트 출력 */
.year-picker {
  width: 200px; /* 원하는 너비로 설정 */
  z-index: 9999;
}

</style>
