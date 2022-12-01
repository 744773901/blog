import request from "@/utils/request";

export function getCategoryByOption(params) {
    return request({
        url: '/admin/category/option',
        method: 'GET',
        params
    })
}

export function getAdminCategories(params) {
    return request({
        url: '/admin/categories',
        method: 'GET',
        params
    })
}

export function saveOrUpdateCategory(data) {
    return request({
        url: '/admin/category',
        method: 'POST',
        data
    })
}

export function deleteCategory(data) {
    return request({
        url: '/admin/category',
        method: 'DELETE',
        data
    })
}
