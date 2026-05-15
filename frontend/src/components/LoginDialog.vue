<template>
  <Teleport to="body">
    <div v-if="visible" class="dialog-mask" @click.self="handleClose">
      <div class="dialog-content">
        <div class="dialog-header">
          <span class="dialog-title">{{ activeTab === 'login' ? '登录' : '注册' }}</span>
          <button class="dialog-close" @click="handleClose">×</button>
        </div>
        
        <div class="tab-header">
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'login' }"
            @click="activeTab = 'login'"
          >
            登录
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'register' }"
            @click="activeTab = 'register'"
          >
            注册
          </div>
        </div>

        <div class="dialog-body">
          <form v-if="activeTab === 'login'" @submit.prevent="handleSubmitLogin">
            <div class="form-group">
              <label>用户名</label>
              <input 
                v-model="loginForm.username" 
                type="text" 
                placeholder="请输入用户名"
                :class="{ error: loginErrors.username }"
              />
              <span v-if="loginErrors.username" class="error-message">{{ loginErrors.username }}</span>
            </div>
            <div class="form-group">
              <label>密码</label>
              <input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码"
                :class="{ error: loginErrors.password }"
              />
              <span v-if="loginErrors.password" class="error-message">{{ loginErrors.password }}</span>
            </div>
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? '登录中...' : '登录' }}
            </button>
            <span v-if="loginError" class="error-message text-center mt-20">{{ loginError }}</span>
            <span v-if="loginSuccess" class="success-message text-center mt-20">✓ 登录成功！</span>
          </form>

          <form v-else @submit.prevent="handleSubmitRegister">
            <div class="form-group">
              <label>头像</label>
              <div class="avatar-upload">
                <div class="avatar-preview-wrapper">
                  <img 
                    v-if="avatarPreview" 
                    :src="avatarPreview" 
                    class="avatar-preview-img"
                  />
                  <Avatar 
                    v-else
                    :size="80" 
                    :placeholder="registerForm.username"
                  />
                </div>
                <input 
                  ref="avatarInput"
                  type="file" 
                  accept="image/*" 
                  class="avatar-input"
                  @change="handleAvatarChange"
                />
                <span class="upload-text" @click="triggerAvatarUpload">点击上传</span>
              </div>
            </div>
            <div class="form-group">
              <label>用户名</label>
              <input 
                v-model="registerForm.username" 
                type="text" 
                placeholder="请输入用户名"
                :class="{ error: registerErrors.username }"
              />
              <span v-if="registerErrors.username" class="error-message">{{ registerErrors.username }}</span>
            </div>
            <div class="form-group">
              <label>密码</label>
              <input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码"
                :class="{ error: registerErrors.password }"
              />
              <span v-if="registerErrors.password" class="error-message">{{ registerErrors.password }}</span>
            </div>
            <div class="form-group">
              <label>确认密码</label>
              <input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码"
                :class="{ error: registerErrors.confirmPassword }"
              />
              <span v-if="registerErrors.confirmPassword" class="error-message">{{ registerErrors.confirmPassword }}</span>
            </div>
            <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? '注册中...' : '注册' }}
            </button>
            <span v-if="registerError" class="error-message text-center mt-20">{{ registerError }}</span>
            <span v-if="registerSuccess" class="success-message text-center mt-20">✓ 注册成功！</span>
          </form>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { useAuth } from '../composables/useAuth';
