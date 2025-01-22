package com.student;

import com.student.service.CollegeService;
import com.student.store.StudentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * This class is to start the execution of the whole system.
 */
@SpringBootApplication
public class Application {
    /**
     * This is the point where application starts to run.
     */
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }
}
