<template>
  <div>
    <Breadcrumb :home="breadcrumbHome" :model="breadcrumbItems" />
  </div>
  <div>

    <span class="totalCount"> 전체 개수: {{ totalCount }}</span>

    <div class="post-container">
      <el-timeline v-if="Object.keys(groupedPosts).length">
        <el-timeline-item
          v-for="(posts, date) in groupedPosts"
          :key="date"
          :timestamp="date"
          placement="top"
        >
          <el-card v-for="post in posts" :key="post.postId" @click.native="detail(post.postId)">
            <div>
              <h4>{{ post.title }}</h4>
              <p class="card-date">{{ post.getDisplayRegDate() }}</p>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <!-- 로딩 인디케이터 -->
      <div v-if="loading" class="loading">
        <p>Loading...</p>
      </div>

      <!-- 데이터가 없을 때 표시 -->
      <div v-if="!loading && !Object.keys(groupedPosts).length" class="no-data">
        <p>No posts to display</p>
      </div>

      <!-- 페이지네이션 -->
      <el-pagination
        v-if="!loading && totalCount > pageSize"
        background
        layout="prev, pager, next"
        :total="totalCount"
        :page-size="pageSize"
        :current-page="page"
        @current-change="handlePageChange"
        class="center-pagination"
      />
    </div>
  </div>
</template>

<script lang="ts">
import {onMounted, ref} from 'vue'
import {container} from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import Paging from '@/entity/data/Paging'
import type PostView from '@/entity/data/PostView'
import {DateTimeFormatter} from '@js-joda/core'
import {useRouter} from 'vue-router'

export default {
  setup() {
    const router = useRouter()
    const POST_REPOSITORY = container.resolve(PostRepository)
    const paging = ref(new Paging<PostView>())
    const loading = ref(false)
    const page = ref(1)
    const pageSize = 5
    const totalCount = ref(0)
    const groupedPosts = ref<Record<string, PostView[]>>({})

    const fetchList = async (pageNumber: number) => {
      loading.value = true
      try {
        const response = await POST_REPOSITORY.getListByUser(pageNumber, pageSize)
        const { items, totalCount: responseTotalCount } = response
        totalCount.value = responseTotalCount

        if (items.length) {
          paging.value.setItems(items)

          // 날짜별로 그룹화
          const postsGroupedByDate: Record<string, PostView[]> = {}
          items.forEach((post: PostView) => {
            const date = post.regDate.format(DateTimeFormatter.ofPattern('yyyy-MM-dd')) // 포스트 생성 날짜 포맷
            if (!postsGroupedByDate[date]) {
              postsGroupedByDate[date] = []
            }
            postsGroupedByDate[date].push(post)
          })
          groupedPosts.value = postsGroupedByDate
        } else {
          groupedPosts.value = {}
        }
      } catch (error) {
        console.error('Error fetching fetchList:', error)
      } finally {
        loading.value = false
      }
    }

    const handlePageChange = (newPage: number) => {
      page.value = newPage
      fetchList(page.value)
    }

    // PostView로 이동하는 detail 메소드
    const detail = (postId: number) => {
      router.push({ name: 'post', params: { postId } }) // postId를 params로 넘겨서 이동
    }

    const breadcrumbHome = ref({ icon: 'pi pi-home', command: () => router.push('/') })
    const breadcrumbItems = ref([
      { label: '마이페이지', command: () => router.push('/myPage') },
      { label: '📥 내 작성글 목록'}])

      onMounted(() => {
      fetchList(page.value)
    })
    return {
      paging,
      loading,
      totalCount,
      groupedPosts,
      page,
      pageSize,
      handlePageChange,
      detail,
      breadcrumbHome,
      breadcrumbItems
  }
  }
}
</script>

<style scoped>
.post-container {
  height: auto; /* 스크롤 없애고 자동 높이 설정 */
  padding-bottom: 20px;
}

.loading {
  text-align: center;
  padding: 16px;
}

.no-data {
  text-align: center;
  padding: 16px;
  color: #888;
}

.center-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
