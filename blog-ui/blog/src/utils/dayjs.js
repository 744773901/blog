import Vue from 'vue'
import dayjs from "dayjs";

export default function () {
    Vue.filter("date", (value) => {
        return dayjs(value).format("YYYY-MM-DD")
    });

    Vue.filter("datetime", (value) => {
        return dayjs(value).format("YYYY-MM-DD HH:mm:ss")
    });

    Vue.filter("hour", (value) => {
        return dayjs(value).format("YYYY-MM-DD HH:mm")
    });

    Vue.filter("year", (value) => {
        return dayjs(value).format("YYYY")
    });

    Vue.filter("time", (value) => {
        return dayjs(value).format("HH:mm:ss")
    });

    Vue.filter("num", (value) => {
        if (value >= 1000) {
            return (value / 1000).toFixed(1) + "k";
        }
        return value;
    });
}
