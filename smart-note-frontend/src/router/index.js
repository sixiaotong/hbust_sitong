import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/detail/:id',
    name: 'Detail',
    component: () => import('../views/DetailView.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue')
  },
  {
    path: '/create',
    name: 'Create',
    component: () => import('../views/CreateView.vue')
  },
  {
    path: '/discover',
    name: 'Discover',
    component: () => import('../views/DiscoverView.vue')
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('../views/MessagesView.vue')
  },
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: () => import('../views/UserProfileView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Login guard: redirect to /login if no token in localStorage
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.public) {
    // If already logged in and going to login page, redirect to home
    if (token && to.path === '/login') {
      next('/')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
