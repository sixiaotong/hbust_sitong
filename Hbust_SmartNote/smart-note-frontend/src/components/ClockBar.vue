<template>
  <div class="flex justify-between items-center px-6 pt-4 pb-2 text-xs text-gray-500 bg-white">
    <span>{{ now }}</span>
    <div class="flex gap-1 text-gray-400">
      <span>&#9632;</span><span>&#9632;</span><span>&#9632;</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const now = ref('')

let timer = null

function updateTime() {
  const d = new Date()
  const h = String(d.getHours()).padStart(2, '0')
  const m = String(d.getMinutes()).padStart(2, '0')
  now.value = h + ':' + m
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>
