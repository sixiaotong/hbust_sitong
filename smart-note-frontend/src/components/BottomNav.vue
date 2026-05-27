<template>
  <div class="h-[60px] bg-white border-t border-gray-100 flex items-center justify-around px-2 pb-2">
    <router-link to="/" class="flex flex-col items-center no-underline" :class="route.path === '/' ? 'text-blue-600' : 'text-gray-400'">
      <span class="text-2xl">&#127968;</span>
      <span class="text-[10px]">首页</span>
    </router-link>
    <router-link to="/discover" class="flex flex-col items-center no-underline" :class="route.path === '/discover' ? 'text-blue-600' : 'text-gray-400'">
      <span class="text-2xl">&#128269;</span>
      <span class="text-[10px]">发现</span>
    </router-link>
    <router-link to="/create" class="flex flex-col items-center no-underline text-gray-400">
      <span class="w-10 h-10 bg-blue-600 text-white rounded-full flex items-center justify-center text-xl shadow-lg hover:bg-blue-700 active:scale-95 transition-all">+</span>
      <span class="text-[10px] mt-0.5">发布</span>
    </router-link>
    <router-link to="/messages" class="flex flex-col items-center no-underline" :class="route.path === '/messages' ? 'text-blue-600' : 'text-gray-400'">
      <span class="text-2xl">&#128276;</span>
      <span class="text-[10px]">消息</span>
    </router-link>
    <router-link to="/profile" class="flex flex-col items-center no-underline" :class="route.path === '/profile' ? 'text-blue-600' : 'text-gray-400'">
      <span class="text-2xl">&#128100;</span>
      <span class="text-[10px]">我的</span>
    </router-link>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
const route = useRoute()
</script>
