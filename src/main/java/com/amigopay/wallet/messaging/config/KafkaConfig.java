package com.amigopay.wallet.messaging.config;

import com.amigopay.events.PaymentInitiatedEvent;
import com.amigopay.events.UserCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // PaymentInitiatedEvent Consumer
    @Bean
    public ConsumerFactory<String, PaymentInitiatedEvent> paymentInitiatedEventConsumerFactory() {
        return new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(consumerConfigs(PaymentInitiatedEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentInitiatedEvent> paymentInitiatedKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentInitiatedEvent>();
        factory.setConsumerFactory(paymentInitiatedEventConsumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // UserCreatedEvent Consumer
    @Bean
    public ConsumerFactory<String, UserCreatedEvent> userCreatedEventConsumerFactory() {
        return new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(consumerConfigs(UserCreatedEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent> userCreatedKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, UserCreatedEvent>();
        factory.setConsumerFactory(userCreatedEventConsumerFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    // Base config parameterized by type
    private <T> Map<String, Object> consumerConfigs(Class<T> clazz) {
        var configs = new HashMap<String, Object>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "wallet-consumer");
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "com.amigopay.events");
        configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, clazz.getName());
        configs.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return configs;
    }
}
