import request from "@/utils/request";

export function getAdminComment(params) {
    return request({
        url: '/admin/comments',
        method: 'GET',
        params
    })
}

export function updateCommentsReview(data) {
    return request({
        url: '/admin/comments/review',
        method: 'PUT',
        data
    })
}

export function deleteComments(data) {
    return request({
        url: '/admin/comments',
        method: 'DELETE',
        data
    })
}
