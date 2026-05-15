import axios from 'axios'

const instance = axios.create({
  baseURL: '',
  withCredentials: true,
  timeout: 10000
})

instance.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code !== 200) {
      if (data.code === 401) {
        const event = new Event('loginRequired')
        window.dispatchEvent(event)
      }
      return Promise.reject(data)
    }
    return data
  },
  (error) => {
    if (error.response?.status === 401) {
      const event = new Event('loginRequired')
      window.dispatchEvent(event)
    }
    return Promise.reject(error)
  }
)

export default instance