<template>
  <div class="home">
    <div class="post-create-area card">
      <textarea 
        v-model="newPostContent" 
        placeholder="分享你的想法..."
        class="post-input"
        maxlength="200"
        @input="handlePostInput"
      ></textarea>
      <span v-if="postError" class="error-tip">{{ postError }}</span>
      <div class="post-footer">
        <span class="char-count" :class="{ warning: newPostContent.length > 180 }">
          {{ newPostContent.length }}/200
        </span>
        <button 
          class="btn btn-primary" 
          @click="submitPost"
        >
          发送
        </button>
      </div>
    </div>

    <div class="post-list">
      <PostItem 
        v-for="post in posts" 
        :key="post.id" 
        :post="post"
        @deleted="handlePostDeleted"
      />
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <button 
      v-if="hasMore && !loading" 
      class="load-more-btn"
      @click="loadMore"
    >
      查看更多
    </button>

    <div v-if="posts.length === 0 && !loading" class="empty-state">
      <p>暂无动态</p>
      <p v-if="authState.isLogin">快来发布第一条动态吧！</p>
      <p v-else>登录后可以发布动态</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PostItem from '../components/PostItem.vue'
import { getPostList, createPost } from '../api/post'
import { useAuth } from '../composables/useAuth'

const { state: authState, showLoginDialog } = useAuth()

const newPostContent = ref('')
const posts = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)
const postError = ref('')

const loadPosts = async (pageNum = 1, append = false) => {
  loading.value = true
  try {
    const result = await getPostList(pageNum)
    if (result.code === 200) {
      const data = result.data.records || []
      if (append) {
        posts.value = [...posts.value, ...data]
      } else {
        posts.value = data
      }
      hasMore.value = data.length >= 10
    }
  } catch (e) {
    console.error('加载帖子失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  page.value++
  loadPosts(page.value, true)
}

const submitPost = async () => {
  postError.value = ''
  
  if (!authState.isLogin) {
    postError.value = '请先登录后再发布动态'
    showLoginDialog()
    setTimeout(() => {
      postError.value = ''
    }, 3000)
    return
  }
  
  if (!newPostContent.value.trim()) {
    postError.value = '动态内容不能为空'
    setTimeout(() => {
      postError.value = ''
    }, 2000)
    return
  }
  
  try {
    const result = await createPost(newPostContent.value.trim())
    if (result.code === 200) {
      newPostContent.value = ''
      page.value = 1
      await loadPosts(1)
    }
  } catch (e) {
    postError.value = '发布失败'
    setTimeout(() => {
      postError.value = ''
    }, 2000)
  }
}

const handlePostDeleted = (postId) => {
  posts.value = posts.value.filter(p => p.id !== postId)
}

onMounted(() => {
  loadPosts(1)
})
</script>

<style scoped>
.home {
  padding-bottom: 20px;
}

.post-create-area {
  margin-bottom: 15px;
}

.post-input {
  width: 100%;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 15px;
  resize: none;
  outline: none;
  min-height: 80px;
  background: #fafafa;
}

.post-input:focus {
  border-color: #e6162d;
  background: #fff;
}

.error-tip {
  display: block;
  color: #ff4d4f;
  font-size: 13px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #fff2f0;
  border-radius: 4px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.char-count {
  font-size: 13px;
  color: #999;
}

.char-count.warning {
  color: #e6162d;
}

.post-list {
  margin-top: 15px;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #999;
}

.load-more-btn {
  width: 100%;
  padding: 15px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 6px;
  color: #666;
  cursor: pointer;
  margin-top: 15px;
}

.load-more-btn:hover {
  background: #f5f5f5;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.empty-state p {
  margin: 5px 0;
}
</style>