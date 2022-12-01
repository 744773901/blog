import request from "@/utils/request";

export function getPhotoAlbumList(params) {
    return request({
        url: '/admin/photo/albums',
        method: 'GET',
        params
    })
}

export function getAllPhotoAlbumInfo() {
    return request({
        url: '/admin/photo/albums/info',
        method: 'GET'
    })
}

export function getPhotoAlbumById(id) {
    return request({
        url: `/admin/photo/album/${id}`,
        method: 'GET'
    })
}

export function saveOrUpdatePhotoAlbum(data) {
    return request({
        url: '/admin/photo/albums',
        method: 'POST',
        data
    })
}

export function deletePhotoAlbum(id) {
    return request({
        url: `/admin/photo/album/${id}`,
        method: 'DELETE'
    })
}

export function getPhotoList(params) {
    return request({
        url: '/admin/photos',
        method: 'GET',
        params
    })
}

export function updatePhotosIsDelete(data) {
    return request({
        url: '/admin/photos/delete',
        method: 'PUT',
        data
    })
}

export function batchDeletePhotos(ids) {
    return request({
        url: '/admin/photos',
        method: 'DELETE',
        data: ids
    })
}

export function savePhotos(data) {
    return request({
        url: '/admin/photos',
        method: 'POST',
        data
    })
}

export function updatePhotos(data) {
    return request({
        url: '/admin/photos',
        method: 'PUT',
        data
    })
}

export function movePhotos(data) {
    return request({
        url: '/admin/photos/move',
        method: 'PUT',
        data
    })
}
