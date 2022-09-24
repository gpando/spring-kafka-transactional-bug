package com.example.kafkatransactionalbug;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


public class AccountService {

    private final AccountRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;

    public AccountService(AccountRepository accountRepository,
                      KafkaTemplate<String, String> kafkaTemplate,
                      String topicName) {

        this.repository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    private Account createAccount(String type) {
        return new Account(String.format("%s message [%s] [%s]", type, UUID.randomUUID().toString(), Instant.now().toString()));
    }

    private void doTask(String type) {
        Account account = createAccount(type);

        kafkaTemplate.send(topicName,account.getName());
        
        repository.save(account);
    }

    @Transactional("transactionManager")
    public void doTaskOK() {
        doTask("OK");
    }

    @Transactional("transactionManager")
    public void doTaskKO() {
        doTask("KO");

        throw new RuntimeException("Ops");
    }
}
