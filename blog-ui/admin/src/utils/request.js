import axios from "axios";
import {Message} from "element-ui";
import {getToken} from "@/utils/auth";

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8';

const service = axios.create({
    baseURL: '/api',
    timeout: 1000000
});

service.interceptors.request.use(config => {
    if (getToken()) {
        config.headers['Authorization'] = getToken();
    }
    return config;
}, error => {
    return Promise.reject(error);
});

service.interceptors.response.use(res => {
    /*switch (res.data.code) {
        case 400:
            Message({
                type: "error",
                message: res.data.message
            });
            break;
        case 401:
            Message({
                type: "error",
                message: res.data.message
            });
            router.push({path: "/login"});
            break;
        case 500:
            Message({
                type: "error",
                message: res.data.message
            });
            break;
        case 501:
            Message({
                type: "error",
                message: res.data.message
            });
            break;
    }*/
    if (res.data.code !== 200) {
        Message({type: 'error', message: res.data.message})
    }
    return res;
}, error => {
    return Promise.reject(error);
});

export default service;
