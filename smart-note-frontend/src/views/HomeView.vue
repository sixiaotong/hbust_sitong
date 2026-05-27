<template>
  <div class="flex flex-col h-full bg-[#F8F9FA] relative">
    <ClockBar />

    <!-- Search Bar -->
    <div class="px-3 py-2 bg-white sticky top-0 z-10 shadow-sm">
      <div class="relative w-full h-[40px] mx-auto flex items-center gap-2">
        <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-xl">&#128269;</span>
        <input
          v-model="keyword"
          class="w-full h-full bg-gray-100 rounded-full pl-10 pr-4 text-sm focus:outline-none focus:ring-1 focus:ring-blue-400"
          placeholder="搜索笔记内容、标签或日期"
          type="text"
          @input="onSearch"
        />
        <button
          class="flex-shrink-0 w-9 h-9 bg-blue-600 text-white rounded-full flex items-center justify-center text-sm hover:bg-blue-700 active:scale-95 transition-all"
          @click="shuffleNotes"
          title="随机刷新"
        >&#8635;</button>
      </div>
      <!-- Categories -->
      <div class="flex gap-4 overflow-x-auto no-scrollbar mt-3 pb-1 px-1" style="-webkit-overflow-scrolling: touch; scroll-behavior: smooth;">
        <span
          v-for="cat in categories"
          :key="cat"
          class="text-sm whitespace-nowrap pb-1 px-1 cursor-pointer transition-colors flex-shrink-0"
          :class="activeCategory === cat ? 'text-blue-600 font-bold border-b-2 border-blue-600' : 'text-gray-500'"
          @click="onCategoryChange(cat)"
        >{{ cat }}</span>
      </div>
    </div>

    <!-- Note List -->
    <div class="flex-1 overflow-y-auto px-4 py-4 space-y-4 no-scrollbar" @scroll="onScroll">
      <NoteCard v-for="note in notes" :key="note.id" :note="note" />

      <div v-if="errorMsg" class="text-center text-red-400 text-sm py-8">{{ errorMsg }}</div>
      <div v-else-if="notes.length === 0 && !loading" class="flex flex-col items-center justify-center py-16 text-gray-400">
        <span class="text-4xl mb-3">📝</span>
        <p class="text-sm">还没有笔记，去发布第一篇吧</p>
      </div>
      <div v-if="loading" class="text-center text-gray-400 text-xs py-4">加载中...</div>
      <div v-else-if="!hasMore && notes.length > 0" class="h-20 flex items-center justify-center text-gray-400 text-xs italic">
        - 没有更多内容了 -
      </div>
    </div>

    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNotes } from '../api/note'
import NoteCard from '../components/NoteCard.vue'
import BottomNav from '../components/BottomNav.vue'
import ClockBar from '../components/ClockBar.vue'

const route = useRoute()
const router = useRouter()

const categories = ['全部', '学习', '杂谈', '游戏', '美景', '趣事', '美食', '咨询求助']
const activeCategory = ref('全部')
const keyword = ref('')
const notes = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)
const errorMsg = ref('')

let searchTimer = null

onMounted(() => {
  if (route.query.category) {
    activeCategory.value = route.query.category
  }
  loadNotes()
})

function onCategoryChange(cat) {
  activeCategory.value = cat
  page.value = 1
  notes.value = []
  hasMore.value = true
  loadNotes()
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    page.value = 1
    notes.value = []
    hasMore.value = true
    loadNotes()
  }, 300)
}

async function loadNotes() {
  if (loading.value || !hasMore.value) return
  errorMsg.value = ''
  loading.value = true
  try {
    const res = await getNotes({
      page: page.value,
      size: 10,
      keyword: keyword.value || undefined,
      category: activeCategory.value
    })
    if (res.code === 200) {
      const data = res.data
      notes.value = page.value === 1 ? data.records : [...notes.value, ...data.records]
      hasMore.value = data.current < data.pages
      page.value++
    }
  } catch (e) {
    console.error('加载笔记失败', e)
    errorMsg.value = '加载失败，请确认后端服务已启动（端口8080）'
  } finally {
    loading.value = false
  }
}

function shuffleNotes() {
  const arr = notes.value
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j], arr[i]]
  }
}

function onScroll(e) {
  const { scrollTop, scrollHeight, clientHeight } = e.target
  if (scrollHeight - scrollTop - clientHeight < 50) {
    loadNotes()
  }
}
</script>
