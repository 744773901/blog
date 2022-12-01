import request from "../utils/request";

export function listPhotoAlbums() {
    return request({
        url: '/photo/albums',
        method: 'GET'
    })
}

export function listPhotosByAlbumId(albumId,params) {
    return request({
        url: `/album/${albumId}/photos`,
        method: 'GET',
        params
    })
}
