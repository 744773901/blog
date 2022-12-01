package com.zjj.blog.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Objects;

/**
 * 分页工具类
 *
 * @author 知白守黑
 * @date 2022/8/9 17:23
 */
public class PageUtil {

    /**
     * 封装分页对象
     */
    private static final ThreadLocal<Page<?>> PAGE = new ThreadLocal<>();

    /**
     * 获取分页对象
     *
     * @return {@link Page} 分页对象
     */
    public static Page<?> getPage() {
        Page<?> page = PAGE.get();
        if (Objects.isNull(page)) {
            setPage(new Page<>());
        }
        return PAGE.get();
    }

    public static void setPage(Page<?> page) {
        PAGE.set(page);
    }

    /**
     * 获取当前页
     *
     * @return 当前页
     */
    public static Long getCurrent() {
        return getPage().getCurrent();
    }

    /**
     * 获取每页条目数
     *
     * @return 每页条目数
     */
    public static Long getSize() {
        return getPage().getSize();
    }

    public static Long getLimitCurrent() {
        return (getCurrent() - 1) * getSize();
    }

    public static void remove() {
        PAGE.remove();
    }
}
