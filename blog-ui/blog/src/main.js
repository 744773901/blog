import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import './assets/css/index.css'
import './assets/css/markdown.css'
import './assets/css/iconfont/iconfont.css'
import './assets/icons/index'
import animated from 'animate.css'
import dayjs from './utils/dayjs'
import InfiniteLoading from 'vue-infinite-loading'
import config from './assets/js/config'
import Toast from './components/toast/index'
import axios from 'axios'
import VueAxios from 'vue-axios'
import VueImageSwipe from 'vue-image-swipe/src/lib'
import 'vue-image-swipe/dist/vue-image-swipe.css'
import { vueBaberrage } from 'vue-baberrage'
import 'highlight.js/styles/atom-one-dark.css'
import './assets/css/vue-social-share/client.css'
import Share from 'vue-social-share'

Vue.prototype.config = config
Vue.config.productionTip = false
Vue.use(animated)
Vue.use(dayjs)
Vue.use(InfiniteLoading)
Vue.use(Toast)
Vue.use(VueAxios, axios)
Vue.use(VueImageSwipe)
Vue.use(vueBaberrage)
Vue.use(Share)

new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
