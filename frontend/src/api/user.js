import request from './request'

export const login = (data) => {
  return request.post('/api/user/login', data)
}

export const register = (formData) => {
  return request.post('/api/user/register', formData)
}

export const getUserInfo = () => {
  return request.get('/api/user/info')
}

export const logout = () => {
  return request.post('/api/user/logout')
}

export const getUserProfile = (userId) =>
  request.get(`/api/user/profile/${userId}`)