const props = defineProps({
 visible: Boolean
});
const emit = defineEmits(['close']);
const { handleLogin, handleRegister } = useAuth();
const activeTab = ref('login');
const isSubmitting = ref(false);
const loginError = ref('');
const registerError = ref('');
const loginSuccess = ref(false);
const registerSuccess = ref(false);
const avatarPreview = ref('');
const avatarInput = ref(null);
const loginForm = reactive({
 username: '',
 password: ''
});
const registerForm = reactive({
 username: '',
 password: '',
 confirmPassword: '',
 avatar: null
});
const loginErrors = reactive({
 username: '',
 password: ''
});
const registerErrors = reactive({
 username: '',
 password: '',
 confirmPassword: ''
});
const handleClose = () => {
 loginForm.username = '';
 loginForm.password = '';
 registerForm.username = '';
 registerForm.password = '';
 registerForm.confirmPassword = '';
 registerForm.avatar = null;
 avatarPreview.value = '';
 loginErrors.username = '';
 loginErrors.password = '';
 registerErrors.username = '';
 registerErrors.password = '';
 registerErrors.confirmPassword = '';
 loginError.value = '';
 registerError.value = '';
 loginSuccess.value = false;
 registerSuccess.value = false;
 emit('close');
};
const validateLogin = () => {
 loginErrors.username = '';
 loginErrors.password = '';
 loginError.value = '';
 loginSuccess.value = false;
 if (!loginForm.username.trim()) {
 loginErrors.username = '请输入用户名';
 return false;
 }
 if (!loginForm.password) {
 loginErrors.password = '请输入密码';
 return false;
 }
 return true;
};
const validateRegister = () => {
 registerErrors.username = '';
 registerErrors.password = '';
 registerErrors.confirmPassword = '';
 registerError.value = '';
 registerSuccess.value = false;
 if (!registerForm.username.trim()) {
 registerErrors.username = '请输入用户名';
 return false;
 }
 if (registerForm.username.length < 3) {
 registerErrors.username = '用户名至少3个字符';
 return false;
 }
 if (!registerForm.password) {
 registerErrors.password = '请输入密码';
 return false;
 }
 if (registerForm.password.length < 3) {
 registerErrors.password = '密码至少3个字符';
 return false;
 }
 if (!registerForm.confirmPassword) {
 registerErrors.confirmPassword = '请确认密码';
 return false;
 }
 if (registerForm.password !== registerForm.confirmPassword) {
 registerErrors.confirmPassword = '两次密码不一致';
 return false;
 }
 return true;
};
const handleSubmitLogin = async () => {
 if (!validateLogin())
 return;
 loginError.value = '';
 loginSuccess.value = false;
 isSubmitting.value = true;
 try {
 const result = await handleLogin({ ...loginForm });
 if (result === true) {
 loginSuccess.value = true;
 setTimeout(() => {
 loginSuccess.value = false;
 handleClose();
 }, 1500);
 }
 }
 catch (e) {
 loginError.value = e.message || '登录失败';
 }
 finally {
 isSubmitting.value = false;
 }
};
const handleSubmitRegister = async () => {
 if (!validateRegister())
 return;
 registerError.value = '';
 registerSuccess.value = false;
 isSubmitting.value = true;
 try {
 const result = await handleRegister({ ...registerForm });
 if (result === true) {
 registerSuccess.value = true;
 setTimeout(() => {
 registerSuccess.value = false;
 handleClose();
 }, 1500);
 }
 }
 catch (e) {
 registerError.value = e.message || '注册失败';
 }
 finally {
 isSubmitting.value = false;
 }
};
const triggerAvatarUpload = () => {
 avatarInput.value?.click();
};
const handleAvatarChange = (e) => {
 const file = e.target.files[0];
 if (file) {
 registerForm.avatar = file;
 avatarPreview.value = URL.createObjectURL(file);
 }
};
watch(() => props.visible, (val) => {
 if (val) {
 loginError.value = '';
 registerError.value = '';
 loginSuccess.value = false;
 registerSuccess.value = false;
 }
});
</script>

<style scoped>
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.dialog-title {
  font-size: 16px;
  font-weight: 600;
}

.dialog-close {
  border: none;
  background: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.dialog-close:hover {
  color: #666;
}

.dialog-body {
  padding: 20px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar-preview-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-input {
  display: none;
}

.upload-text {
  color: #e6162d;
  font-size: 14px;
  cursor: pointer;
  text-decoration: underline;
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 5px;
}

.success-message {
  color: #52c41a;
  font-size: 12px;
  margin-top: 5px;
}

.text-center {
  text-align: center;
}

.mt-20 {
  margin-top: 20px;
}
</style>