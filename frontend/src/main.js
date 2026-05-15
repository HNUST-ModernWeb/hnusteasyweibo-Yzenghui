import { createApp } from 'vue'
import './styles/global.css'
import App from './App.vue'
import router from './router'
import mitt from 'mitt'

const app = createApp(App)
const emitter = mitt()

app.use(router)

app.config.globalProperties.emitter = emitter
window.emitter = emitter

app.mount('#app')