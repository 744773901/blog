import request from "../utils/request";

export function listMessages() {
    return request({
        url: '/messages',
        method: 'GET'
    })
}

export function saveMessage(data) {
    return request({
        url: '/messages',
        method: 'POST',
        data
    })
}
