package com.student.service;

import com.mojro.collection.ArrayList;
import com.mongodb.MongoException;
import com.student.RepositoryException;
import com.student.RepositoryRuntimeException;
import com.student.store.DepartmentRepository;
import com.student.store.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import com.student.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service class to handle business validations, and it facilitates the interaction between user interface and class which
 * does database operation.
 */
@Slf4j
@Component
public class CollegeService {
    @Autowired
    private  StudentRepository stdRepo;
    @Autowired
    private  DepartmentRepository deptRepo;



                                            //DEPARTMENT OPERATIONS


    /**
     * This method is used to add the new department to the department collection.
     * @return true if student added successfully, false otherwise
     * @throws RepositoryException Exception if the connection is not right
     */
    public boolean addDept(Department dept) throws RepositoryException {
        try{
            if (deptRepo.IsDeptIdNotPresent(dept.getId())){
                log.info("Saving the new department to department list.");
                return deptRepo.save(dept);
            } else {
                return false;
            }
        }catch(RepositoryException e) {
            throw new RepositoryException(e.getMessage(),e);
        }
    }

    /**
     * This method is used to delete the department based on the dept_id
     * @param delid id of the dept to be deleted
     * @return true if deleted,false otherwise
     */
    public boolean deleteDept (int delid) {
        try{
            log.info("deleting the department from  collection");
            return deptRepo.delete(delid);
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is used to delete all the students belongs to particular department before
     * deleting that particular department itself.
     * @param dept_id id of the department
     */
    public void deleteStdDept (int dept_id) {
        try{
            log.info("deleting the students belongs to specific dept");
            stdRepo.deleteStudentOfDept(dept_id);
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is used to return all the documents in the department collection
     * @return list of departments
     * @throws RepositoryException throws exception if the connection not done.
     */
    public ArrayList<Department> viewDept() throws RepositoryException {
        try{
            log.info("Fetching department data from the repository.");
            return deptRepo.view(); // Return the list of students
        }catch (RepositoryException e) {
            throw new RepositoryException(e.getMessage(),e);
        }

    }

    /**
     * This method helps to update the department name by facilitating communication between app and repository.
     */
    public Document updateDeptName(int dept_id, String newName) throws RepositoryException {
        try{
            return deptRepo.updateDeptName(dept_id,newName);
        }catch (MongoException e) {
            throw new RepositoryException(e.getMessage(),e);
        }

    }

    /**
     * This method helps to update the HOD name by facilitating communication between app and repository.
     */
    public Document updateHODName(int dept_id,String newName) {
        try{
            return deptRepo.updateHODName(dept_id,newName);
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is used to check whether the department with dept_id exists or not
     */
    public boolean IsDeptIdNotPresent(int dept_id){
        return deptRepo.IsDeptIdNotPresent(dept_id);
    }




                                    // STUDENT OPERATIONS



    /**
     * This method is to add the new student record.
     * @param student this is student class object
     */
    public boolean addStudent(Student student) throws RepositoryException {
        try{
            if (!deptRepo.IsDeptIdNotPresent(student.dept_id) && stdRepo.IsStudentIdNotPresent(student.id)) {
                log.info("Saving the new student to student list.");
                return stdRepo.Save(student);
            } else {
                if(!deptRepo.IsDeptIdNotPresent(student.dept_id)) {
                    return false;
                }
            }
        }catch(RepositoryException e) {
            throw new RepositoryException("Failed to save the data",e);
        }
        return false;
    }

    /**
     * This method is to delete the student record which takes the id of a student to be deleted.
     */
    public boolean deleteStudent (int delid)
    {
        try{
            log.info("deleting the element from both collection");
            return stdRepo.delete(delid);
        }catch(RepositoryRuntimeException e){
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is to print the data of all students
     */
    public ArrayList<Student> viewStudents() throws RepositoryRuntimeException, RepositoryException {
        try{
            log.info("Fetching student data from the repository.");
            return stdRepo.view(); // Return the list of students
        }catch (RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }catch (RepositoryException e){
            log.info(e.getMessage(),e);
            throw new RepositoryException(e.getMessage(),e);
        }

    }


    /**
     * This method is to return the list of students who belongs to particular department
     * @return list of students
     * @throws RepositoryException throws in connection is not done with database.
     */
    public ArrayList<Student> stuOfDept(int dept_id) throws  RepositoryException {
        log.info("Fetching student record of particular department from the repository.");
        try{
            return stdRepo.viewStudentOfDept(dept_id);
        }catch (Exception e){
            throw new RepositoryException(e.getMessage(),e);
        }

    }

    /**
     * This method helps to update the student name by facilitating communication between app and repository.
     */
    public void updateStdName(int std_id,String newName)
    {
        try{
            stdRepo.updateStdName(std_id,newName);
        }catch(RepositoryRuntimeException e){
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }

    }

    /**
     * This method helps to update the student age by facilitating communication between app and repository.
     */
    public void updateStdAge(int std_id,int newAge)
    {
        stdRepo.updateStdAge(std_id,newAge);
    }

    /**
     * This method helps to update the department id of student  by facilitating communication between app and repository.
     */
    public void updateStdDeptId(int std_id,int newId)
    {
        stdRepo.updateStdDeptId(std_id,newId);
    }

    /**
     * This method is used to check whether the student with student_id exists or not
     */
    public boolean isStdIdNotPresent(int student_id){
        return stdRepo.IsStudentIdNotPresent(student_id);
    }
}


