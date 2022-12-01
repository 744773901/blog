import request from "@/utils/request";

export function getAboutContent() {
    return request({
        url: '/about',
        method: "GET"
    })
}

export function updateAboutContent(data) {
    return request({
        url: '/admin/about',
        method: "PUT",
        data
    })
}
