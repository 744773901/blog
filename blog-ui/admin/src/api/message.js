import request from "@/utils/request";

export function getAdminMessage(params) {
    return request({
        url: '/admin/messages',
        method: 'GET',
        params
    })
}

export function updateMessageReview(data) {
    return request({
        url: '/admin/messages/review',
        method: 'PUT',
        data
    })
}

export function deleteMessage(data) {
    return request({
        url: '/admin/messages',
        method: 'DELETE',
        data
    })
}
