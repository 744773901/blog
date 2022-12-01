import request from "../utils/request";

export function listComments(params) {
    return request({
        url: '/comments',
        method: 'GET',
        params
    })
}

export function saveCommentLike(id) {
    return request({
        url: `/comments/${id}/like`,
        method: 'POST'
    })
}

export function saveComment(data) {
    return request({
        url: `/comments`,
        method: 'POST',
        data
    })
}

export function listRepliesById(id,params) {
    return request({
        url: `/comments/${id}/replies`,
        method: 'GET',
        params
    })
}
