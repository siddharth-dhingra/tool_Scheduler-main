package com.toolScheduler.ToolSchedulerApplication.config;
import com.toolScheduler.ToolSchedulerApplication.dto.PullAcknowledgement;
import com.toolScheduler.ToolSchedulerApplication.dto.ScanRequestEvent;
import com.toolScheduler.ToolSchedulerApplication.model.ScanEvent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    private static final String BOOTSTRAP = "localhost:9092";

    @Bean
    public ConsumerFactory<String, ScanRequestEvent> scanEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "toolscheduler-group");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(ScanRequestEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ScanRequestEvent> scanEventListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ScanRequestEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(scanEventConsumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, ScanEvent> scanEventProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ScanEvent> scanEventKafkaTemplate() {
        return new KafkaTemplate<>(scanEventProducerFactory());
    }

    // @Bean
    // public ProducerFactory<String, ScanParseEvent> fileLocationProducerFactory() {
    //     Map<String, Object> props = new HashMap<>();
    //     props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
    //     props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //     props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    //     return new DefaultKafkaProducerFactory<>(props);
    // }

    // @Bean
    // public KafkaTemplate<String, ScanParseEvent> fileLocationKafkaTemplate() {
    //     return new KafkaTemplate<>(fileLocationProducerFactory());
    // }

    @Bean
    public ProducerFactory<String, String> fileLocationProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> fileLocationKafkaTemplate() {
        return new KafkaTemplate<>(fileLocationProducerFactory());
    }


    @Bean
    public ProducerFactory<String, PullAcknowledgement> acknowledgementProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, PullAcknowledgement> acknowledgementKafkaTemplate() {
        return new KafkaTemplate<>(acknowledgementProducerFactory());
    }
}