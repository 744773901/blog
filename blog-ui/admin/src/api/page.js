import request from "@/utils/request";

export function getPageList() {
    return request({
        url: '/admin/pages',
        method: 'GET'
    })
}

export function saveOrUpdatePage(data) {
    return request({
        url: '/admin/pages',
        method: 'POST',
        data
    })
}

export function deletePageById(id) {
    return request({
        url: `/admin/pages/${id}`,
        method: 'DELETE'
    })
}
