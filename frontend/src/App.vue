<template>
  <div class="app">
    <header class="header">
      <div class="container header-content">
        <router-link to="/" class="logo">微博客</router-link>
        <div class="header-right">
          <template v-if="authState.isLogin">
            <router-link 
              :to="`/profile/${authState.userInfo?.id}`" 
              class="user-info"
            >
              <Avatar 
                :src="authState.userInfo?.avatar" 
                :size="32"
                :placeholder="authState.userInfo?.username"
              />
              <span class="username">{{ authState.userInfo?.username }}</span>
            </router-link>
            <button class="btn btn-sm btn-secondary" @click="handleLogout">退出</button>
          </template>
          <template v-else>
            <button class="btn btn-primary" @click="showLoginDialog">登录</button>
          </template>
        </div>
      </div>
    </header>

    <main class="main">
      <div class="container">
        <router-view />
      </div>
    </main>

    <LoginDialog 
      :visible="loginDialogVisible"
      @close="hideLoginDialog"
    />

    <PostDetailDialog 
      :visible="postDetailVisible"
      :post-id="currentPostId"
      @close="hidePostDetail"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import LoginDialog from './components/LoginDialog.vue'
import PostDetailDialog from './components/PostDetailDialog.vue'
import Avatar from './components/Avatar.vue'
import { useAuth } from './composables/useAuth'

const router = useRouter()

const {
  state: authState,
  loginDialogVisible,
  showLoginDialog,
  hideLoginDialog,
  handleLogin,
  handleRegister,
  handleLogout,
  checkLogin
} = useAuth()

const postDetailVisible = ref(false)
const currentPostId = ref(null)

const showPostDetail = (postId) => {
  currentPostId.value = postId
  postDetailVisible.value = true
}

const hidePostDetail = () => {
  postDetailVisible.value = false
  currentPostId.value = null
}

const handleLoginSuccess = async (form) => {
  try {
    await handleLogin(form)
    router.push('/')
  } catch (e) {
    throw e
  }
}

const handleRegisterSuccess = async (form) => {
  try {
    await handleRegister(form)
    router.push('/')
  } catch (e) {
    throw e
  }
}

onMounted(() => {
  checkLogin()
  
  window.addEventListener('loginRequired', () => {
    showLoginDialog()
  })
  
  window.emitter.on('showPostDetail', showPostDetail)
})

onUnmounted(() => {
  window.emitter.off('showPostDetail', showPostDetail)
})
</script>

<style>
.header {
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #e6162d;
  text-decoration: none;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.username {
  font-size: 14px;
  color: #333;
}

.main {
  padding: 20px 0;
}

.router-link-exact-active {
  color: #e6162d;
}
</style>