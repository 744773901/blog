import request from "@/utils/request";

export function getTagByOption(params) {
    return request({
        url: '/admin/tag/option',
        method: 'GET',
        params
    })
}

export function getAdminTag(params) {
    return request({
        url: '/admin/tags',
        method: 'GET',
        params
    })
}

export function saveOrUpdateTag(data) {
    return request({
        url: '/admin/tag',
        method: 'POST',
        data
    })
}

export function deleteTag(data) {
    return request({
        url: '/admin/tag',
        method: 'DELETE',
        data
    })
}
