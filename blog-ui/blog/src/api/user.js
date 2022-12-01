import request from "../utils/request";

export function login(data) {
    return request({
        url: '/login',
        method: 'POST',
        data
    })
}

export function logout() {
    return request({
        url: '/logout',
        method: 'POST'
    })
}

export function register(data) {
    return request({
        url: '/register',
        method: 'POST',
        data
    })
}

export function getInfo() {
    return request({
        url: '/',
        method: 'GET'
    })
}

export function updateUserInfo(data) {
    return request({
        url: '/user/info',
        method: 'PUT',
        data
    })
}

export function report() {
    return request({
        url: '/report',
        method: 'POST'
    })
}

export function sendCode(params) {
    return request({
        url: '/user/code',
        method: 'GET',
        params
    })
}

export function saveUserEmail(data) {
    return request({
        url: '/user/email',
        method: 'POST',
        data
    })
}

export function updatePassword(data) {
    return request({
        url: '/user/password',
        method: 'PUT',
        data
    })
}

export function sendVoice(data) {
    return request({
        headers: {'Content-Type': 'multipart/form-data'},
        url: '/voice',
        method: 'POST',
        data
    })
}
