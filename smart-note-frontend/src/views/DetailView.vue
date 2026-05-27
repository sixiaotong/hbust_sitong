<template>
  <div class="flex flex-col h-full bg-white">
    <ClockBar />
    <!-- Top Navigation -->
    <div class="flex items-center justify-between px-4 pt-0 pb-2 bg-white sticky top-0 z-10">
      <button class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors" @click="router.back()">
        <span class="text-2xl text-gray-800">&#8592;</span>
      </button>
      <div class="flex gap-2">
        <button class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors" @click="handleShare">
          <span class="text-2xl text-gray-800">&#8681;</span>
        </button>
        <button class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors" @click="showMenu = !showMenu">
          <span class="text-2xl text-gray-800">&#8942;</span>
        </button>
        <!-- Dropdown Menu -->
        <div v-if="showMenu" class="absolute top-12 right-4 bg-white rounded-xl shadow-lg border border-gray-100 py-1 z-20" @click="showMenu = false">
          <div class="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 cursor-pointer" @click="handleCopyLink">复制链接</div>
          <div v-if="currentUserId == note?.userId" class="px-4 py-2 text-sm text-red-500 hover:bg-gray-50 cursor-pointer" @click="handleDelete">删除</div>
          <div v-else class="px-4 py-2 text-sm text-red-500 hover:bg-gray-50 cursor-pointer" @click="handleReport">举报</div>
        </div>
      </div>
    </div>

    <!-- Scrollable Content -->
    <div class="flex-1 overflow-y-auto no-scrollbar" v-if="note">
      <!-- Cover Image -->
      <img
        v-if="note.coverImage"
        :src="note.coverImage"
        :alt="note.title"
        class="w-full aspect-[4/3] object-cover"
      />

      <div class="p-5">
        <!-- Author -->
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-3">
            <img
              :src="note.avatar || defaultAvatar"
              alt="Author"
              class="w-12 h-12 rounded-full ring-2 ring-blue-50"
            />
            <div>
              <h4 class="font-bold text-gray-900 leading-tight">{{ note.username }}</h4>
              <p class="text-xs text-gray-400">发布于 {{ note.createdAt }}</p>
            </div>
          </div>
          <button
            v-if="note && currentUserId != note.userId"
            class="px-5 py-1.5 text-sm font-medium rounded-full shadow-sm active:scale-95 transition-all"
            :class="followed ? 'bg-gray-200 text-gray-600' : 'bg-blue-600 text-white hover:bg-blue-700'"
            @click="toggleFollow"
          >
            {{ followed ? '已关注' : '关注' }}
          </button>
        </div>

        <!-- Note Body -->
        <article>
          <h1 class="text-2xl font-bold text-gray-900 mb-4 leading-snug">{{ note.title }}</h1>
          <div class="text-gray-700 leading-relaxed space-y-4 text-base" v-html="note.content"></div>
        </article>

        <!-- Tags -->
        <div class="flex flex-wrap gap-2 mt-8 mb-10">
          <span class="px-3 py-1 bg-gray-100 text-gray-500 text-xs rounded-full"># {{ note.category }}</span>
        </div>

        <!-- Stats -->
        <div class="flex items-center gap-4 py-4 border-t border-gray-50 text-gray-400 text-sm flex-wrap">
          <span class="flex items-center gap-1">&#128065; {{ note.viewCount }} 阅读</span>
          <span class="flex items-center gap-1">&#10084; {{ note.likeCount }} 点赞</span>
          <span class="flex items-center gap-1">&#11088; {{ note.favoriteCount || 0 }} 收藏</span>
          <span class="flex items-center gap-1">&#128172; {{ note.commentCount || comments.length }} 评论</span>
        </div>

        <!-- Comments -->
        <div class="mt-8">
          <h5 class="font-bold text-gray-900 mb-6">全部评论 ({{ comments.length }})</h5>
          <div class="space-y-6 pb-20">
            <CommentItem v-for="c in comments" :key="c.id" :comment="c" />
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="loadError" class="flex-1 flex items-center justify-center text-red-400 text-sm">{{ loadError }}</div>
    <div v-else class="flex-1 flex items-center justify-center text-gray-400">加载中...</div>

    <!-- Bottom Toolbar -->
    <div class="h-16 bg-white border-t border-gray-100 flex items-center px-4 gap-4 pb-2">
      <div class="flex-1 h-10 bg-gray-100 rounded-full flex items-center px-4 gap-2">
        <span class="text-gray-400">&#9998;</span>
        <input
          v-model="commentText"
          class="bg-transparent text-sm w-full focus:outline-none"
          placeholder="写下你的想法..."
          type="text"
          @keyup.enter="handleAddComment"
        />
      </div>
      <div class="flex gap-4 text-gray-600">
        <button class="flex flex-col items-center" @click="handleLike">
          <span class="text-2xl" :class="{ 'text-red-500': liked }">&#10084;</span>
          <span class="text-[10px]">{{ note?.likeCount || 0 }}</span>
        </button>
        <button class="flex flex-col items-center" @click="handleFavorite">
          <span class="text-2xl" :class="{ 'text-yellow-500': favorited }">&#9733;</span>
          <span class="text-[10px]">收藏</span>
        </button>
        <div class="flex flex-col items-center">
          <span class="text-2xl">&#128172;</span>
          <span class="text-[10px]">{{ comments.length }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNoteDetail, getComments, addComment, toggleLike, toggleFavorite, deleteNote } from '../api/note'
