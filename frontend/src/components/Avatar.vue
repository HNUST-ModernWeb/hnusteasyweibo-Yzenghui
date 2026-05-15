<template>
  <div 
    class="avatar" 
    :style="{ width: size + 'px', height: size + 'px' }"
  >
    <img 
      v-if="src" 
      :src="src" 
      :alt="alt"
      class="avatar-img"
      @error="handleError"
    />
    <div v-else class="avatar-placeholder">
      <span class="avatar-text">{{ placeholderText }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  alt: {
    type: String,
    default: 'avatar'
  },
  size: {
    type: Number,
    default: 40
  },
  placeholder: {
    type: String,
    default: ''
  }
})

const placeholderText = computed(() => {
  if (props.placeholder) {
    return props.placeholder.slice(0, 1)
  }
  return '?'
})

const handleError = (e) => {
  e.target.style.display = 'none'
}
</script>

<style scoped>
.avatar {
  border-radius: 50%;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  color: white;
  font-size: 16px;
  font-weight: 600;
}
</style>