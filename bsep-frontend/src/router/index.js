import { createRouter, createWebHistory } from 'vue-router'

function guardMyroute (_to, _from, next) {
  let isAuthenticated = false
  if (window.sessionStorage.getItem('jwt')) { isAuthenticated = true }
  if (isAuthenticated) {
    next()
  } else {
    next('/')
  }
}

function guardMyRouteAdmin (_to, _from, next) {
  let adminAuthenticated = false
  if (getRoleFromToken() === 'ROLE_ADMIN') { adminAuthenticated = true }
  if (adminAuthenticated) {
    next()
  } else {
    next('/all-certificates-page')
  }
}

function getRoleFromToken () {
  const jwtToken = window.sessionStorage.getItem('jwt')
  if (jwtToken) {
    const tokenSplit = jwtToken.split('.')
    const decoded = decodeURIComponent(escape(window.atob(tokenSplit[1])))
    const obj = JSON.parse(decoded)
    return obj.role
  }
  return ''
}

const routes = [
  {
    path: '/',
    name: '',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/LoginView.vue')
  },
  {
    path: '/admin-page',
    name: '',
    beforeEnter: guardMyroute,
    component: () => import(/* webpackChunkName: "about" */ '../views/AdminView.vue')
  },
  {
    path: '/create-certificate-page',
    name: '',
    beforeEnter: guardMyRouteAdmin,
    component: () => import(/* webpackChunkName: "about" */ '../views/CreateCertificateView.vue')
  },
  {
    path: '/all-certificates-page',
    name: '',
    beforeEnter: guardMyroute,
    component: () => import(/* webpackChunkName: "about" */ '../views/AllCertificatesView.vue')
  },
  {
    path: '/profile-page',
    name: '',
    beforeEnter: guardMyroute,
    component: () => import(/* webpackChunkName: "about" */ '../views/ChangePasswordView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
