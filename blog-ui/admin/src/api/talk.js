import request from "@/utils/request";

export function saveOrUpdateAdminTalk(data) {
    return request({
        url: '/admin/talk',
        method: 'POST',
        data
    })
}

export function getAdminTalks(params) {
    return request({
        url: '/admin/talks',
        method: 'GET',
        params
    })
}

export function getAdminTalkById(id) {
    return request({
        url: `/admin/talk/${id}`,
        method: 'GET'
    })
}

export function deleteTalkById(id) {
    return request({
        url: `/admin/talk/${id}`,
        method: 'DELETE'
    })
}
