package com.student.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AppConfig {
    @Bean
    public MongoClient mongoClient() {
        log.info("Initializing Mongo Clients");
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public MongoDatabase mongoDatabase(){
        log.info("Initializing Mongo DB");
        return mongoClient().getDatabase("NewStudentDB");
    }
}
