package com.zjj.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.zjj.blog.dto.ArticleSearchDTO;
import com.zjj.blog.dto.MaxwellDataDTO;
import com.zjj.blog.mapper.ElasticSearchMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.zjj.blog.constant.RabbitMQConst.MAXWELL_QUEUE;

/**
 * @author 知白守黑
 * @date 2022/11/16 23:18
 */
@Component
@RabbitListener(queues = MAXWELL_QUEUE)
public class MaxwellConsumer {

    @Resource
    private ElasticSearchMapper elasticSearchMapper;

    @RabbitHandler
    public void handle(byte[] data) {
        MaxwellDataDTO maxwellDataDTO = JSON.parseObject(data, MaxwellDataDTO.class);
        ArticleSearchDTO article = JSON.parseObject(JSON.toJSONString(maxwellDataDTO.getData()), ArticleSearchDTO.class);
        switch (maxwellDataDTO.getType()) {
            case "insert":
            case "update":
                //更新文章数据
                elasticSearchMapper.save(article);
                break;
            case "delete":
                //删除文章
                elasticSearchMapper.deleteById(article.getId());
                break;
            default:
                break;
        }
    }
}
