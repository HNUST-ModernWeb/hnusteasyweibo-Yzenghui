import request from './request'

export const getPostList = (page = 1, size = 10) =>
  request.get('/api/post/list', { params: { page, size } })

export const getPostDetail = (postId) =>
  request.get(`/api/post/detail/${postId}`)

export const createPost = (content) =>
  request.post('/api/post/create', { content })

export const deletePost = (postId) =>
  request.delete(`/api/post/delete/${postId}`)

export const likePost = (postId) =>
  request.post(`/api/post/like/${postId}`)

export const getUserPosts = (userId, page = 1, size = 10) =>
  request.get(`/api/post/user/${userId}`, { params: { page, size } })