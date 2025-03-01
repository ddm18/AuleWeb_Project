package it.univaq.f4i.iw.ex.AuleWeb.data.dao;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Classroom;
import it.univaq.f4i.iw.framework.data.DataException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface ClassroomDAO {

    // Retrieve a single Classroom by its ID
    Classroom getClassroom(int classroomID) throws DataException;

    // Retrieve a list of all Classrooms
    List<Classroom> getClassrooms() throws DataException;

    // Factory method to create an empty Classroom object
    Classroom createClassroom();
    
    

    // Other potential methods for CRUD operations
    void storeClassroom(Classroom classroom) throws DataException;

    void deleteClassroom(int classroomID) throws DataException;

	List<Classroom> getClassroomByDepartment(int departmentId) throws DataException;

	List<Classroom> getClassroomByDepartmentAndDateRange(int departmentId, LocalDate startDate, LocalDate endDate)
			throws DataException;

	Classroom getClassroomByName(String name) throws DataException;
}
