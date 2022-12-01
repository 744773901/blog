import request from "@/utils/request";

export function getAdminBlogInfo() {
    return request({
        url: '/admin',
        method: 'GET'
    })
}

export function getUserArea(params) {
    return request({
        url: '/admin/users/area',
        method: 'GET',
        params
    })
}
