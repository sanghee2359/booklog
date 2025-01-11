<template>
  <div class="to-read-list">
    <!-- 연도 -->
    <div class="year-selector">
      <label for="year">Year:</label>
      <select id="year" v-model="year" @change="getBookList">
        <option v-for="y in availableYears" :key="y" :value="y" :disabled="!yearAvailability[y]">
          {{ y }}
        </option>
      </select>
    </div>

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
  </div>
</template>

<script lang="ts">
import { onMounted, reactive, ref } from 'vue'
import BookCard from '@/components/BookCard.vue'
import BookView from '@/entity/book/BookView'
import List from '@/entity/data/List'
import { container } from 'tsyringe'
import BookRepository from '@/repository/BookRepository'

export default {
  name: 'ToReadList',
  components: { BookCard },
  setup() {
    type StateType = {
      bookList: List<BookView>
    }
    const state = reactive<StateType>({
      bookList: new List<BookView>()
    })
    const year = ref(new Date().getFullYear())
    const availableYears: number[] = [2023, 2024, 2025]
    const yearAvailability = reactive<{ [key: number]: boolean }>({})
    const loading = ref(false)
    const BOOK_REPOSITORY = container.resolve(BookRepository)

    // 연도별 책 리스트 가져오기
    const getBookList = async (): Promise<List<BookView>> => {
      if (loading.value) return
      loading.value = true
      try {
        // 서버에서 데이터 가져오기
        state.bookList = await BOOK_REPOSITORY.getBooksOfYear(year.value) // 상태 업데이트
        loadImagesFromStorage()
      } catch (error) {
        console.error('Error fetching book list:', error)
        state.bookList = new List<BookView>() // 빈 리스트로 초기화
        return Promise.reject(error)
      } finally {
        loading.value = false
      }
    }

    // 연도별 선택 가능 여부 확인
    const checkYears = async (): Promise<void> => {
      for (const y of availableYears) {
        try {
          const response = await BOOK_REPOSITORY.getBooksOfYear(y)
          yearAvailability[y] = response && response.getCount() > 0
        } catch (error) {
          console.error(`Error checking books for year ${y}:`, error)
          yearAvailability[y] = false
        }
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
    const getRandomBookImageFromFallback = (index: number): string => {
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
        '/images/books/book10.png'
      ]
      // index를 사용하지 않고 랜덤으로 선택
      const randomIndex = Math.floor(Math.random() * bookImages.length)
      return bookImages[randomIndex]
    }
    // 로컬스토리지에서 책 이미지 로드
    const loadImagesFromStorage = () => {
      const storedImages = localStorage.getItem('bookImages')
      if (!storedImages) {
        // 로컬스토리지에 책 이미지가 없으면 랜덤한 이미지를 생성하여 저장
        const images = state.bookList.items.map((_, index) => getRandomBookImage(index))
        localStorage.setItem('bookImages', JSON.stringify(images))
      }

      // 각 책에 대한 이미지를 로컬 스토리지에서 불러오거나 랜덤 이미지를 할당
      state.bookList.items.forEach((_, index) => {
        const savedImage = localStorage.getItem(`book-image-${index}`)
        if (!savedImage) {
          const randomImage = getRandomBookImage(index)
          localStorage.setItem(`book-image-${index}`, randomImage)
        }
      })
    }

    // 책 별 이미지 가져오기
    const getRandomBookImage = (index: number): string => {
      const storedImages = JSON.parse(localStorage.getItem('bookImages') || '[]')
      if (storedImages.length > 0) {
        return storedImages[index] || getRandomBookImageFromFallback(index)
      } else {
        return getRandomBookImageFromFallback(index)
      }
    }

    // 페이지 로드시 저장된 이미지 URL을 localStorage에서 가져와서 적용
    const getBookImage = (index: number) => {
      const savedImage = localStorage.getItem(`book-image-${index}`)
      return savedImage || getRandomBookImage(index)
    }
    onMounted(() => {
      checkYears()
      getBookList()
    })

    return {
      state,
      year,
      loading,
      availableYears,
      yearAvailability,
      getBookList,
      deleteBook,
      getRandomBookImage,
      getBookStyle,
      getBookImage // getBookImage 함수로 로컬스토리지에서 이미지를 가져옴
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
  transform: translateY(-100px); /* 위로 이동 */
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
.year-selector {
  margin-bottom: 20px;
}
</style>
