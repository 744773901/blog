package com.zjj.blog.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjj.blog.dto.UniqueViewDTO;
import com.zjj.blog.entity.UniqueView;
import com.zjj.blog.mapper.UniqueViewMapper;
import com.zjj.blog.service.RedisService;
import com.zjj.blog.service.UniqueViewService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.zjj.blog.constant.CommonConst.TIMEZONE_SHANGHAI;
import static com.zjj.blog.constant.RedisConst.UNIQUE_VISITOR;
import static com.zjj.blog.constant.RedisConst.USER_AREA;

/**
 * @author 知白守黑
 * @date 2022/11/19 23:21
 */
@Service
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewMapper, UniqueView> implements UniqueViewService {

    @Resource
    private RedisService redisService;
    @Resource
    private UniqueViewMapper uniqueViewMapper;

    /**
     * 每天00:00时刻执行
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = TIMEZONE_SHANGHAI)
    public void saveUniqueView() {
        //获取每天访问用户量
        Long count = redisService.sSize(UNIQUE_VISITOR);
        //保存昨天访问量
        UniqueView uniqueView = UniqueView.builder()
                .createTime(LocalDateTimeUtil.offset(LocalDateTime.now(ZoneId.of(TIMEZONE_SHANGHAI)), -1, ChronoUnit.DAYS))
                .viewsCount(Optional.of(count.intValue()).orElse(0))
                .build();
        uniqueViewMapper.insert(uniqueView);
    }

    /**
     * 每天00:01时刻执行
     */
    @Scheduled(cron = "0 1 0 * * ?", zone = TIMEZONE_SHANGHAI)
    public void clearUniqueView() {
        //清空每天访问用户缓存
        redisService.del(UNIQUE_VISITOR);
        //清空访问用户地区统计缓存
        redisService.del(USER_AREA);
    }

    @Override
    public List<UniqueViewDTO> listUniqueView() {
        //获取七天内的开始时间和结束时间
        //例如: 现在时间是2022-11-24 则开始时间是2022-11-17 00:00:00 结束时间是2022-11-24 23:59:59
        DateTime startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new Date(), -7));
        DateTime endTime = DateUtil.endOfDay(new Date());
        return uniqueViewMapper.listUniqueView(startTime, endTime);
    }
}
