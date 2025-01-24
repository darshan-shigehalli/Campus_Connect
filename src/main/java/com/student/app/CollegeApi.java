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
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * This class is used to take the choice of the user about the actions they wanted to perform and the input from the user.
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
    @PostMapping("/departments/create")
    public ResponseEntity<Object> createDept(@Valid  @RequestBody Department dept) {
        try {
            if (service.addDept(dept)) {
                log.info("new department added");
                return ResponseEntity.status(HttpStatus.OK).body(dept);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already there is the department with id:" + dept.getId());
        } catch (Exception e) {
            log.error("Error in adding the department details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is used to delete the department
     */
    @DeleteMapping("/departments/{deptId}")
    public  ResponseEntity<Object> deleteDept(@PathVariable int deptId)
    {
        // Initially deleting the students belonging to particular department from student collection
        try{
            service.deleteStdDept(deptId);
            log.info("The students belong to the department id ={} is deleted successfully", deptId);
        }catch (RepositoryRuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while deleting the students belonging to department with id="+deptId);
        }
        // deleting the department from dept collection
        try{
            if(service.deleteDept(deptId))
            {
                return ResponseEntity.status(HttpStatus.OK).body("Students belonging to department and department itself having DeptID="+deptId+"is successfully deleted");
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no department with the id="+deptId+" in the department collection.");
            }
        }catch(RepositoryRuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while deleting the department with id="+deptId);
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
            StringBuilder str= new StringBuilder();
            for(int i=0;i<departments.getSize();i++)
            {
                str.append(departments.get(i));
            }
            log.info("Successfully fetched departments: {}", departments);
            return ResponseEntity.status(HttpStatus.OK).body(str.toString());

        } catch (Exception e) {
            log.error("An error occurred while reading departments: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while reading the departments");
        }
    }

    /**
     * This method is used to print all the students belongs to particular department
     */
    @GetMapping("/departments/students/{dept_id}")
    public  ResponseEntity<Object> readStudentOfParticularDept(@PathVariable int dept_id){
        ArrayList<Student> list=null;
        if(!service.IsDeptIdNotPresent(dept_id))
        {
            try{
                list=service.stuOfDept(dept_id);
                if (list.getSize()==0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no departments to display");
                }
                StringBuilder str= new StringBuilder();
                for(int i=0;i<list.getSize();i++)
                {
                    str.append(list.get(i));
                }
                return ResponseEntity.status(HttpStatus.OK).body(str);
            }catch(Exception e) {
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
    @PutMapping("/departments/update")
    public  ResponseEntity<Object> updateDeptName(@RequestBody Department dept) {
        if (service.IsDeptIdNotPresent(dept.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There  is no department with the id="+dept.getId());
        } else {
            try {
                Document doc=service.updateDeptName(dept.getId(), dept.getName());
                return ResponseEntity.status(HttpStatus.OK).body(doc);
            } catch (RepositoryException e) {
                log.error(e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while processing the request.");
            }
        }
    }

    /**
     * This method is used to update the HOD name of the department.
     */
    @PutMapping("/departments/update/hod")
    public  ResponseEntity<Object> updateHODName(@RequestBody Department dept)
    {
        if (service.IsDeptIdNotPresent(dept.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There  is no department with the id="+dept.getId());
        } else {
            try{
                Document doc=service.updateHODName(dept.getId(),dept.getHodname());
                return ResponseEntity.status(HttpStatus.OK).body(doc);
            }catch(RepositoryRuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred while processing the request.");
            }
        }
    }


    //                                      STUDENT OPERATIONS BEGINS HERE


    /**
     * This method is to take all the information from the student and then creating the student object of the same information.
     */
    @PostMapping("/students")
    public  ResponseEntity<Object> create(@Valid @RequestBody Student student) {
    log.info("Collecting the data of the student from the user");
        try {
            if (service.addStudent(student)) {
                return ResponseEntity.status(HttpStatus.OK).body(student);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("It seems you have entered wrong student Id or wrong department Id");
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is used to delete the student based on their id.
     */
    @DeleteMapping("/students/{delid}")
    private  ResponseEntity<Object> delete(@PathVariable int delid) {
        try{
            if(service.deleteStudent(delid))
            {
                return ResponseEntity.status(HttpStatus.OK).body("The student with id "+ delid+" is deleted");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no student with id="+delid);
        }catch(RepositoryRuntimeException e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is to read all the student record saved in the database.
     */
    @GetMapping("/students")
    private  ResponseEntity<Object> read() {
        ArrayList<Student> students;
        try{
            students = service.viewStudents();
            if (students.getSize()==0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There are no students to display");
            }
            StringBuilder str= new StringBuilder();
            for(int i=0;i<students.getSize();i++)
            {
                str.append(students.get(i));
            }
            return ResponseEntity.status(HttpStatus.OK).body(str);
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred while processing the request.");
        }
    }

    /**
     * This method is used to update the student name
     */
    @PutMapping("/students/name/{std_id}")
    public  ResponseEntity<Object> updateStdName(@PathVariable int std_id,@RequestBody Student student) {
        if (service.isStdIdNotPresent(std_id)) {
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
    @PutMapping("/students/age/{std_id}")
    public  ResponseEntity<Object> updateStdAge(@PathVariable int std_id,@RequestBody Student student) {
        if (service.isStdIdNotPresent(std_id)) {
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
    @PutMapping("/students/deptId/{std_id}")
    public  ResponseEntity<Object> updateStdDeptId(@PathVariable int std_id,@RequestBody Student student) {
        if (service.isStdIdNotPresent(std_id)) {
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


