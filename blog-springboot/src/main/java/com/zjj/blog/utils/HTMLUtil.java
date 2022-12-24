package com.zjj.blog.utils;

import com.alibaba.fastjson2.JSON;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.zjj.blog.constant.CommonConst;
import com.zjj.blog.vo.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * HTML工具类
 */
public class HTMLUtil {

    private static final SensitiveWordBs WORD_BS = SensitiveWordBs.newInstance()
            .ignoreCase(true)
            .ignoreWidth(true)
            .ignoreNumStyle(true)
            .ignoreChineseStyle(true)
            .ignoreEnglishStyle(true)
            .ignoreRepeat(true)
            .enableNumCheck(false)
            .enableEmailCheck(false)
            .enableUrlCheck(false)
            .init();


    /**
     * 删除标签
     *
     * @param source 需要进行剔除HTML的文本
     * @return 过滤后的内容
     */
    public static String filter(String source) {
        // 敏感词过滤
        source = WORD_BS.replace(source);
        // 保留图片标签
        source = source.replaceAll("(?!<(img).*?>)<.*?>", "")
                .replaceAll("(onload(.*?)=)", "")
                .replaceAll("(onerror(.*?)=)", "");
        return deleteHMTLTag(source);
    }

    /**
     * 删除标签
     *
     * @param source 文本
     * @return 过滤后的文本
     */
    public static String deleteHMTLTag(String source) {
        // 删除转义字符
        source = source.replaceAll("&.{2,6}?;", "");
        // 删除script标签
        source = source.replaceAll("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", "");
        // 删除style标签
        source = source.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "");
        return source;
    }

    /**
     * 响应处理
     *
     * @param response 响应
     * @param result   返回结果
     * @throws IOException IO异常
     */
    public static void render(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType(CommonConst.APPLICATION_JSON);
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(JSON.toJSONBytes(result));
        outputStream.flush();
        outputStream.close();
    }

}