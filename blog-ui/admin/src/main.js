import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/css/index.css';
import axios from "axios";
import VueAxios from "vue-axios";
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import '@/assets/icons/index';
import dayjs from "dayjs";
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';
import ECharts from 'vue-echarts';
import "echarts/lib/chart/line";
import "echarts/lib/chart/pie";
import "echarts/lib/chart/bar";
import "echarts/lib/chart/map";
import "echarts/lib/component/tooltip";
import "echarts/lib/component/legend";
import "echarts/lib/component/title";
import VueCalendarHeatmap from 'vue-calendar-heatmap'
import tagCloud from "@/components/tag-cloud";

Vue.use(ElementUI)
Vue.use(VueAxios, axios)
Vue.use(mavonEditor)
Vue.use(VueCalendarHeatmap)
Vue.use(tagCloud)
Vue.component("v-chart", ECharts);
Vue.prototype.$dayjs = dayjs
Vue.config.productionTip = false

Vue.filter("date", (value, timeFormat = "YYYY-MM-DD") => {
    return dayjs(value).format(timeFormat)
});

Vue.filter("datetime", (value, timeFormat = "YYYY-MM-DD HH:mm:ss") => {
    return dayjs(value).format(timeFormat)
});

NProgress.configure({
    easing: "ease", // 动画方式
    speed: 500, // 递增进度条的速度
    showSpinner: false, // 是否显示加载ico
    trickleSpeed: 200, // 自动递增间隔
    minimum: 0.3 // 初始化时的最小百分比
});

const whiteList = ['/login']

router.beforeEach((to, from, next) => {
    NProgress.start()
    if (!store.getters.token) {
        to.path === '/login' ? next() : next({path: '/login'})
        NProgress.done()
    } else if (whiteList.indexOf(to.path) !== -1) {
        next()
        NProgress.done()
    } else {
        if (store.getters.roleList.length === 0) {
            store.dispatch('GetUserInfo').then()
        }
        to.path === '/login' ? next({path: '/'}) : next();
        NProgress.done()
    }
});

router.afterEach(() => {
    NProgress.done()
});

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
