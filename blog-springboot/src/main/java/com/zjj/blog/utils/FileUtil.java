package com.zjj.blog.utils;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 文件工具类
 *
 * @author 知白守黑
 * @date 2022/8/7 20:03
 */
public class FileUtil {

    /**
     * 获取文件MD5值
     *
     * @param inputStream 字节输入流
     * @return {@link String} 文件MD5值
     */
    public static String getMD5(InputStream inputStream) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encode(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return {@link String} 文件扩展名
     */
    public static String getExtName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static void main(String[] args) {
        System.out.println(getExtName("0.2.15151.txt"));
    }
}
