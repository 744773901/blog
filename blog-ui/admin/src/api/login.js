import request from "@/utils/request";

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

export function getCodeImg() {
    return request({
        url: '/captchaImage',
        method: 'GET'
    })
}
