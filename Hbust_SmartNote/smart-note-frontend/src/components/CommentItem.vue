<template>
  <div class="flex gap-3">
    <img
      :src="comment.avatar || defaultAvatar"
      alt="User"
      class="w-8 h-8 rounded-full"
      @error="e => e.target.src = defaultAvatar"
    />
    <div class="flex-1">
      <div class="flex justify-between mb-1">
        <span class="text-sm font-semibold text-gray-700">{{ comment.username }}</span>
        <span class="text-xs text-gray-400">{{ comment.timeText }}</span>
      </div>
      <p class="text-sm text-gray-600">{{ comment.content }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  comment: { type: Object, required: true }
})
const defaultAvatar = 'https://modao.cc/agent-py/media/generated_images/2026-04-02/e653f1adb59f4a08891211d24549cd6b.jpg'
</script>
