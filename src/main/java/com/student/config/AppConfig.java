package com.student.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
/*
  It is configuration class used for configuring the beans for spring container.
 */
public class AppConfig {
    @Bean
    /**
     * This Bean is to create the connection with the database and return the MongoClient variable.
     */
    public MongoClient mongoClient() {
        log.info("Initializing Mongo Clients");
        return MongoClients.create("mongodb://localhost:27017");
    }


    @Bean
    /**
     * This bean is used to get the database using the MongoClient.
     */
    public MongoDatabase mongoDatabase(){
        log.info("Initializing Mongo DB");
        return mongoClient().getDatabase("NewStudentDB");
    }
}
