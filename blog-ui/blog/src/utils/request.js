import axios from "axios";
import Vue from "vue";
import store from '../store';

const service = axios.create({
    baseURL: '/api',
    timeout: 600000
})

service.interceptors.request.use(config => {
    // const token = JSON.parse(sessionStorage.getItem('vuex')).token || ''
    if (store.state.token) {
        config.headers['Authorization'] = store.state.token
    }
    return config;
}, error => {
    return new Promise.reject(error);
})

service.interceptors.response.use(res => {
    const {data} = res
    switch (data.code) {
        case 500:
            Vue.prototype.$toast({type: "error", message: "系统异常"});
            break;
        case 401:
            store.commit('LOGOUT')
            break;
    }
    return data;
}, error => {
    return Promise.reject(error);
})

export default service
