import request from "@/utils/request";

export function getArticles(params) {
    return request({
        url: '/admin/articles',
        method: 'GET',
        params
    })
}

export function getArticleById(id) {
    return request({
        url: `/admin/article/${id}`,
        method: 'GET'
    })
}

export function saveOrUpdateArticle(data) {
    return request({
        url: '/admin/article',
        method: 'POST',
        data
    })
}

export function updateArticleTop(data) {
    return request({
        url: '/admin/article/top',
        method: 'PUT',
        data
    })
}

export function updateArticleLogicDelete(data) {
    return request({
        url: '/admin/article',
        method: 'PUT',
        data
    })
}

export function deleteArticle(data) {
    return request({
        url: '/admin/article',
        method: 'DELETE',
        data
    })
}

export function uploadArticleImage(data) {
    return request({
        url: '/admin/article/image',
        method: 'POST',
        data
    })
}
