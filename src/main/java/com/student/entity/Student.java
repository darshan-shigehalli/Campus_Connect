package com.student.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Entity class representing a Student
  */
@Data
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student implements Serializable {
    @Id
    @NotNull(message = "Student id cannot be null")
    public int id;

    @NotNull(message = "Student's department id cannot be null")
    public int dept_id;

    @NotNull
    @Pattern(regexp = "[a-zA-Z ]+", message = "Invalid student name format")
    public String name;

    @Min(value = 6, message = "Age must be at least 6")
    @Max(value = 100, message = "Age must be no more than 100")
    public int age;

    /**
     * Override toString method for easy printing
     * @return string consists of every state of Student class.
     */
    @Override
    public  String toString() {
        return "{" +
                "name='" + name + ", id=" + id +
                ", age=" + age + ", deptId=" + dept_id +
                '}';
    }
}
