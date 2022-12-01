import request from "../utils/request";

export function listArticles(params) {
    return request({
        url: '/articles',
        method: 'GET',
        params
    })
}

export function listArticlesByCondition(params) {
    return request({
        url: '/articles/condition',
        method: 'GET',
        params
    })
}

export function getArticleById(id) {
    return request({
        url: `/article/${id}`,
        method: 'GET'
    })
}

export function saveArticleLike(id) {
    return request({
        url: `/article/like/${id}`,
        method: 'POST'
    })
}

export function searchArticle(params) {
    return request({
        url: '/article/search',
        method: 'GET',
        params
    })
}

export function listArchives(params) {
    return request({
        url: '/articles/archives',
        method: 'GET',
        params
    })
}

export function listCategories() {
    return request({
        url: '/categories',
        method: 'GET'
    })
}

export function listTags() {
    return request({
        url: '/tags',
        method: 'GET'
    })
}
