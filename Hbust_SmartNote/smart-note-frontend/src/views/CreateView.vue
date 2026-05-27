<template>
  <div class="flex flex-col h-full bg-white">
    <ClockBar />
    <!-- Top Navigation -->
    <div class="flex items-center justify-between px-4 pt-0 pb-2 bg-white border-b border-gray-100">
      <button class="w-10 h-10 flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors" @click="router.back()">
        <span class="text-2xl text-gray-800">&#8592;</span>
      </button>
      <span class="font-bold text-gray-800">发布笔记</span>
      <button
        class="px-4 py-1.5 bg-blue-600 text-white text-sm font-medium rounded-full hover:bg-blue-700 active:scale-95 transition-all disabled:opacity-50"
        :disabled="!form.title.trim() || submitting"
        @click="handleSubmit"
      >
        {{ submitting ? '发布中...' : '发布' }}
      </button>
    </div>

    <!-- Form -->
    <div class="flex-1 overflow-y-auto p-4 space-y-4 no-scrollbar">
      <input
        v-model="form.title"
        class="w-full text-lg font-bold text-gray-800 placeholder-gray-300 focus:outline-none"
        placeholder="请输入标题"
        type="text"
      />
      <textarea
        v-model="form.content"
        class="w-full min-h-[200px] text-sm text-gray-700 placeholder-gray-300 focus:outline-none resize-none leading-relaxed"
        placeholder="写下你的想法..."
      ></textarea>

      <div class="border-t pt-4 space-y-3">
        <div>
          <label class="block text-xs text-gray-500 mb-1">封面图片（可选）</label>
          <input ref="fileInput" type="file" accept="image/*" class="hidden" @change="handleFileChange" />
          <div class="flex gap-2">
            <button
              class="flex-1 h-10 bg-gray-100 border border-gray-200 rounded-lg text-sm text-gray-600 hover:bg-gray-200 transition-colors"
              :disabled="uploading"
              @click="$refs.fileInput.click()"
            >{{ uploading ? '上传中...' : '选择本地图片' }}</button>
          </div>
          <input
            v-model="form.coverImage"
            class="w-full h-10 bg-gray-50 border border-gray-200 rounded-lg px-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 mt-2"
            placeholder="或输入图片链接"
            type="text"
          />
        </div>
        <div>
          <label class="block text-xs text-gray-500 mb-1">分类</label>
          <div class="flex flex-wrap gap-2">
            <span
              v-for="cat in categories"
              :key="cat"
              class="px-3 py-1 rounded-full text-xs cursor-pointer border transition-colors"
              :class="form.category === cat ? 'bg-blue-600 text-white border-blue-600' : 'bg-gray-100 text-gray-500 border-gray-200 hover:bg-gray-200'"
              @click="form.category = cat"
            >{{ cat }}</span>
          </div>
        </div>
      </div>

      <!-- Preview cover image -->
      <div v-if="form.coverImage" class="border-t pt-4">
        <p class="text-xs text-gray-500 mb-2">封面预览</p>
        <img :src="form.coverImage" alt="Preview" class="w-full h-40 object-cover rounded-xl" @error="e => e.target.style.display = 'none'" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createNote, uploadFile } from '../api/note'
import ClockBar from '../components/ClockBar.vue'

const router = useRouter()
const submitting = ref(false)
const uploading = ref(false)
const fileInput = ref(null)

const categories = ['学习', '杂谈', '游戏', '美景', '趣事', '美食', '咨询求助']

const form = reactive({
  title: '',
  content: '',
  coverImage: '',
  category: '学习'
})

async function handleFileChange(e) {
  const file = e.target.files[0]
  if (!file) return
  uploading.value = true
  try {
    const res = await uploadFile(file)
    if (res.code === 200) {
      form.coverImage = res.data.url
    } else {
      alert(res.message || '上传失败')
    }
  } catch (err) {
    alert('图片上传失败，请重试')
  } finally {
    uploading.value = false
    if (fileInput.value) fileInput.value.value = ''
  }
}

async function handleSubmit() {
  if (!form.title.trim()) return
  submitting.value = true
  try {
    const res = await createNote({
      title: form.title.trim(),
      content: form.content.trim(),
      coverImage: form.coverImage.trim(),
      category: form.category
    })
    if (res.code === 200) {
      router.push('/')
    } else {
      alert(res.message)
    }
  } catch (e) {
    console.error('发布失败', e)
    alert('发布失败，请检查网络连接')
  } finally {
    submitting.value = false
  }
}
</script>
