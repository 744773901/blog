import request from "@/utils/request";

export function getResourceOptions() {
    return request({
        url: '/admin/role/resources',
        method: 'GET'
    })
}

export function getResources(params) {
    return request({
        url: '/admin/resources',
        method: 'GET',
        params
    })
}

export function deleteResourceById(id) {
    return request({
        url: `/admin/resources/${id}`,
        method: 'DELETE'
    })
}

export function saveOrUpdateResource(data) {
    return request({
        url: '/admin/resources',
        method: 'POST',
        data
    })
}
