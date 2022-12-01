package com.zjj.blog.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.zjj.blog.constant.RabbitMQConst.*;

/**
 * RabbitMQ配置类
 *
 * @author 知白守黑
 * @date 2022/11/12 21:33
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    FanoutExchange emailExchange() {
        return new FanoutExchange(EMAIL_EXCHANGE, true, false);
    }

    @Bean
    Binding bindingEmailDirect() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }

    @Bean
    Queue maxwellQueue() {
        return new Queue(MAXWELL_QUEUE, true);
    }

    @Bean
    FanoutExchange maxwellExchange() {
        return new FanoutExchange(MAXWELL_EXCHANGE, true, false);
    }

    @Bean
    Binding bindingMaxwellDirect() {
        return BindingBuilder.bind(maxwellQueue()).to(maxwellExchange());
    }
}
