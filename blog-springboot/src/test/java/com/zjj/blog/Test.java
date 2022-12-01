package com.zjj.blog;

import com.alibaba.fastjson2.JSON;

import java.io.File;
import java.util.*;

/**
 * @author 知白守黑
 * @date 2022/8/24 20:06
 */

public class Test {
    public static void main(String[] args) {
        File dir = new File("F:\\workspace\\blog\\blog-vue\\admin\\src\\assets\\icons\\svg");
        List<String> fileNameList = new ArrayList<>();
        getAllFiles(dir, fileNameList);
        String jsonString = JSON.toJSONString(fileNameList);
        System.out.println(fileNameList.size());
        System.out.println(jsonString);
    }

    public static void getAllFiles(File dir, List<String> fileNameList) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (Objects.nonNull(files)) {
                for (File file : files) {
                    String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                    fileNameList.add(fileName);
                }
            }
        }
    }
}