import { followUser, unfollowUser } from '../api/user'
import CommentItem from '../components/CommentItem.vue'
import ClockBar from '../components/ClockBar.vue'

const route = useRoute()
const router = useRouter()
const defaultAvatar = 'https://www.hbust.edu.cn/images/20/06/28/1lemhgc1vr/9.jpg'

const note = ref(null)
const loadError = ref('')
const comments = ref([])
const commentText = ref('')
const liked = ref(false)
const favorited = ref(false)
const followed = ref(false)
const showMenu = ref(false)
const currentUserId = ref(null)

onMounted(async () => {
  currentUserId.value = localStorage.getItem('userId')
  const id = route.params.id
  try {
    const res = await getNoteDetail(id)
    if (res.code === 200) {
      note.value = res.data
      liked.value = res.data.isLiked || false
      favorited.value = res.data.isFavorited || false
      followed.value = res.data.isFollowing || false
    } else {
      loadError.value = res.message || '加载失败'
    }
  } catch (e) {
    console.error('加载详情失败', e)
    loadError.value = '加载失败，请确认后端服务已启动（端口8080）'
  }
  try {
    const res = await getComments(id)
    if (res.code === 200) comments.value = res.data
  } catch (e) {
    console.error('加载评论失败', e)
  }
})

async function handleAddComment() {
  if (!commentText.value.trim()) return
  if (!currentUserId.value) {
    router.push('/login')
    return
  }
  try {
    const res = await addComment(route.params.id, commentText.value)
    if (res.code === 200) {
      comments.value.unshift({
        ...res.data,
        username: localStorage.getItem('username'),
        avatar: localStorage.getItem('avatar')
      })
      commentText.value = ''
    } else {
      alert(res.message)
    }
  } catch (e) {
    console.error('评论失败', e)
  }
}

async function handleLike() {
  if (!currentUserId.value) { router.push('/login'); return }
  try {
    const res = await toggleLike(route.params.id)
    if (res.code === 200) {
      liked.value = res.data.liked
      note.value.likeCount += liked.value ? 1 : -1
    }
  } catch (e) {
    console.error('点赞失败', e)
  }
}

async function handleFavorite() {
  if (!currentUserId.value) { router.push('/login'); return }
  try {
    const res = await toggleFavorite(route.params.id)
    if (res.code === 200) {
      favorited.value = res.data.favorited
    }
  } catch (e) {
    console.error('收藏失败', e)
  }
}

async function toggleFollow() {
  if (!currentUserId.value) { router.push('/login'); return }
  try {
    if (followed.value) {
      const res = await unfollowUser(note.value.userId)
      if (res.code === 200) {
        followed.value = false
        note.value.isFollowing = false
      }
    } else {
      const res = await followUser(note.value.userId)
      if (res.code === 200) {
        followed.value = true
        note.value.isFollowing = true
      }
    }
  } catch (e) {
    console.error('关注操作失败', e)
  }
}

function handleShare() {
  const url = window.location.href
  if (navigator.share) {
    navigator.share({ title: note.value?.title || '笔记', url })
  } else {
    navigator.clipboard.writeText(url).then(() => alert('链接已复制到剪贴板'))
  }
}

function handleCopyLink() {
  navigator.clipboard.writeText(window.location.href).then(() => alert('链接已复制'))
}

function handleReport() {
  alert('举报已提交，我们会尽快处理')
}

async function handleDelete() {
  showMenu.value = false
  if (!confirm('确定要删除这条笔记吗？删除后不可恢复。')) return
  try {
    const res = await deleteNote(route.params.id)
    if (res.code === 200) {
      router.back()
    } else {
      alert(res.message || '删除失败')
    }
  } catch (e) {
    console.error('删除失败', e)
    alert('删除失败，请稍后重试')
  }
}
</script>
