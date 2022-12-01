import request from "@/utils/request";

export function listOperationLog(params) {
    return request({
        url: '/admin/operation/log',
        method: 'GET',
        params
    })
}

export function deleteOperationLog(data) {
    return request({
        url: '/admin/operation/log',
        method: 'DELETE',
        data
    })
}
