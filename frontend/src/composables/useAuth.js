import { ref, reactive } from 'vue'
import { login, register, getUserInfo, logout } from '../api/user'

const state = reactive({
  isLogin: false,
  userInfo: null
})

export function useAuth() {
  const loginDialogVisible = ref(false)

  const showLoginDialog = () => {
    loginDialogVisible.value = true
  }

  const hideLoginDialog = () => {
    loginDialogVisible.value = false
  }

  const handleLogin = async (form) => {
    try {
      const result = await login({
        username: form.username,
        password: form.password
      })
      if (result.code === 200) {
        state.isLogin = true
        state.userInfo = result.data
        return true
      }
      throw new Error(result.message || '登录失败')
    } catch (e) {
      const errorMessage = e.message || (e.response?.data?.message) || (e.data?.message) || '登录失败'
      throw new Error(errorMessage)
    }
  }

  const handleRegister = async (form) => {
    try {
      const formData = new FormData()
      formData.append('username', form.username)
      formData.append('password', form.password)
      formData.append('confirmPassword', form.confirmPassword)
      if (form.avatar) {
        formData.append('avatar', form.avatar)
      }
      const result = await register(formData)
      if (result.code === 200) {
        state.isLogin = true
        state.userInfo = result.data
        return true
      }
      throw new Error(result.message || '注册失败')
    } catch (e) {
      const errorMessage = e.message || (e.response?.data?.message) || (e.data?.message) || '注册失败'
      throw new Error(errorMessage)
    }
  }

  const handleLogout = async () => {
    await logout()
    state.isLogin = false
    state.userInfo = null
  }

  const checkLogin = async () => {
    try {
      const result = await getUserInfo()
      if (result.code === 200) {
        state.isLogin = true
        state.userInfo = result.data
      }
    } catch (e) {
      state.isLogin = false
      state.userInfo = null
    }
  }

  const requireAuth = (callback) => {
    if (state.isLogin) {
      callback()
    } else {
      showLoginDialog()
    }
  }

  return {
    state,
    loginDialogVisible,
    showLoginDialog,
    hideLoginDialog,
    handleLogin,
    handleRegister,
    handleLogout,
    checkLogin,
    requireAuth
  }
}