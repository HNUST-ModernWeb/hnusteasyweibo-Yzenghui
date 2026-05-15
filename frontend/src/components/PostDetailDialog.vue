<template>
  <Teleport to="body">
    <div v-if="visible" class="dialog-mask" @click.self="handleClose">
      <div class="dialog-content detail-dialog">
        <div class="dialog-header">
          <span class="dialog-title">动态详情</span>
          <button class="dialog-close" @click="handleClose">×</button>
        </div>
        <div v-if="errorMessage" class="error-tip">{{ errorMessage }}</div>

        <div v-if="post" class="dialog-body">
          <div class="post-detail-card">
            <div class="post-header">
              <div class="author-info">
                <Avatar 
                  :src="post.avatar" 
                  :size="56"
                  :placeholder="post.username"
                />
                <div class="author-detail">
                  <span class="username">{{ post.username }}</span>
                  <span class="time">{{ formatTime(post.createTime) }}</span>
                </div>
              </div>
            </div>
            <div class="post-full-content">{{ post.content }}</div>
            <div class="post-stats">
              <span class="stat-item">
                <span class="like-icon" :class="{ liked: post.liked }">♥</span>
                <span>{{ post.likeCount }}</span>
              </span>
              <span class="stat-item">
                <span>💬</span>
                <span>{{ post.commentCount }}</span>
              </span>
            </div>
          </div>

          <div class="comment-section">
            <h3 class="comment-title">评论 ({{ totalComments }})</h3>
            
            <div class="comment-list">
              <div 
                v-for="comment in comments" 
                :key="comment.id" 
                class="comment-item"
              >
                <Avatar 
                  :src="comment.avatar" 
                  :size="40"
                  :placeholder="comment.username"
                />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="comment-username">{{ comment.username }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <p>{{ comment.content }}</p>
                </div>
              </div>
            </div>

            <div v-if="loading" class="loading">加载中...</div>
            
            <button 
              v-if="hasMoreComments && !loading" 
              class="load-more-btn"
              @click="loadMoreComments"
            >
              查看更多评论
            </button>
            
            <div v-if="comments.length === 0 && !loading" class="no-comment">
              暂无评论
            </div>
          </div>

          <div class="comment-input-area">
            <textarea 
              v-model="commentContent" 
              placeholder="写下你的评论..."
              class="comment-input"
              maxlength="200"
              @input="handleCommentInput"
            ></textarea>
            <div class="input-footer">
              <span class="char-count" :class="{ warning: commentContent.length > 180 }">
                {{ commentContent.length }}/200
              </span>
              <button 
                class="btn btn-primary" 
                @click="submitComment"
              >
                发送
              </button>
            </div>
          </div>
        </div>

        <div v-else class="dialog-body loading">加载中...</div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, watch, onUnmounted } from 'vue'
import Avatar from './Avatar.vue'
import { getPostDetail } from '../api/post'
import { getCommentList, createComment } from '../api/comment'
import { useAuth } from '../composables/useAuth'

const props = defineProps({
  visible: Boolean,
  postId: Number
})

const emit = defineEmits(['close'])

const { state: authState, showLoginDialog } = useAuth()

const post = ref(null)
const comments = ref([])
const commentContent = ref('')
const loading = ref(false)
const commentPage = ref(1)
const hasMoreComments = ref(true)
const totalComments = ref(0)
const errorMessage = ref('')

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

const loadPost = async () => {
  if (!props.postId) return
  loading.value = true
  try {
    const result = await getPostDetail(props.postId)
    if (result.code === 200) {
      post.value = result.data
      totalComments.value = result.data.commentCount
    }
  } catch (e) {
    alert('加载帖子失败')
  } finally {
    loading.value = false
  }
}

const loadComments = async (page = 1, append = false) => {
  if (!props.postId) return
  loading.value = true
  try {
    const result = await getCommentList(props.postId, page)
    if (result.code === 200) {
      const data = result.data.records || []
      if (append) {
        comments.value = [...comments.value, ...data]
      } else {
        comments.value = data
      }
      hasMoreComments.value = data.length >= 10
    }
  } catch (e) {
    alert('加载评论失败')
  } finally {
    loading.value = false
  }
}

