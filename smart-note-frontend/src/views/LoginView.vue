<template>
  <div class="flex flex-col p-8 h-full bg-[#F5F5F5]">
    <div class="mb-12">
      <ClockBar />
    </div>

    <!-- Logo -->
    <div class="flex flex-col items-center mb-12">
      <div class="w-20 h-20 rounded-2xl shadow-lg mb-4 overflow-hidden">
        <img src="/img/hbust.jpg" alt="Logo" class="w-full h-full object-cover" />
      </div>
      <h1 class="text-2xl font-bold text-gray-800">湖科风光</h1>
      <p class="text-sm text-gray-500 mt-1">记录 2026 年的每一个灵感</p>
    </div>

    <!-- Login Form -->
    <div class="space-y-4">
      <div>
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-xl">&#9993;</span>
          <input
            v-model="form.email"
            class="w-full h-12 bg-white border rounded-xl pl-10 pr-4 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            :class="{
              'border-red-500': emailError,
              'border-gray-200': !emailError
            }"
            placeholder="请输入邮箱"
            type="email"
            @blur="validateEmail"
            @input="emailError = ''"
          />
        </div>
        <p v-if="emailError" class="text-red-500 text-xs mt-1 ml-2">{{ emailError }}</p>
      </div>
      
      <div>
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-xl">&#128274;</span>
          <input
            v-model="form.password"
            class="w-full h-12 bg-white border rounded-xl pl-10 pr-4 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
            :class="{
              'border-red-500': passwordError,
              'border-gray-200': !passwordError
            }"
            placeholder="请输入密码"
            type="password"
            @keyup.enter="handleLogin"
            @input="passwordError = ''"
          />
        </div>
        <p v-if="passwordError" class="text-red-500 text-xs mt-1 ml-2">{{ passwordError }}</p>
      </div>
    </div>

    <!-- Actions -->
    <div class="mt-8 space-y-4">
      <button
        class="flex items-center justify-center w-full h-12 bg-blue-600 text-white font-semibold rounded-xl shadow-md hover:bg-blue-700 active:scale-95 transition-all disabled:opacity-50"
        :disabled="loading || !isFormValid"
        @click="handleLogin"
      >
        {{ loading ? '登录中...' : '登录' }}
      </button>
      <div class="text-center">
        <a
          class="text-sm text-blue-600 underline decoration-blue-600 underline-offset-4 cursor-pointer hover:text-blue-700"
          @click="openRegisterModal"
        >
          还没有账号? 立即注册
        </a>
      </div>
    </div>

    <!-- Register Modal -->
    <div v-if="showRegister" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeRegisterModal">
      <div class="bg-white rounded-2xl p-6 w-[320px] shadow-xl">
        <h2 class="text-lg font-bold text-gray-800 mb-4 text-center">注册账号</h2>
        <div class="space-y-3">
          <div>
            <input 
              v-model="regForm.email" 
              class="w-full h-10 bg-gray-50 border rounded-lg px-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              :class="{
                'border-red-500': regEmailError,
                'border-gray-200': !regEmailError
              }"
              placeholder="邮箱（用于登录）" 
              type="email"
              @blur="validateRegEmail"
              @input="regEmailError = ''"
            />
            <p v-if="regEmailError" class="text-red-500 text-xs mt-1">{{ regEmailError }}</p>
          </div>
          
          <div>
            <input 
              v-model="regForm.username" 
              class="w-full h-10 bg-gray-50 border rounded-lg px-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              :class="{
                'border-red-500': usernameError,
                'border-gray-200': !usernameError
              }"
              placeholder="用户名（显示名称）"
              @blur="validateUsername"
              @input="usernameError = ''"
            />
            <p v-if="usernameError" class="text-red-500 text-xs mt-1">{{ usernameError }}</p>
          </div>
          
          <div>
            <input 
              v-model="regForm.password" 
              class="w-full h-10 bg-gray-50 border rounded-lg px-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              :class="{
                'border-red-500': regPasswordError,
                'border-gray-200': !regPasswordError
              }"
              placeholder="密码（至少6位）" 
              type="password" 
              @keyup.enter="handleRegister"
              @blur="validateRegPassword"
              @input="regPasswordError = ''"
            />
            <p v-if="regPasswordError" class="text-red-500 text-xs mt-1">{{ regPasswordError }}</p>
          </div>
          
          <p v-if="errorMsg" class="text-center text-red-500 text-xs">{{ errorMsg }}</p>
          <p v-if="successMsg" class="text-center text-green-500 text-xs">{{ successMsg }}</p>
          
          <div class="flex gap-2">
            <button
              class="flex-1 h-10 bg-gray-200 text-gray-700 font-semibold rounded-lg hover:bg-gray-300 transition-all"
              @click="closeRegisterModal"
            >
              取消
            </button>
            <button
              class="flex-1 h-10 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 active:scale-95 transition-all disabled:opacity-50"
              :disabled="regLoading || !isRegFormValid"
              @click="handleRegister"
            >
              {{ regLoading ? '注册中...' : '注册' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Toast 提示 -->
    <div v-if="toastMessage" class="fixed bottom-20 left-1/2 transform -translate-x-1/2 bg-gray-800 text-white px-4 py-2 rounded-lg shadow-lg text-sm z-50 animate-fade-in-out">
      {{ toastMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '../api/auth'
import ClockBar from '../components/ClockBar.vue'

const router = useRouter()

// 表单数据
const form = reactive({ email: '', password: '' })
const regForm = reactive({ email: '', username: '', password: '' })

// 错误状态
const errorMsg = ref('')
const successMsg = ref('')
const toastMessage = ref('')
const showRegister = ref(false)
const loading = ref(false)
const regLoading = ref(false)

// 实时校验错误
const emailError = ref('')
const passwordError = ref('')
const regEmailError = ref('')
const usernameError = ref('')
const regPasswordError = ref('')

// 邮箱格式验证函数
const validateEmailFormat = (email) => {
  const emailRegex = /^[^\s@]+@([^\s@]+\.)+[^\s@]+$/
  if (!email) return '请输入邮箱'
  if (!emailRegex.test(email)) return '请输入有效的邮箱地址（例如：114514@qq.com）'
  return ''
}

const validatePassword = (password) => {
  if (!password) return '请输入密码'
  if (password.length < 6) return '密码至少需要6位'
  return ''
}

const validateUsername = (username) => {
  if (!username) return '请输入用户名'
  if (username.length < 2) return '用户名至少需要2个字符'
  if (username.length > 20) return '用户名不能超过20个字符'
  if (!/^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(username)) return '用户名只能包含中文、字母、数字和下划线'
  return ''
}

const validateRegPassword = (password) => {
  if (!password) return '请输入密码'
  if (password.length < 6) return '密码至少需要6位'
  if (password.length > 20) return '密码不能超过20位'
  return ''
}

// 实时校验方法
const validateEmail = () => {
  emailError.value = validateEmailFormat(form.email)
}

const validateRegEmail = () => {
  regEmailError.value = validateEmailFormat(regForm.email)
}

// 计算表单是否有效
const isFormValid = computed(() => {
  return form.email.trim() && form.password.trim() && !emailError.value && !passwordError.value
})

const isRegFormValid = computed(() => {
  return regForm.email.trim() && regForm.username.trim() && regForm.password.trim() &&
         !regEmailError.value && !usernameError.value && !regPasswordError.value
})

// 显示提示消息
const showToast = (message, isError = true) => {
  if (isError) {
    errorMsg.value = message
  } else {
    successMsg.value = message
  }
  toastMessage.value = message
  setTimeout(() => {
    toastMessage.value = ''
    if (!isError) successMsg.value = ''
    setTimeout(() => {
      if (isError) errorMsg.value = ''
    }, 300)
  }, 3000)
}

async function handleLogin() {
  // 实时校验
  emailError.value = validateEmailFormat(form.email)
  passwordError.value = validatePassword(form.password)
  
  if (emailError.value || passwordError.value) {
    showToast(emailError.value || passwordError.value)
    return
  }
  
  loading.value = true
  try {
    const res = await login({ email: form.email.trim(), password: form.password })
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userId', res.data.userId)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('avatar', res.data.avatar || '')
      showToast('登录成功！', false)
      setTimeout(() => {
        router.push('/')
      }, 500)
    } else {
      showToast(res.message || '登录失败，请检查邮箱和密码')
    }
  } catch (e) {
    if (e.response && e.response.status >= 500) {
      showToast('服务器未启动，请先启动后端服务（端口8080）')
    } else if (e.response && e.response.status === 401) {
      showToast('邮箱或密码错误')
    } else {
      showToast('无法连接服务器，请确认后端已启动')
    }
  } finally {
    loading.value = false
  }
}

function openRegisterModal() {
  showRegister.value = true
  errorMsg.value = ''
  successMsg.value = ''
  // 重置注册表单和错误
  regForm.email = ''
  regForm.username = ''
  regForm.password = ''
  regEmailError.value = ''
  usernameError.value = ''
  regPasswordError.value = ''
}

function closeRegisterModal() {
  showRegister.value = false
  errorMsg.value = ''
  successMsg.value = ''
}

async function handleRegister() {
  // 实时校验
  regEmailError.value = validateEmailFormat(regForm.email)
  usernameError.value = validateUsername(regForm.username)
  regPasswordError.value = validateRegPassword(regForm.password)
  
  if (regEmailError.value || usernameError.value || regPasswordError.value) {
    showToast(regEmailError.value || usernameError.value || regPasswordError.value)
    return
  }
  
  regLoading.value = true
  try {
    const res = await register({
      email: regForm.email.trim(),
      username: regForm.username.trim(),
      password: regForm.password
    })
    if (res.code === 200) {
      closeRegisterModal()
      showToast('注册成功！请登录', false)
      // 自动填充登录表单
      form.email = regForm.email
      form.password = ''
    } else {
      showToast(res.message || '注册失败，请稍后重试')
    }
  } catch (e) {
    if (e.response && e.response.status >= 500) {
      showToast('服务器未启动，请先启动后端服务（端口8080）')
    } else if (e.response && e.response.status === 409) {
      showToast('该邮箱已被注册')
    } else {
      showToast('无法连接服务器，请确认后端已启动')
    }
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped>
@keyframes fade-in-out {
  0% {
    opacity: 0;
    transform: translateX(-50%) translateY(10px);
  }
  15% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  85% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-10px);
  }
}

.animate-fade-in-out {
  animation: fade-in-out 3s ease-in-out forwards;
}
</style>