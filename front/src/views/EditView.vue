<script setup lang="ts">
import { onMounted, reactive, watch } from 'vue'
import PostEdit from '@/entity/post/PostEdit'
import { container } from 'tsyringe'
import PostRepository from '@/repository/PostRepository'
import { ElMessage } from 'element-plus'
import PostView from '@/entity/post/PostView'

// Define props to receive postId
const props = defineProps<{
  postId: number
}>()

type StateType = {
  post: PostView | null
  edit: PostEdit | null
}

const state = reactive<StateType>({
  post: null,
  edit: new PostEdit()
})

const POST_REPOSITORY = container.resolve(PostRepository)

// Fetch post data and initialize edit state
function getPost() {
  POST_REPOSITORY.get(props.postId, PostView)
    .then((post: PostView) => {
      state.post = post
      state.edit = new PostEdit({
        title: post.title,
        content: post.content
      })
    })
    .catch((e) => {
      console.error(e)
      ElMessage({ type: 'error', message: `${props.postId}번 글 조회 실패` })
    })
}

// Function to send edited post data to the server
function editPost() {
  if (!state.edit) return

  POST_REPOSITORY.edit(props.postId, state.edit)
    .then(() => {
      ElMessage({ type: 'success', message: '글 수정 완료' })
      location.href = `/posts/${props.postId}`
    })
    .catch(() => {
      ElMessage({ type: 'error', message: `글 수정 실패` })
    })
}

// On component mounted, fetch the post
onMounted(() => {
  getPost()
})

// Watch for changes in post content and update edit accordingly
watch(
  () => state.post?.title,
  (newTitle) => {
    if (state.edit) {
      state.edit.title = newTitle || ''
    }
  }
)

watch(
  () => state.post?.content,
  (newContent) => {
    if (state.edit) {
      state.edit.content = newContent || ''
    }
  }
)
</script>

<template>
  <el-row v-if="state.post && state.edit" class="edit-page">
    <el-col :span="24" class="edit-col">
      <el-card class="edit-card">
        <h2 class="edit-title">Edit Post</h2>
        <el-form :model="state.edit" ref="editForm">
          <el-form-item label="Title" class="form-item">
            <!-- Bind title to state.edit.title -->
            <el-input v-model="state.edit.title" placeholder="Enter title" />
          </el-form-item>
          <el-form-item label="Content" class="form-item">
            <!-- Bind content to state.edit.content -->
            <el-input
              v-model="state.edit.content"
              type="textarea"
              rows="10"
              placeholder="Enter content"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="editPost">Update Post</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>
  </el-row>
</template>

<style lang="scss" scoped>
.edit-page {
  padding: 20px;
  display: flex;
  justify-content: center; // Center the content horizontally
}

.edit-col {
  display: flex;
  justify-content: center; // Center the column content horizontally
}

.edit-card {
  width: 100%;
  max-width: 800px; // Set a maximum width for the card
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.edit-title {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.form-item {
  margin-bottom: 20px;
}

.el-button {
  margin-top: 20px;
}

@media (max-width: 600px) {
  .edit-card {
    padding: 15px;
  }

  .edit-title {
    font-size: 20px;
  }

  .form-item {
    margin-bottom: 15px;
  }

  .el-button {
    margin-top: 15px;
  }
}
</style>
