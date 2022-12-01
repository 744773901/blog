import request from "../utils/request";

export function listFriendLinks() {
    return request({
        url: '/links',
        method: 'GET'
    })
}
