package com.student.entity;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Entity class representing a department
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Department implements Serializable {

    @Pattern(regexp = "[a-zA-Z ]+", message = "Name must contain only alphabets and spaces")
    private String name;

    @Id
    @NotNull(message = "Department ID cannot be null")
    @Positive(message = "Department ID must be a positive number")
    private Integer id;

    @Pattern(regexp = "[a-zA-Z ]+", message = "HOD Name must contain only alphabets and spaces")
    private String hodname;

    /**
     * Override toString method for easy printing
     * @return string consists of every state of Department class.
     */
    @Override
    public String toString() {
        return "{" + "dept=" + name + ", deptId=" + id + ", HOD name=" + hodname + '}';
    }
}
