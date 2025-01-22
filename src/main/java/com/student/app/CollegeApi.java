/**
 * This is 3 layered architecture used for saving student and department information
 * the first layer is CollegeApp which will understand what user is wanting to do.
 * the second layer Service connects the app and repository layer by transferring the input data.
 * the third layer Repository will connect with database and is responsible for the modification in the database.
 */
package com.student.app;
import com.mojro.collection.ArrayList;
import com.student.RepositoryException;
import com.student.RepositoryRuntimeException;
import java.util.*;
import com.student.service.CollegeService;
import lombok.extern.slf4j.Slf4j;
import com.student.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This class is used to take the choice of the user and the input from the user
*/
@RestController
@Slf4j
@RequestMapping("/api")
public class CollegeApi {
    Scanner sc = new Scanner(System.in);
    @Autowired
    private  CollegeService service;
    /**
     * This method is to create the new department
     */
    @PostMapping("/departments")
    public ResponseEntity<Object> createDept(@Valid  @RequestBody Department dept) {
        try {
            /**
             * only if it is not null and valid trigger the save method else send the error with responseEntity which field is not valid
              */

            if (service.deptAdd(dept)) {
                return ResponseEntity.status(HttpStatus.OK).body(dept);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Already there is the department with id:" + dept);
            }
        } catch (Exception e) {
            log.error("Error in adding the department details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is used to delete the department
     */
    @DeleteMapping("/departments/{deptId}")
    public  ResponseEntity<Object> deleteDept(@PathVariable int deptId)
    {
        // deleting the students belonging to particular department from student collection
        try{
            service.deleteStdDept(deptId);
        }catch (RepositoryRuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while deleting the students belonging to department with id="+deptId);
        }
        // deleting the department from dept collection
        try{
            if(service.deleteDept(deptId))
            {
                return ResponseEntity.status(HttpStatus.OK).body("Students belonging to department and department itself having DeptID="+deptId+"is successfully deleted");
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete department.");
            }
        }catch(RepositoryRuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while deleting the department with id="+deptId);
        }
    }

    /**
     * This method is used to print all the existing departments
     */
    @GetMapping("/departments")
    public ResponseEntity<Object> readDept() {
        ArrayList<Department> departments;
        try {
            departments = service.viewDept();
            return ResponseEntity.status(HttpStatus.OK).body(departments);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while reading the departments");
        }
    }

    /**
     * This method is used to print all the students belongs to particular department
     */
    @GetMapping("/stdOfDept/{dept_id}")
    public  ResponseEntity<Object> readStudentOfParticularDept(@RequestBody int dept_id){
        ArrayList<Student> list=null;
        if(service.IsDeptIdNotPresent(dept_id))
        {
            try{
                list=service.StuOfDept(dept_id);
                if (list.getSize()==0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no departments to display");
                }
                return ResponseEntity.status(HttpStatus.OK).body(list);
            }catch(Exception e)
            {
                log.info(e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while processing the request.");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no department with id="+dept_id);
        }
    }

    /**
     * This method is used to update the department name
     */
    @PutMapping("/{id}")
    public  ResponseEntity<Object> updateDeptName(@PathVariable int id,@RequestBody Department dept) {
        try {
            service.updateDeptName(dept.id, dept.name);
            return ResponseEntity.status(HttpStatus.OK).body(dept);
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is used to update the HOD name of the department
     */
    @PutMapping("/hod/{id}")
    public  ResponseEntity<Object> updateHODName(@PathVariable int id,@RequestBody Department dept)
    {
        try{
            service.updateHODName(dept.id,dept.HODName);
            return ResponseEntity.status(HttpStatus.OK).body(dept);
        }catch(RepositoryRuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    //            STUDENT OPERATIONS BEGINS HERE

    /**
     * This method is to take all the information from the student and then creating the student object of the same information.
     */
    @PostMapping("/student")
    public  ResponseEntity<Object> create(@RequestBody Student student) {
    log.info("Collecting the data of the student from the user");
        try {
            if (service.add(student)) {
                return ResponseEntity.status(HttpStatus.OK).body(student);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is some issue in creating a student record");
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is used to delete the student based on their id.
     */
    @DeleteMapping("/student/{delid}")
    private  ResponseEntity<Object> delete(@PathVariable int delid) {
        try{
            if(service.deleteStudent(delid))
            {
                return ResponseEntity.status(HttpStatus.OK).body("The student with id "+ delid+" is deleted");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no student with id="+delid);
        }catch(RepositoryRuntimeException e)
        {
            log.error(e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is to read all the student record saved in the database.
     */
    @GetMapping("/student")
    private  ResponseEntity<Object> read() {
        ArrayList<Student> students;
        try{
            students = service.view();
            if (students.getSize()==0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no students to display");
            }
            return ResponseEntity.status(HttpStatus.OK).body(students);
        }catch (Exception e)
        {
            log.error(e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is used to update the student name
     */
    @PutMapping("/student/name/{std_id}")
    public  ResponseEntity<Object> updateStdName(@PathVariable int std_id,@RequestBody Student student) {
        if (service.IsStdIdNotPresent(std_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There  is no student with the id="+std_id);
        } else {
            try{
                service.updateStdName(std_id, student.name);
                return ResponseEntity.status(HttpStatus.OK).body(student);
            }catch(RepositoryRuntimeException e)
            {
                log.error(e.getMessage(),e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred while processing the request.");
            }
        }
    }

    /**
     * This method is used to update the student age
     */
    @PutMapping("/Student/age/{std_id}")
    public  ResponseEntity<Object> updateStdAge(@PathVariable int std_id,@RequestBody Student student) {
        if (service.IsStdIdNotPresent(std_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There  is no student with the id="+std_id);
        } else {
            try{
                service.updateStdAge(std_id, student.age);
                return ResponseEntity.status(HttpStatus.OK).body(student);
            }catch(RepositoryRuntimeException e)
            {
                log.error(e.getMessage(),e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred while processing the request.");
            }
        }
    }

    /**
     * This method is used to update the department I'd of a student.
     */
    @PutMapping("/Student/deptId/{std_id}")
    public  ResponseEntity<Object> updateStdDeptId(@PathVariable int std_id,@RequestBody Student student) {
        if (service.IsStdIdNotPresent(std_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There  is no student with the id="+std_id);
        } else {
            try{
                service.updateStdDeptId(std_id, student.dept_id);
                return ResponseEntity.status(HttpStatus.OK).body(student);
            }catch(RepositoryRuntimeException e)
            {
                log.error(e.getMessage(),e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred while processing the request.");
            }
        }
    }
}


