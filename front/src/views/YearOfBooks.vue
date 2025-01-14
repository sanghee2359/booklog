<template>
  <div class="to-read-list">
    <el-date-picker
      v-model="year"
      type="year"
      placeholder="Select Year"
      @update:modelValue="handleYearChange"
      class="year-picker"
    />

    <!-- 책 리스트 -->
    <div class="book-list" v-if="state.bookList.getCount() > 0">
      <ul class="book-container">
        <li
          v-for="(book, index) in state.bookList.items"
          :key="book.bookId"
          class="book-item"
          :style="getBookStyle(index)"
        >
          <img :src="getBookImage(index)" alt="Book image" class="book-image" />
        </li>
      </ul>
    </div>

    <!-- 데이터 없음 -->
    <div v-else-if="!loading && state.bookList.getCount() === 0" class="no-data-message">
      No books found for the selected year.
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading-indicator">Loading...</div>

    <!-- 이미지 변경 버튼 -->
    <el-button
      type="primary"
      icon="el-icon-picture-outline"
      @click="changeBookImages"
      class="custom-button"
      >Change Book Images
    </el-button>
  </div>
</template>

<script lang="ts">
import { onMounted, reactive, ref } from 'vue'
import BookCard from '@/components/BookCard.vue'
import BookView from '@/entity/book/BookView'
import BookUI from '@/entity/book/BookUI'
import List from '@/entity/data/List'
import { container } from 'tsyringe'
import BookRepository from '@/repository/BookRepository'

export default {
  name: 'ToReadList',
  components: { BookCard },
  setup() {
    type StateType = {
      bookList: List<BookUI>
    }
    const state = reactive<StateType>({
      bookList: new List<BookUI>()
    })
    const year = ref(new Date())

    const loading = ref(false)

    const BOOK_REPOSITORY = container.resolve(BookRepository)
    /**
     * 년도 데이터를 받았을 때
     * 해당 년도의 올해의 책을 출력하기
     * @param newYear
     */
    const handleYearChange = async (newYear) => {
      if (!newYear) {
        console.error('Invalid year:', newYear)
        return
      }
      year.value = newYear // year 값을 Date 객체로 업데이트

      console.log(year.value) // year가 Date 객체인지 확인

      await getBookList() // 서버에 년도만 전달
    }
    // 연도별 책 리스트 가져오기
    const getBookList = async (): Promise<List<BookUI>> => {
      if (loading.value) return // 이미 로딩 중이라면 무시
      loading.value = true
      try {
        const bookViewList: List<BookView> = await BOOK_REPOSITORY.getBooksOfYear(
          year.value.getFullYear()
        ) // 상태 업데이트

        // items가 정의되어 있는지 체크
        if (bookViewList && bookViewList.items) {
          // 서버에서 데이터 가져오기
          // 기존 리스트를 새로 갱신 (addItem 대신)
          state.bookList.items = bookViewList.items.map((bookView) => new BookUI(bookView))
          console.log(state.bookList)
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

    // 책 삭제
    const deleteBook = (bookId: number) => {
      state.bookList.items = state.bookList.items.filter((b) => b.bookId !== bookId)
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

    // 책 이미지 변경
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

    onMounted(() => {
      getBookList()
    })

    return {
      state,
      year,
      loading,
      getBookList,
      deleteBook,
      getRandomBookImage,
      getBookStyle,
      getBookImage,
      changeBookImages,
      handleYearChange
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
.to-read-list {
  padding: 20px;
}
.book-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  height: 100vh;
  z-index: 1; /* DOM에서 렌더링된 위치가 date-picker과 겹치지 않도록 수정 */
}
.book-item {
  position: absolute;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-bottom: 28px;
}

.book-image {
  width: 300px;
  height: auto;
  margin-bottom: 10px;
}

.book-details {
  font-size: 16px;
  text-align: center;
  color: #333;
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
  margin-top: 20px;
}
.year-picker {
  width: 200px; /* 원하는 너비로 설정 */
  z-index: 9999;
}
.custom-button {
  background-color: #f0f0f0; /* 버튼 배경색 */
  color: #333; /* 텍스트 색상 */
  font-size: 14px; /* 글꼴 크기 */
  border-radius: 4px; /* 둥근 모서리 */
  padding: 8px 16px; /* 버튼 안쪽 여백 */
  transition:
    background-color 0.3s ease,
    transform 0.3s ease; /* 애니메이션 */
}

.custom-button:hover {
  background-color: #e0e0e0; /* 호버 시 배경색 */
  transform: translateY(-2px); /* 버튼이 위로 살짝 떠오르는 효과 */
}

.custom-button:active {
  background-color: #d0d0d0; /* 클릭 시 배경색 */
  transform: translateY(0); /* 클릭 효과 */
}
</style>
