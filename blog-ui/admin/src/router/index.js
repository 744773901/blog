import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter);

const originalPush = VueRouter.prototype.push;

VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

const routes = [
    {
        path: '/login',
        name: 'Login',
        hidden: true,
        component: () => import("@/views/login/Login")
    },
]
const createRouter = () =>
    new VueRouter({
        mode: "history",
        routes
    });


const router = createRouter();

export function resetRouter() {
    const newRouter = createRouter();
    router.matcher = newRouter.matcher;
}

export default router;
