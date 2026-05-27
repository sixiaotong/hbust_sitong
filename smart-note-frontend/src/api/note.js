import request from './request'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getNotes(params) {
  return request.get('/notes', { params })
}

export function getNoteDetail(id) {
  return request.get(`/notes/${id}`)
}

export function createNote(data) {
  return request.post('/notes', data)
}

export function updateNote(id, data) {
  return request.put(`/notes/${id}`, data)
}

export function deleteNote(id) {
  return request.delete(`/notes/${id}`)
}

export function getComments(noteId) {
  return request.get(`/notes/${noteId}/comments`)
}

export function addComment(noteId, content) {
  return request.post(`/notes/${noteId}/comments`, { content })
}

export function toggleLike(noteId) {
  return request.post(`/notes/${noteId}/like`)
}

export function toggleFavorite(noteId) {
  return request.post(`/notes/${noteId}/favorite`)
}
