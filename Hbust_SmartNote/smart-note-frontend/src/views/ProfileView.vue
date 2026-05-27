<template>
  <div class="flex flex-col h-full bg-[#F8F9FA]">
    <ClockBar />

    <!-- Profile Header -->
    <div class="bg-white px-6 pt-6 pb-4 text-center">
      <div class="relative inline-block cursor-pointer" @click="$refs.avatarInput.click()">
        <img
          :src="profile.avatar || defaultAvatar"
          alt="Avatar"
          class="w-20 h-20 rounded-full mx-auto mb-3 object-cover border-2 border-blue-100"
          @error="e => e.target.src = defaultAvatar"
        />
        <div class="absolute inset-0 w-20 h-20 rounded-full mx-auto bg-black/30 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
          <span class="text-white text-xs font-medium">更换头像</span>
        </div>
      </div>
      <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarChange" />
      <h2 class="text-lg font-bold text-gray-800">{{ profile.username }}</h2>
      <div class="flex items-center justify-center gap-1 mt-1">
        <p v-if="!editingBio" class="text-xs text-gray-500">{{ profile.bio || 'ta还没想好签名~' }}</p>
        <input
          v-else
          ref="bioInput"
          v-model="bioDraft"
          class="text-xs text-gray-700 bg-gray-100 rounded-full px-3 py-1 w-48 text-center focus:outline-none focus:ring-1 focus:ring-blue-400"
          placeholder="写一句个性签名..."
          maxlength="50"
          @keyup.enter="saveBio"
          @blur="saveBio"
        />
        <button
          class="w-5 h-5 flex items-center justify-center rounded-full hover:bg-gray-200 text-gray-400 hover:text-gray-600 transition-colors"
          @click="startEditBio"
        >
          <span class="text-xs">&#9998;</span>
        </button>
      </div>
    </div>

    <!-- Stats -->
    <div class="flex justify-around bg-white mx-4 mt-3 rounded-xl py-4 shadow-sm border border-gray-100">
      <div class="text-center cursor-pointer" @click="activeTab = 'notes'">
        <div class="text-lg font-bold text-gray-800">{{ profile.noteCount || 0 }}</div>
        <div class="text-xs text-gray-400" :class="{ 'text-blue-600 font-bold': activeTab === 'notes' }">笔记</div>
      </div>
      <div class="text-center cursor-pointer" @click="showFollowModal('followers')">
        <div class="text-lg font-bold text-gray-800">{{ profile.followerCount || 0 }}</div>
        <div class="text-xs text-gray-400">粉丝</div>
      </div>
      <div class="text-center cursor-pointer" @click="showFollowModal('following')">
        <div class="text-lg font-bold text-gray-800">{{ profile.followingCount || 0 }}</div>
        <div class="text-xs text-gray-400">关注</div>
      </div>
    </div>

    <!-- Logout -->
    <div class="mx-4 mt-3">
      <button
        class="w-full h-10 bg-white border border-red-200 text-red-500 text-sm font-medium rounded-xl hover:bg-red-50 active:scale-[0.98] transition-all"
        @click="handleLogout"
      >
        退出登录
      </button>
    </div>

    <!-- Tab Switcher -->
    <div class="flex bg-white mx-4 mt-3 rounded-xl shadow-sm border border-gray-100 overflow-hidden">
      <div
        class="flex-1 py-3 text-center text-sm cursor-pointer transition-colors"
        :class="activeTab === 'notes' ? 'text-blue-600 font-bold bg-blue-50' : 'text-gray-500'"
        @click="activeTab = 'notes'"
      >我的笔记</div>
      <div
        class="flex-1 py-3 text-center text-sm cursor-pointer transition-colors"
        :class="activeTab === 'favorites' ? 'text-blue-600 font-bold bg-blue-50' : 'text-gray-500'"
        @click="activeTab = 'favorites'"
      >我的收藏</div>
    </div>

    <!-- Content Area -->
    <div class="flex-1 overflow-y-auto px-4 py-3 space-y-3 no-scrollbar" @scroll="onScroll">
      <NoteCard v-for="note in noteList" :key="note.id" :note="note" />

      <div v-if="loading" class="text-center text-gray-400 text-xs py-4">加载中...</div>
      <div v-else-if="noteList.length === 0" class="flex flex-col items-center justify-center py-16 text-gray-400">
        <span class="text-4xl mb-3">{{ activeTab === 'notes' ? '📝' : '⭐' }}</span>
        <p class="text-sm">{{ activeTab === 'notes' ? '还没有发布过笔记' : '还没有收藏过笔记' }}</p>
      </div>
      <div v-else-if="!hasMore" class="text-center text-gray-400 text-xs py-4 pb-20">- 没有更多了 -</div>
    </div>

    <!-- Follow List Modal -->
    <div v-if="followModal.show" class="fixed inset-0 bg-black/50 z-50 flex items-end sm:items-center justify-center" @click.self="followModal.show = false">
      <div class="bg-white rounded-t-2xl sm:rounded-2xl w-full sm:w-[360px] max-h-[70vh] flex flex-col shadow-xl">
        <div class="flex items-center justify-between px-4 py-3 border-b border-gray-100">
          <span class="font-bold text-gray-800">{{ followModal.type === 'followers' ? '粉丝' : '关注' }}</span>
          <button class="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-100" @click="followModal.show = false">✕</button>
        </div>
        <div class="flex-1 overflow-y-auto no-scrollbar" @scroll="onFollowScroll">
          <div v-for="u in followModal.list" :key="u.userId" class="flex items-center gap-3 px-4 py-3 border-b border-gray-50 cursor-pointer hover:bg-gray-50" @click="goToUser(u.userId)">
            <img :src="u.avatar || defaultAvatar" class="w-10 h-10 rounded-full object-cover" @error="e => e.target.src = defaultAvatar" />
            <div class="flex-1 min-w-0">
              <div class="text-sm font-medium text-gray-800 truncate">{{ u.username }}</div>
              <div class="text-xs text-gray-400 truncate">{{ u.bio || 'ta还没想好签名~' }}</div>
            </div>
            <button v-if="u.userId != userId" class="px-3 py-1 text-xs rounded-full"
              :class="u.isFollowing ? 'bg-gray-200 text-gray-500' : 'bg-blue-600 text-white'"
              @click.stop="toggleFollowInModal(u)">{{ u.isFollowing ? '已关注' : '关注' }}</button>
          </div>
          <div v-if="followModal.loading" class="text-center text-gray-400 text-xs py-4">加载中...</div>
          <div v-else-if="!followModal.hasMore && followModal.list.length > 0" class="text-center text-gray-400 text-xs py-4">- 没有更多了 -</div>
          <div v-else-if="followModal.list.length === 0 && !followModal.loading" class="text-center text-gray-400 text-sm py-8">暂无数据</div>
        </div>
      </div>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getUserProfile, getUserNotes, getUserFavorites, getFollowers, getFollowing, followUser, unfollowUser, uploadAvatar, updateProfile } from '../api/user'