const loadMoreComments = () => {
  commentPage.value++
  loadComments(commentPage.value, true)
}

const submitComment = async () => {
  errorMessage.value = ''
  
  if (!authState.isLogin) {
    errorMessage.value = '请先登录后再发表评论'
    showLoginDialog()
    setTimeout(() => {
      errorMessage.value = ''
    }, 3000)
    return
  }
  
  if (!commentContent.value.trim()) {
    errorMessage.value = '评论内容不能为空'
    setTimeout(() => {
      errorMessage.value = ''
    }, 2000)
    return
  }
  
  try {
    const result = await createComment(props.postId, commentContent.value.trim())
    if (result.code === 200) {
      commentContent.value = ''
      errorMessage.value = '✓ 评论成功'
      setTimeout(() => {
        errorMessage.value = ''
      }, 2000)
      commentPage.value = 1
      await loadComments(1)
      post.value.commentCount++
      totalComments.value++
    }
  } catch (e) {
    errorMessage.value = '评论失败'
    setTimeout(() => {
      errorMessage.value = ''
    }, 2000)
  }
}

const handleClose = () => {
  emit('close')
}

watch(() => props.visible, (val) => {
  if (val) {
    post.value = null
    comments.value = []
    commentPage.value = 1
    hasMoreComments.value = true
    commentContent.value = ''
    loadPost()
    loadComments(1)
  }
})

onUnmounted(() => {
  post.value = null
  comments.value = []
})
</script>

<style scoped>
.detail-dialog {
  max-width: 700px;
  width: 92%;
  max-height: 88vh;
  overflow-y: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  border-bottom: 1px solid #eee;
  position: relative;
}

.dialog-title {
  font-size: 17px;
  font-weight: 600;
}

.dialog-close {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  padding: 0;
  line-height: 1;
  transition: all 0.2s ease;
  z-index: 10;
}

.dialog-close:hover {
  color: #666;
  transform: translateY(-50%) scale(1.1);
}

.error-tip {
  background: #fff2f0;
  color: #ff4d4f;
  padding: 12px 18px;
  margin: 15px 20px 0 20px;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
}

.dialog-body {
  padding: 24px 28px;
}

.post-detail-card {
  background: #fff;
  padding: 24px;
  margin: 0 0 20px 0;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.post-header {
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  gap: 14px;
}

.author-detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.username {
  font-size: 16px;
  font-weight: 600;
}

.time {
  font-size: 13px;
  color: #999;
}

.post-full-content {
  font-size: 15px;
  line-height: 1.9;
  color: #333;
  margin-bottom: 18px;
  word-wrap: break-word;
}

.post-stats {
  display: flex;
  gap: 24px;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
}

.like-icon {
  font-size: 16px;
  color: #999;
}

.like-icon.liked {
  color: #e6162d;
}

.comment-section {
  padding: 0;
  margin-bottom: 20px;
}

.comment-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  background: #f8f9fa;
  border-radius: 10px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.comment-username {
  font-size: 14px;
  font-weight: 600;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content p {
  font-size: 14px;
  color: #333;
  margin: 0;
}

.loading {
  text-align: center;
  color: #999;
  padding: 20px;
}

.load-more-btn {
  width: 100%;
  padding: 12px;
  background: #f5f5f5;
  border: none;
  border-radius: 6px;
  color: #666;
  cursor: pointer;
  margin-top: 10px;
}

.load-more-btn:hover {
  background: #e8e8e8;
}

.no-comment {
  text-align: center;
  color: #999;
  padding: 20px;
}

.comment-input-area {
  padding: 20px;
  border-top: 1px solid #eee;
  background: #fafafa;
}

.comment-input {
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  height: 90px;
  outline: none;
  transition: border-color 0.2s ease;
}

.comment-input:focus {
  border-color: #e6162d;
  background: #fff;
}

.input-footer {
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
</style>