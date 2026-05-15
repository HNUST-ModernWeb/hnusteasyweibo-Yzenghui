<template>
  <div class="post-item card">
    <div class="post-header">
      <div 
        class="author-info"
        @click="goToProfile"
      >
        <Avatar 
          :src="post.avatar" 
          :size="48"
          :placeholder="post.username"
        />
        <div class="author-detail">
          <span class="username">{{ post.username }}</span>
          <span class="time">{{ formatTime(post.createTime) }}</span>
        </div>
      </div>
      <button 
        v-if="isOwner" 
        class="delete-btn"
        @click="handleDelete"
      >
        删除
      </button>
    </div>
    
    <div class="post-content">{{ post.content }}</div>
    
    <div class="post-actions">
      <button class="action-btn" @click="handleLike">
        <span class="like-icon" :class="{ liked: post.liked }">♥</span>
        <span>{{ post.likeCount }}</span>
      </button>
      <button class="action-btn" @click="handleViewDetail">
        <span>💬</span>
        <span>{{ post.commentCount }}</span>
      </button>
      <button class="action-btn view-detail" @click="handleViewDetail">
        查看详情
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import Avatar from './Avatar.vue'
import { useAuth } from '../composables/useAuth'
import { likePost, deletePost } from '../api/post'

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['deleted'])

const { state: authState, showLoginDialog } = useAuth()

const isOwner = computed(() => {
  return authState.isLogin && authState.userInfo?.id === props.post.userId
})

const goToProfile = () => {
  const router = useRouter()
  router.push(`/profile/${props.post.userId}`)
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const handleLike = async () => {
  if (!authState.isLogin) {
    alert('请先登录后再点赞')
    showLoginDialog()
    return
  }
  
  const oldLiked = props.post.liked
  const oldCount = props.post.likeCount
  
  props.post.liked = !oldLiked
  props.post.likeCount = oldLiked ? oldCount - 1 : oldCount + 1
  
  try {
    await likePost(props.post.id)
  } catch (e) {
    props.post.liked = oldLiked
    props.post.likeCount = oldCount
    alert('点赞失败')
  }
}

const handleViewDetail = () => {
  if (!authState.isLogin) {
    alert('请先登录后再查看详情')
    showLoginDialog()
    return
  }
  window.emitter.emit('showPostDetail', props.post.id)
}

const handleDelete = async () => {
  if (!confirm('确定要删除这条动态吗？')) return
  
  try {
    await deletePost(props.post.id)
    emit('deleted', props.post.id)
    alert('删除成功')
  } catch (e) {
    alert('删除失败')
  }
}
</script>

<style scoped>
.post-item {
  margin-bottom: 12px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.author-info {
  display: flex;
  gap: 10px;
  cursor: pointer;
  flex: 1;
}

.author-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.delete-btn {
  padding: 4px 10px;
  font-size: 12px;
  color: #ff4d4f;
  background: none;
  border: 1px solid #ff4d4f;
  border-radius: 4px;
  cursor: pointer;
}

.delete-btn:hover {
  background: #fff2f0;
}

.post-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 12px;
}

.post-actions {
  display: flex;
  gap: 20px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
  background: none;
  border: none;
  cursor: pointer;
}

.action-btn:hover {
  color: #e6162d;
}

.like-icon {
  font-size: 16px;
  color: #999;
}

.like-icon.liked {
  color: #e6162d;
}

.view-detail {
  margin-left: auto;
  color: #1890ff;
}

.view-detail:hover {
  color: #096dd9;
}
</style>