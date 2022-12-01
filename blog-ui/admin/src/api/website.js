import request from "@/utils/request";

export function getWebSiteConfig() {
    return request({
        url: '/admin/website/config',
        method: 'GET'
    })
}

export function updateWebSiteConfig(data) {
    return request({
        url: '/admin/website/config',
        method: 'PUT',
        data
    })
}