import { logout } from '../api/auth'
import NoteCard from '../components/NoteCard.vue'
import BottomNav from '../components/BottomNav.vue'
import ClockBar from '../components/ClockBar.vue'

const router = useRouter()
const defaultAvatar = 'https://www.hbust.edu.cn/images/20/06/28/1lemhgc1vr/9.jpg'

const userId = ref(null)
const profile = reactive({
  username: '',
  avatar: '',
  bio: '',
  noteCount: 0,
  followerCount: 0,
  followingCount: 0
})
const activeTab = ref('notes')
const noteList = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)
const editingBio = ref(false)
const bioDraft = ref('')
const bioInput = ref(null)

const followModal = reactive({ show: false, type: 'followers', list: [], page: 1, hasMore: true, loading: false })

onMounted(async () => {
  userId.value = localStorage.getItem('userId')
  if (!userId.value) {
    router.push('/login')
    return
  }
  profile.username = localStorage.getItem('username') || ''
  profile.avatar = localStorage.getItem('avatar') || ''
  await loadProfile()
  loadContent()
})

async function loadProfile() {
  try {
    const res = await getUserProfile(userId.value)
    if (res.code === 200) {
      Object.assign(profile, res.data)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('avatar', res.data.avatar || '')
    }
  } catch (e) {
    console.error('加载资料失败', e)
  }
}

