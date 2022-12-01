import request from "@/utils/request";

export function getUserInfo() {
    return request({
        url: '/user/info',
        method: 'GET'
    });
}

export function updateUserInfo(data) {
    return request({
        url: '/user/info',
        method: 'PUT',
        data
    });
}

export function updateAdminPassword(data) {
    return request({
        url: '/admin/user/password',
        method: 'PUT',
        data
    });
}

export function getUsers(params) {
    return request({
        url: '/admin/users',
        method: 'GET',
        params
    })
}

export function updateUserDisable(data) {
    return request({
        url: '/admin/user/disable',
        method: 'PUT',
        data
    })
}

export function updateUserRole(data) {
    return request({
        url: '/admin/user/role',
        method: 'PUT',
        data
    })
}

export function getOnlineUsers(params) {
    return request({
        url: '/admin/users/online',
        method: 'GET',
        params
    })
}

export function offlineUser(id) {
    return request({
        url: `/admin/user/${id}/offline`,
        method: 'DELETE'
    })
}
