<template>
  <div class="profile">
    <div class="back-bar">
      <router-link to="/" class="back-btn">← 返回首页</router-link>
    </div>
    <div v-if="user" class="profile-header card">
      <div class="avatar-section">
        <Avatar 
          :src="user.avatar" 
          :size="100"
          :placeholder="user.username"
        />
      </div>
      <h2 class="username">{{ user.username }}</h2>
      <p class="join-time">注册时间：{{ formatDate(user.createTime) }}</p>
      <p v-if="isOwnProfile" class="own-tag">这是我的主页</p>
    </div>

    <div class="profile-content">
      <h3 class="posts-title">我的动态</h3>
      
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
        <p v-if="isOwnProfile">还没有发布动态</p>
        <p v-else>{{ user?.username }}还没有发布动态</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Avatar from '../components/Avatar.vue'
import PostItem from '../components/PostItem.vue'
import { getUserProfile } from '../api/user'
import { getUserPosts } from '../api/post'
import { useAuth } from '../composables/useAuth'

const route = useRoute()
const { state: authState } = useAuth()

const user = ref(null)
const posts = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)

const userId = computed(() => {
  return parseInt(route.params.userId)
})

const isOwnProfile = computed(() => {
  return authState.isLogin && authState.userInfo?.id === userId.value
})

const formatDate = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

const loadUser = async () => {
  try {
    const result = await getUserProfile(userId.value)
    if (result.code === 200) {
      user.value = result.data
    }
  } catch (e) {
    alert('加载用户信息失败')
  }
}

const loadPosts = async (pageNum = 1, append = false) => {
  loading.value = true
  try {
    const result = await getUserPosts(userId.value, pageNum)
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

const handlePostDeleted = (postId) => {
  posts.value = posts.value.filter(p => p.id !== postId)
}

onMounted(() => {
  loadUser()
  loadPosts(1)
})

watch(() => route.params.userId, () => {
  page.value = 1
  posts.value = []
  hasMore.value = true
  loadUser()
  loadPosts(1)
})
</script>

<style scoped>
.profile {
  padding-bottom: 20px;
}

.back-bar {
  margin-bottom: 15px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  padding: 8px 16px;
  background: #f5f5f5;
  color: #666;
  text-decoration: none;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #e8e8e8;
  color: #333;
}

.profile-header {
  text-align: center;
  padding: 30px;
  margin-bottom: 15px;
}

.avatar-section {
  margin-bottom: 15px;
}

.username {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.join-time {
  font-size: 14px;
  color: #999;
  margin: 0 0 10px 0;
}

.own-tag {
  display: inline-block;
  padding: 4px 12px;
  background: #e6162d;
  color: white;
  font-size: 12px;
  border-radius: 12px;
}

.profile-content {
  background: white;
  border-radius: 8px;
  padding: 15px;
}

.posts-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.post-list {
  margin-top: 10px;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #999;
}

.load-more-btn {
  width: 100%;
  padding: 15px;
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  color: #666;
  cursor: pointer;
  margin-top: 15px;
}

.load-more-btn:hover {
  background: #e8e8e8;
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