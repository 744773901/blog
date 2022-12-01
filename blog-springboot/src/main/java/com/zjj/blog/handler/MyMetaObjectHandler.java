package com.zjj.blog.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.zjj.blog.constant.CommonConst.TIMEZONE_SHANGHAI;

/**
 * MybatisPlus自动填充功能
 *
 * @author 知白守黑
 * @date 2022/7/23 20:17
 * @see <a href="https://baomidou.com/pages/4c6bcf">官方文档地址</a>
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now(ZoneId.of(TIMEZONE_SHANGHAI)));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now(ZoneId.of(TIMEZONE_SHANGHAI)));
    }
}