watch(activeTab, () => {
  page.value = 1
  noteList.value = []
  hasMore.value = true
  loadContent()
})

async function loadContent() {
  if (loading.value || !hasMore.value) return
  loading.value = true
  try {
    const api = activeTab.value === 'notes' ? getUserNotes : getUserFavorites
    const res = await api(userId.value, { page: page.value, size: 10 })
    if (res.code === 200) {
      const data = res.data
      noteList.value = page.value === 1 ? data.records : [...noteList.value, ...data.records]
      hasMore.value = data.current < data.pages
      page.value++
    }
  } catch (e) {
    console.error('加载内容失败', e)
  } finally {
    loading.value = false
  }
}

function handleLogout() {
  logout()
  router.push('/login')
}

async function handleAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadAvatar(file)
    if (res.code === 200) {
      profile.avatar = res.data.url
      localStorage.setItem('avatar', res.data.url)
    } else {
      alert(res.message || '上传失败')
    }
  } catch (err) {
    alert('头像上传失败，请重试')
  }
}

function startEditBio() {
  bioDraft.value = profile.bio || ''
  editingBio.value = true
  setTimeout(() => {
    if (bioInput.value) bioInput.value.focus()
  }, 50)
}

async function saveBio() {
  if (!editingBio.value) return
  editingBio.value = false
  const newBio = bioDraft.value.trim()
  if (newBio === (profile.bio || '')) return
  try {
    const res = await updateProfile({ bio: newBio })
    if (res.code === 200) {
      profile.bio = newBio
    } else {
      alert(res.message || '保存失败')
    }
  } catch (e) {
    console.error('保存签名失败', e)
    alert('保存失败')
  }
}

function showFollowModal(type) {
  followModal.show = true
  followModal.type = type
  followModal.list = []
  followModal.page = 1
  followModal.hasMore = true
  followModal.loading = false
  loadFollowList()
}

async function loadFollowList() {
  if (followModal.loading || !followModal.hasMore) return
  followModal.loading = true
  try {
    const api = followModal.type === 'followers' ? getFollowers : getFollowing
    const res = await api(userId.value, { page: followModal.page, size: 20 })
    if (res.code === 200) {
      followModal.list = followModal.page === 1 ? res.data.records : [...followModal.list, ...res.data.records]
      followModal.hasMore = res.data.current < res.data.pages
      followModal.page++
    }
  } catch (e) { console.error('加载列表失败', e) }
  finally { followModal.loading = false }
}

function onFollowScroll(e) {
  const { scrollTop, scrollHeight, clientHeight } = e.target
  if (scrollHeight - scrollTop - clientHeight < 50) loadFollowList()
}

function goToUser(targetId) {
  followModal.show = false
  if (targetId == userId.value) {
    return
  }
  router.push('/user/' + targetId)
}

async function toggleFollowInModal(u) {
  try {
    if (u.isFollowing) {
      await unfollowUser(u.userId)
      u.isFollowing = false
    } else {
      await followUser(u.userId)
      u.isFollowing = true
    }
  } catch (e) { console.error('操作失败', e) }
}

function onScroll(e) {
  const { scrollTop, scrollHeight, clientHeight } = e.target
  if (scrollHeight - scrollTop - clientHeight < 50) {
    loadContent()
  }
}
</script>
