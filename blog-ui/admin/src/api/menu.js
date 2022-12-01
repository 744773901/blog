import Layout from '@/layout/Index';
import {Message} from "element-ui";
import router from "@/router";
import store from "@/store";
import request from "@/utils/request";

export function report() {
    return request({
        url: '/report',
        method: 'POST'
    })
}

export function getUserMenu() {
    return request({
        url: '/admin/user/menus',
        method: 'GET'
    })
}

export function getMenuOptions() {
    return request({
        url: '/admin/role/menus',
        method: 'GET'
    })
}

export function getMenus(params) {
    return request({
        url: '/admin/menus',
        method: 'GET',
        params
    })
}

export function saveOrUpdateMenu(data) {
    return request({
        url: '/admin/menus',
        method: 'POST',
        data
    })
}

export function deleteMenuById(id) {
    return request({
        url: `/admin/menus/${id}`,
        method: 'DELETE'
    })
}

export function getMenu() {
    getUserMenu().then(({data}) => {
        if (data.flag) {
            let userMenuList = data.data;
            userMenuList.forEach(menu => {
                if (menu.children.length === 0) {
                    menu.children.push({name: menu.name, path: '', component: menu.component, icon: menu.icon})
                    menu.component = Layout
                    menu.name = null
                }
                if (menu.component === 'Layout') {
                    menu.component = Layout
                }
                if (menu.children && menu.children.length > 0) {
                    menu.children.forEach(menu => {
                        menu.component = loadView(menu.component)
                    });
                }
            });
            store.commit('setUserMenuList', userMenuList);
            // router.addRoutes(userMenuList);
            userMenuList.forEach(item => router.addRoute(item))
        } else {
            Message.error(data.message);
            router.push({path: '/login'});
        }
    });
}

export const loadView = view => {
    // 路由懒加载
    return resolve => require([`@/views${view}`], resolve);
};
