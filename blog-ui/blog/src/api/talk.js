import request from "../utils/request";

export function listTalks(params) {
    return request({
        url: '/talks',
        method: 'GET',
        params
    })
}

export function listHomeTalks() {
    return request({
        url: '/home/talks',
        method: 'GET'
    })
}

export function saveTalkLike(talkId) {
    return request({
        url: `/talk/${talkId}/like`,
        method: 'POST'
    })
}

export function getTalkById(talkId) {
    return request({
        url: `/talk/${talkId}`,
        method: 'GET'
    })
}
