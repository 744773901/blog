import Vue from 'vue'
import Vuex from 'vuex'
import {getToken, removeToken, setToken} from "@/utils/auth";
import {login, logout} from "@/api/login";
import {getUserInfo} from "@/api/user";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        collapse: false,
        token: getToken(),
        userInfo: {},
        roleList: [],
        userMenuList: [],
        tabList: [{name: '首页', path: '/'}]
    },
    mutations: {
        SET_TOKEN: (state, token) => {
            state.token = token;
        },
        SET_USER: (state, userInfo) => {
            state.userInfo.userId = userInfo.id;
            state.userInfo.avatar = userInfo.avatar;
            state.userInfo.nickname = userInfo.nickname;
            state.userInfo.intro = userInfo.intro;
            state.userInfo.webSite = userInfo.webSite;
        },
        SET_ROLE: (state, roleList) => {
            state.roleList = roleList;
        },
        setUserMenuList(state, userMenuList) {
            state.userMenuList = userMenuList;
        },
        trigger(state) {
            state.collapse = !state.collapse;
        },
        saveTab(state, tab) {
            if (state.tabList.findIndex(item => item.path === tab.path) === -1) {
                state.tabList.push({name: tab.name, path: tab.path})
            }
        },
        removeTab(state, tab) {
            let index = state.tabList.findIndex(item => item.path === tab.path)
            state.tabList.splice(index, 1)
        },
        resetTab(state) {
            state.tabList = [{name: '首页', path: '/'}]
        },
        updateAvatar(state, avatar) {
            state.userInfo = Object.assign({}, state.userInfo, {avatar})
        },
        updateUserInfo(state, userInfo) {
            state.userInfo.nickname = userInfo.nickname
            state.userInfo.intro = userInfo.intro
            state.userInfo.webSite = userInfo.webSite
        },
    },
    actions: {
        Login({commit, dispatch}, loginUser) {
            return new Promise((resolve, reject) => {
                login(loginUser).then(({data}) => {
                    setToken(data.data.token);
                    commit('SET_TOKEN', data.data.token);
                    dispatch('GetUserInfo')
                    resolve(data);
                }).catch(error => {
                    reject(error);
                })
            })
        },
        GetUserInfo({commit}) {
            return new Promise((resolve, reject) => {
                getUserInfo().then(({data}) => {
                    commit('SET_USER', data.data);
                    commit('SET_ROLE', data.data.roleList);
                    resolve(data);
                }).catch(error => {
                    reject(error);
                })
            })
        },
        Logout({commit}) {
            return new Promise((resolve, reject) => {
                logout().then(res => {
                    commit('SET_TOKEN', '')
                    commit('SET_ROLE', [])
                    commit('SET_USER', {})
                    commit('setUserMenuList', [])
                    removeToken()
                    resolve()
                }).catch(error => reject(error))
            })
        }
    },
    modules: {},
    getters: {
        token: state => state.token,
        roleList: state => state.roleList
    }
});
