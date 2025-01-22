package com.student.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.student.RepositoryException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojro.collection.ArrayList;
import com.student.entity.*;
import com.mongodb.client.result.DeleteResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.RepositoryRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * This class is to perform the CRUD operations with student collection. It will exclusively interact with database and
 * does all the action which are initiated.
 */
@Repository
public class StudentRepository{
    private static final Logger logger = LoggerFactory.getLogger(StudentRepository.class);
    @Autowired
    private MongoClient mongoClient;  //injecting it from IOC container
    @Autowired
    private MongoDatabase database;   //injecting it from IOC container
    private MongoCollection<Document> collection;

    /**
     *This is the method to choose the correct collection to perform the action before the bean of this class is being used.
     */
    @PostConstruct
    public void init(){
        collection = database.getCollection("studentCollection");
    }

    /**
     * This method is used to read all the entries from the database.
     * @return list of student class objects
     */
    public ArrayList<Student> viewStudents() throws RepositoryRuntimeException, RepositoryException {
        logger.info("Fetching all student records...");
        ArrayList<Student> studentList = new ArrayList<>();
        try {
            MongoCursor<Document> cursor = collection.find().iterator();
            ObjectMapper objectMapper = new ObjectMapper();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String json = document.toJson();
                Student student = objectMapper.readValue(json, Student.class);
                studentList.add(student);
            }
        }
        catch (MongoException e) {
            throw new MongoException(e.getMessage(),e);
        } catch (JsonProcessingException e) {
            throw new RepositoryException(e.getMessage(),e);
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
        return studentList;
    }

    /**
     * This method is to return the lst of students who belongs to same department
     * @return list of students
     */
    public ArrayList<Student> viewStudentOfDept(int dept_id) throws RepositoryRuntimeException, RepositoryException {
        ArrayList<Student> list = new ArrayList<>();
        Document query = new Document("dept_id",dept_id);
        try {
            MongoCursor<Document> cursor = collection.find(query).iterator();
            ObjectMapper objectMapper = new ObjectMapper();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String json = document.toJson();
                Student dept = objectMapper.readValue(json, Student.class);
                list.add(dept);
            }
        }
        catch (MongoException e) {
            logger.error("MongoDB error: {}", e.getMessage(), e);
            throw new RepositoryException(e.getMessage(),e);
        }
        catch (JsonProcessingException e) {
            logger.error("JSON processing error: {}", e.getMessage(), e);
            throw new RepositoryException(e.getMessage(),e);
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
        return list;
    }

    /**
     * This method is to add the new student data
     * @param student this is student class object
     */
    public boolean Save(Student student) throws RepositoryRuntimeException, RepositoryException {
        logger.info("Adding new student");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(student);
            Document jsonDocument = Document.parse(jsonString);
            collection.insertOne(jsonDocument);
            return true;
        } catch (JsonProcessingException e) {
            throw new RepositoryException(e.getMessage(),e);
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * Checking the id given is existing in the database or not.
     * @param id this is id to check the uniqueness
     * @return true if id is not present,false if id is present
     */
    public boolean IsStudentIdNotPresent(int id) {
        try{
            logger.info("Checking id is unique or not");
            Document query = new Document("id", id);
            return collection.find(query).first() == null;
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is to delete the specific student record using id
     * @param id this is id to delete the student
     * @return return true if document is deleted or false is not even one document is deleted.
     */
    public boolean delete(int id)
    {
        try{
            logger.info("Deleting the student record");
            Document query = new Document("id",id);
            DeleteResult result = collection.deleteOne(query);
            return result.getDeletedCount() != 0;
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }

    }

    /**
     * this method is used for deleting all the students of a dept before deleting dept itself
     */
    public  void deleteStudentOfDept(int dept_id)
    {
        try{
            logger.info("Deleting the students with matching dept");
            Document query = new Document("dept_id",dept_id);
            DeleteResult result = collection.deleteMany(query);
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }

    }

    /**
     * This method helps to update the student name.
     */
    public void updateStdName(int std_id,String newName)
    {
        try{
            Document filter = new Document("id", std_id);
            Document update = new Document("$set", new Document("name", newName));
            var result = collection.updateOne(filter, update);
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method helps to update the student age.
     */
    public void updateStdAge(int std_id,int newAge)
    {
        try{
            Document filter = new Document("id", std_id);
            Document update = new Document("$set", new Document("age", newAge));
            var result = collection.updateOne(filter, update);
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method helps to update the department id of student.
     */
    public void updateStdDeptId(int std_id,int newId)
    {
        try{
            Document filter = new Document("id", std_id);
            Document update = new Document("$set", new Document("dept_id", newId));
            var result = collection.updateOne(filter, update);
        }catch(RepositoryRuntimeException e)
        {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }

    }
}