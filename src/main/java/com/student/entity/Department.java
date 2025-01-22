package com.student.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Entity class representing a department
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Department implements Serializable {
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Field must contain only alphabets and spaces")
    public String name;
    @NotNull(message = "Department id cannot be null")
    public int id;
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Field must contain only alphabets and spaces")
    public String HODName;
    /**
     * Override toString method for easy printing
     * @return string consists of every state of Student class.
     */
    @Override
    public String toString() {
        return "{" + "dept=" + name +  ", deptId=" + id+
                ", HOD name=" + HODName + '}';
    }
}
