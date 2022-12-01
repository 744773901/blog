package com.zjj.blog.consumer;

import com.alibaba.fastjson2.JSON;
import com.zjj.blog.dto.EmailDTO;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.zjj.blog.constant.RabbitMQConst.EMAIL_QUEUE;

/**
 * @author 知白守黑
 * @date 2022/11/12 21:43
 */
@Component
@RabbitListener(queues = EMAIL_QUEUE)
public class EmailConsumer {

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String email;

    @RabbitHandler
    public void handle(byte[] data) {
        EmailDTO emailDTO = JSON.parseObject(data, EmailDTO.class);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(emailDTO.getEmail());
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getContent());
        javaMailSender.send(message);
    }
}
