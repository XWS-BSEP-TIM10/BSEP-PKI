import { createRouter, createWebHistory } from 'vue-router'

function guardMyroute (to, from, next) {
  let isAuthenticated = false
  if (window.sessionStorage.getItem('jwt')) { isAuthenticated = true } else { isAuthenticated = false }
  if (isAuthenticated) {
    next()
  } else {
    next('/')
  }
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
    beforeEnter: guardMyroute,
    component: () => import(/* webpackChunkName: "about" */ '../views/CreateCertificateView.vue')
  },
  {
    path: '/all-certificates-page',
    name: '',
    beforeEnter: guardMyroute,
    component: () => import(/* webpackChunkName: "about" */ '../views/AllCertificatesView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
