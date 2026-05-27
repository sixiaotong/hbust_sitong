<template>
  <div class="flex flex-col h-full bg-[#F8F9FA]">
    <ClockBar />
    <div class="flex-1 overflow-y-auto no-scrollbar px-4 py-4">
      <h2 class="text-lg font-bold text-gray-800 mb-4">发现精彩内容</h2>

      <!-- Category Grid -->
      <div class="grid grid-cols-3 gap-3 mb-6">
        <div
          v-for="cat in categories"
          :key="cat.name"
          class="bg-white rounded-xl p-4 shadow-sm border border-gray-100 text-center cursor-pointer active:scale-95 transition-transform"
          @click="goCategory(cat.name)"
        >
          <span class="text-2xl">{{ cat.icon }}</span>
          <p class="text-sm font-semibold text-gray-800 mt-1">{{ cat.name }}</p>
          <p class="text-xs text-gray-400 mt-0.5">热门话题</p>
        </div>
      </div>

      <!-- Hot Notes -->
      <h3 class="text-base font-bold text-gray-800 mb-3">热门笔记</h3>
      <div class="space-y-3">
        <NoteCard v-for="note in hotNotes" :key="note.id" :note="note" />
      </div>

      <div v-if="loading" class="text-center text-gray-400 text-xs py-4">加载中...</div>
      <div v-else-if="hotNotes.length === 0" class="text-center text-gray-400 text-sm py-10">暂无热门笔记</div>

      <div class="pb-20"></div>
    </div>
    <BottomNav />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getNotes } from '../api/note'
import NoteCard from '../components/NoteCard.vue'
import BottomNav from '../components/BottomNav.vue'
import ClockBar from '../components/ClockBar.vue'

const router = useRouter()

const categories = [
  { name: '学习', icon: '📚' },
  { name: '杂谈', icon: '💬' },
  { name: '游戏', icon: '🎮' },
  { name: '美景', icon: '🏞️' },
  { name: '趣事', icon: '😄' },
  { name: '美食', icon: '🍜' },
  { name: '咨询求助', icon: '❓' },
  { name: '全部', icon: '🔥' }
]

const hotNotes = ref([])
const loading = ref(false)

onMounted(() => {
  loadHotNotes()
})

async function loadHotNotes() {
  loading.value = true
  try {
    const res = await getNotes({ page: 1, size: 6, category: '全部' })
    if (res.code === 200) {
      hotNotes.value = res.data.records
    }
  } catch (e) {
    console.error('加载热门笔记失败', e)
  } finally {
    loading.value = false
  }
}

function goCategory(cat) {
  router.push({ path: '/', query: { category: cat } })
}
</script>
