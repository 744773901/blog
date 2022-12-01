import request from "@/utils/request";

export function listFriendLink(params) {
    return request({
        url: '/admin/links',
        method: 'GET',
        params
    })
}

export function saveOrUpdateFriendLink(data) {
    return request({
        url: '/admin/links',
        method: 'POST',
        data
    })
}

export function deleteFriendLink(ids) {
    return request({
        url: '/admin/links',
        method: 'DELETE',
        data: ids
    })
}
