<template>
  <div class="flex flex-col h-full bg-[#F8F9FA]">
    <div class="flex items-center justify-between px-4 py-2 bg-white border-b border-gray-100">
      <button class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100" @click="router.back()">
        <span class="text-xl">&#8592;</span>
      </button>
      <span class="font-bold text-gray-800 text-sm">用户主页</span>
      <div class="w-10"></div>
    </div>

    <div class="flex-1 overflow-y-auto no-scrollbar">
      <div class="bg-white px-6 pt-6 pb-4 text-center">
        <img :src="profile.avatar || defaultAvatar" alt="Avatar" class="w-20 h-20 rounded-full mx-auto mb-3 object-cover border-2 border-blue-100"
          @error="e => e.target.src = defaultAvatar" />
        <h2 class="text-lg font-bold text-gray-800">{{ profile.username }}</h2>
        <p class="text-xs text-gray-500 mt-1">{{ profile.bio || 'ta还没想好签名~' }}</p>
        <button v-if="!isSelf" class="mt-3 px-6 py-1.5 text-sm font-medium rounded-full active:scale-95 transition-all"
          :class="profile.isFollowing ? 'bg-gray-200 text-gray-600' : 'bg-blue-600 text-white'"
          @click="toggleFollow">
          {{ profile.isFollowing ? '已关注' : '关注' }}
        </button>
      </div>

      <div class="flex justify-around bg-white mx-4 mt-3 rounded-xl py-4 shadow-sm border border-gray-100">
        <div class="text-center">
          <div class="text-lg font-bold text-gray-800">{{ profile.noteCount || 0 }}</div>
          <div class="text-xs text-gray-400">笔记</div>
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

      <div class="px-4 py-3 space-y-3">
        <NoteCard v-for="note in noteList" :key="note.id" :note="note" />
        <div v-if="loading" class="text-center text-gray-400 text-xs py-4">加载中...</div>
        <div v-else-if="noteList.length === 0" class="flex flex-col items-center justify-center py-16 text-gray-400">
          <span class="text-4xl mb-3">📝</span>
          <p class="text-sm">还没有发布过笔记</p>
        </div>
        <div v-else-if="!hasMore" class="text-center text-gray-400 text-xs py-4 pb-20">- 没有更多了 -</div>
      </div>
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
            <button v-if="u.userId != currentUserId" class="px-3 py-1 text-xs rounded-full"
              :class="u.isFollowing ? 'bg-gray-200 text-gray-500' : 'bg-blue-600 text-white'"
              @click.stop="toggleFollowUser(u)">{{ u.isFollowing ? '已关注' : '关注' }}</button>
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
import { useRoute, useRouter } from 'vue-router'
import { getUserProfile, getUserNotes, followUser, unfollowUser, getFollowers, getFollowing } from '../api/user'
import NoteCard from '../components/NoteCard.vue'
import BottomNav from '../components/BottomNav.vue'

const route = useRoute()
const router = useRouter()
const defaultAvatar = 'https://www.hbust.edu.cn/images/20/06/28/1lemhgc1vr/9.jpg'

const currentUserId = ref(null)
const isSelf = ref(false)
const profile = reactive({ username: '', avatar: '', bio: '', noteCount: 0, followerCount: 0, followingCount: 0, isFollowing: false })
const noteList = ref([])
const notePage = ref(1)
const hasMore = ref(true)
const loading = ref(false)

const followModal = reactive({ show: false, type: 'followers', list: [], page: 1, hasMore: true, loading: false })

onMounted(() => {
  currentUserId.value = localStorage.getItem('userId')
  const targetId = route.params.id
  if (currentUserId.value == targetId) {
    router.replace('/profile')
    return
  }
  isSelf.value = false
  loadProfile()
  loadNotes()
})

watch(() => route.params.id, (newId) => {
  if (currentUserId.value == newId) {
    router.replace('/profile')
    return
  }
  noteList.value = []
  notePage.value = 1
  hasMore.value = true
  loadProfile()
  loadNotes()
})

async function loadProfile() {
  try {
    const res = await getUserProfile(route.params.id)
    if (res.code === 200) Object.assign(profile, res.data)
  } catch (e) { console.error('加载资料失败', e) }
}

async function loadNotes() {
  if (loading.value || !hasMore.value) return
  loading.value = true
  try {
    const res = await getUserNotes(route.params.id, { page: notePage.value, size: 10 })
    if (res.code === 200) {
      noteList.value = notePage.value === 1 ? res.data.records : [...noteList.value, ...res.data.records]
      hasMore.value = res.data.current < res.data.pages
      notePage.value++
    }
  } catch (e) { console.error('加载笔记失败', e) }
  finally { loading.value = false }
}

async function toggleFollow() {
  try {
    if (profile.isFollowing) {
      const res = await unfollowUser(route.params.id)
      if (res.code === 200) { profile.isFollowing = false; profile.followerCount-- }
    } else {
      const res = await followUser(route.params.id)
      if (res.code === 200) { profile.isFollowing = true; profile.followerCount++ }
    }
  } catch (e) { console.error('操作失败', e) }
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
    const res = await api(route.params.id, { page: followModal.page, size: 20 })
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

function goToUser(userId) {
  followModal.show = false
  if (userId == currentUserId.value) {
    router.push('/profile')
  } else {
    router.push('/user/' + userId)
  }
}

async function toggleFollowUser(u) {
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
</script>
