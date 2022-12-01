import request from "@/utils/request";

export function getUserRoles() {
    return request({
        url: '/admin/user/roles',
        method: 'GET'
    })
}

export function getRoles(params) {
    return request({
        url: '/admin/roles',
        method: 'GET',
        params
    })
}

export function deleteRolesById(ids) {
    return request({
        url: '/admin/roles',
        method: 'DELETE',
        data: ids
    })
}

export function saveOrUpdateRole(data) {
    return request({
        url: '/admin/role',
        method: 'POST',
        data
    })
}
