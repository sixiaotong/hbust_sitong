<template>
  <div class="flex flex-col h-full bg-[#F8F9FA]">
    <ClockBar />
    <div class="flex-1 overflow-y-auto no-scrollbar">
      <h2 class="text-lg font-bold text-gray-800 px-4 pt-4 pb-2">消息通知</h2>
      <div v-if="loading" class="text-center text-gray-400 text-xs py-4">加载中...</div>
      <div v-else-if="notifications.length === 0" class="flex flex-col items-center justify-center py-20 text-gray-400">
        <span class="text-5xl mb-4">📭</span>
        <p class="text-sm">暂无消息</p>
        <p class="text-xs mt-1">你收到的赞、评论和关注会显示在这里</p>
      </div>
      <div v-else class="px-4 space-y-1 pb-20">
        <div v-for="item in notifications" :key="item.type + '-' + item.fromUserId + '-' + (item.noteId || '') + '-' + item.createdAt"
          class="bg-white rounded-xl p-4 shadow-sm border border-gray-100 flex items-center gap-3 active:scale-95 transition-transform cursor-pointer"
          @click="goToNote(item)">
          <div class="relative flex-shrink-0">
            <img :src="item.fromAvatar || defaultAvatar" alt="Avatar" class="w-10 h-10 rounded-full" @error="e => e.target.src = defaultAvatar" />
            <span class="absolute -bottom-1 -right-1 text-base">{{ typeIcon[item.type] }}</span>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm text-gray-700 truncate">
              <span class="font-semibold">{{ item.fromUsername }}</span> {{ typeAction[item.type] }}
              <span v-if="item.noteTitle" class="text-blue-500">「{{ item.noteTitle }}」</span>
            </p>
            <p v-if="item.content" class="text-xs text-gray-400 mt-0.5 truncate">{{ item.content }}</p>
            <p class="text-xs text-gray-400 mt-0.5">{{ item.timeText }}</p>
          </div>
          <div v-if="item.noteCover" class="w-12 h-12 rounded-lg overflow-hidden flex-shrink-0">
            <img :src="item.noteCover" alt="" class="w-full h-full object-cover" />
          </div>
        </div>
      </div>
    </div>
    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNotifications } from '../api/user'
import BottomNav from '../components/BottomNav.vue'
import ClockBar from '../components/ClockBar.vue'

const router = useRouter()
const defaultAvatar = 'https://www.hbust.edu.cn/images/20/06/28/1lemhgc1vr/9.jpg'
const notifications = ref([])
const loading = ref(false)

const typeIcon = { like: '❤️', comment: '💬', follow: '👤', favorite: '⭐' }
const typeAction = { like: '点赞了你的笔记', comment: '评论了你的笔记', follow: '关注了你', favorite: '收藏了你的笔记' }

onMounted(async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    router.push('/login')
    return
  }
  loading.value = true
  try {
    const res = await getNotifications()
    if (res.code === 200) {
      notifications.value = res.data
    }
  } catch (e) {
    console.error('加载消息失败', e)
  } finally {
    loading.value = false
  }
})

function goToNote(item) {
  if (item.noteId) {
    router.push(`/detail/${item.noteId}`)
  } else if (item.type === 'follow') {
    router.push(`/user/${item.fromUserId}`)
  }
}
</script>
