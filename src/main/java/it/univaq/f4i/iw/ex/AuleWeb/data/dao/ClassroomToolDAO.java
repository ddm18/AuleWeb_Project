package it.univaq.f4i.iw.ex.AuleWeb.data.dao;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomTool;
import it.univaq.f4i.iw.framework.data.DataException;
import java.util.List;

public interface ClassroomToolDAO {

    // Retrieve a single ClassroomTool by its ID
    ClassroomTool getClassroomTool(int classroomToolID) throws DataException;

    // Retrieve all tools for a specific Classroom
    List<ClassroomTool> getToolsByClassroom(int classroomID) throws DataException;

    // Factory method to create an empty ClassroomTool object
    ClassroomTool createClassroomTool() throws DataException;

    // CRUD operations for storing and deleting
    void storeClassroomTool(ClassroomTool classroomTool) throws DataException;

    void deleteClassroomTool(int classroomToolID) throws DataException;

	List<ClassroomTool> getClassroomTools() throws DataException;
}
