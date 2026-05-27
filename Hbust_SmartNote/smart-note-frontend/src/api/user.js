import request from './request'

export function getUserProfile(userId) {
  return request.get(`/users/${userId}/profile`)
}

export function followUser(userId) {
  return request.post(`/users/${userId}/follow`)
}

export function unfollowUser(userId) {
  return request.post(`/users/${userId}/unfollow`)
}

export function getUserNotes(userId, params) {
  return request.get(`/users/${userId}/notes`, { params })
}

export function getUserFavorites(userId, params) {
  return request.get(`/users/${userId}/favorites`, { params })
}

export function updateProfile(data) {
  return request.put('/users/profile', data)
}

export function getFollowers(userId, params) {
  return request.get(`/users/${userId}/followers`, { params })
}

export function getFollowing(userId, params) {
  return request.get(`/users/${userId}/following`, { params })
}

export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/users/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getNotifications() {
  return request.get('/notifications')
}
