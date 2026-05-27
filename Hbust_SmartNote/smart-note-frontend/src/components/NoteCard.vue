<template>
  <router-link
    :to="`/detail/${note.id}`"
    class="block bg-white rounded-2xl overflow-hidden shadow-sm border border-gray-100 active:scale-95 transition-transform"
    :class="{ 'p-4': !note.coverImage }"
  >
    <img
      v-if="note.coverImage"
      :src="note.coverImage"
      :alt="note.title"
      class="w-full h-40 object-cover"
      @error="handleImgError"
    />
    <div :class="{ 'p-4': note.coverImage }">
      <div class="flex items-center gap-2 mb-2">
        <img
          :src="note.avatar || 'https://modao.cc/agent-py/media/generated_images/2026-04-02/dab6812f86484cee8fbcc5ded2f13f0d.jpg'"
          alt="Avatar"
          class="w-6 h-6 rounded-full border border-gray-100"
          @error="e => e.target.src = 'https://modao.cc/agent-py/media/generated_images/2026-04-02/dab6812f86484cee8fbcc5ded2f13f0d.jpg'"
        />
        <span class="text-xs text-gray-600 font-medium">{{ note.username }}</span>
        <span class="text-[10px] text-gray-400 ml-auto">{{ note.timeText || note.createdAt }}</span>
      </div>
      <h3 class="text-base font-bold text-gray-800 mb-1 leading-tight">{{ note.title }}</h3>
      <p class="text-sm text-gray-500 line-clamp-2" v-if="note.content">{{ note.content }}</p>
      <div class="flex items-center gap-3 mt-2 text-xs text-gray-400" v-if="note.likeCount !== undefined || note.commentCount !== undefined">
        <span v-if="note.likeCount !== undefined" class="flex items-center gap-1">&#10084; {{ note.likeCount }}</span>
        <span v-if="note.commentCount !== undefined" class="flex items-center gap-1">&#128172; {{ note.commentCount }}</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
defineProps({
  note: { type: Object, required: true }
})

function handleImgError(e) {
  e.target.style.display = 'none'
}
</script>
