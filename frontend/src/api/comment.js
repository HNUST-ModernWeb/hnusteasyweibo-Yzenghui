import request from './request'

export const getCommentList = (postId, page = 1, size = 10) =>
  request.get('/api/comment/list', { params: { postId, page, size } })

export const createComment = (postId, content) =>
  request.post('/api/comment/create', { postId, content })