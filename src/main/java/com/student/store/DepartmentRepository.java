package com.student.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import com.mojro.collection.ArrayList;
import com.student.entity.*;
import com.mongodb.client.result.DeleteResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.RepositoryException;
import com.student.RepositoryRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This class is for performing CRUD operations on  department collection.
 */
@Component
@Slf4j
public class DepartmentRepository{
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @PostConstruct
    public void init(){
        collection = database.getCollection("Department_Collection");
    }

    /**
     * This method returns the list of all departments in the dept collection
     * @return list of departments
     */
    public ArrayList<Department> view() throws RepositoryException {
        log.info("Fetching all department records...");
        ArrayList<Department> deptList = new ArrayList<>();
        try(MongoCursor<Document> cursor = collection.find().iterator()) {
            ObjectMapper objectMapper = new ObjectMapper();
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String json = document.toJson();
                Department dept = objectMapper.readValue(json, Department.class);
                deptList.add(dept);
            }
        }
        catch (MongoException e) {
            log.error(e.getMessage(), e);
            throw new RepositoryException(e.getMessage(),e);
        } catch (JsonProcessingException e) {
            log.error("JSON processing error: {}", e.getMessage(), e);
            throw new RepositoryException(e.getMessage(),e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            throw new RepositoryException(e.getMessage(),e);
        }
        return deptList;
    }

    /**
     * This method is used to save the department details under department collection.
     * @return ture if successfully student is added false otherwise.
     */
    public boolean save(Department dept) throws RepositoryRuntimeException, RepositoryException {
        log.info("Adding new department");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(dept);
            Document jsonDocument = Document.parse(jsonString);
            collection.insertOne(jsonDocument);
            return true;
        } catch (JsonProcessingException e) {
            throw new RepositoryException(e.getMessage(),e);
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is used to check whether the department with given ID exists or not
     * @return true if dept exists false otherwise
     */
    public boolean IsDeptIdNotPresent(int id)  throws RepositoryRuntimeException {
        try{
            log.info("Checking dept id is present or not");
            Document query = new Document("id", id);
            return collection.find(query).first() == null;
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is used to delete the department from department collection
     */
    public boolean delete(int id)  throws RepositoryRuntimeException
    {
        try{
            log.info("Deleting the  department from collection");
            Document query = new Document("id",id);
            DeleteResult result = collection.deleteOne(query);
            return result.getDeletedCount() != 0;
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }

    /**
     * This method is used to update the name of the existing department
     */
    public Document updateDeptName(int dept_id,String newName)  throws RepositoryRuntimeException
    {
        try{
            Document filter = new Document("id", dept_id);
            Document update = new Document("$set", new Document("name", newName));
            var result = collection.updateOne(filter, update);
            Document query = new Document("id", dept_id);
            return collection.find(query).first();
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }

    }

    /**
     * This method is used to update the HOD name of the existing department
     */
    public Document updateHODName(int dept_id,String newName) throws RepositoryRuntimeException
    {
        try{
            Document filter = new Document("id", dept_id);
            Document update = new Document("$set", new Document("hodname", newName));
            var result = collection.updateOne(filter, update);
            Document query = new Document("id", dept_id);
            return collection.find(query).first();
        }catch(RepositoryRuntimeException e) {
            throw new RepositoryRuntimeException(e.getMessage(),e);
        }
    }
}
