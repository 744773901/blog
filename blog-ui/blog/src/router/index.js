import Vue from 'vue'
import VueRouter from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: () => import('../views/home/Home')
  },
  {
    path: '/articles/:articleId',
    component: () => import('../views/article/Article')
  },
  {
    path: '/categories/:categoryId',
    component: () => import('../views/article/ArticleList')
  },
  {
    path: '/tags/:tagId',
    component: () => import('../views/article/ArticleList')
  },
  {
    path: '/archives',
    component: () => import('../views/archive/Archive'),
    meta: {title: '归档'}
  },
  {
    path: '/categories',
    component: () => import('../views/category/Category'),
    meta: {title: '分类'}
  },
  {
    path: '/tags',
    component: () => import('../views/tag/Tag'),
    meta: {title: '标签'}
  },
  {
    path: '/albums',
    component: () => import('../views/album/Album'),
    meta: {title: '相册'}
  },
  {
    path: '/albums/:albumId',
    component: () => import('../views/album/Photo')
  },
  {
    path: '/talks',
    component: () => import('../views/talk/Talk'),
    meta: {
      title: '说说'
    }
  },
  {
    path: '/talks/:talkId',
    component: () => import('../views/talk/TalkInfo')
  },
  {
    path: '/links',
    component: () => import('../views/link/Link'),
    meta: {
      title: '友情链接'
    }
  },
  {
    path: '/about',
    component: () => import('../views/about/About'),
    meta: {
      title: '关于我'
    }
  },
  {
    path: '/message',
    component: () => import('../views/message/Message'),
    meta: {
      title: '留言板'
    }
  },
  {
    path: '/user',
    component: () => import('../views/user/User'),
    meta: {
      title: '个人中心'
    }
  },
  {
    path: '/oauth/login/qq',
    component: () => import('../components/OauthLogin')
  },
  {
    path: '/oauth/login/weibo',
    component: () => import('../components/OauthLogin')
  },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (to.meta.title) {
    document.title = to.meta.title;
  }
  next()
})

router.afterEach(() => {
  window.scrollTo({
    top: 0,
    behavior: "instant"
  });
  NProgress.done();
})

export default router
