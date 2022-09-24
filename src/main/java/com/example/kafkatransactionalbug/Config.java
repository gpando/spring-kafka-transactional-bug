package com.example.kafkatransactionalbug;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;

@Configuration
public class Config {

    // @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

   
    // Create datasource transaction manager so it can chain with kafka transaction manager. 
    // Following this documentation: https://docs.spring.io/spring-kafka/reference/html/#ex-jdbc-sync

    // @Bean
    public DataSourceTransactionManager dstm(DataSource dataSource) {

        return new DataSourceTransactionManager(dataSource);
    }

    // @Bean
    public KafkaTemplate<String, String> kafkaTemplateTransactional(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers)
    {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        // props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction-id-example"); // enable transaction awareness
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic createTopic(@Value("${kafka.topicName}") String topicName)
    {
        return TopicBuilder.name(topicName)
                           .build();
    }

    @Bean
    public AccountService accountService(AccountRepository accountRepository,
                                         KafkaTemplate<String, String> kafkaTemplate,
                                         @Value("${kafka.topicName}") String topicName) {

        return new AccountService(accountRepository, kafkaTemplate, topicName);
    }  
}
