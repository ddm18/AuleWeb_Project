package it.univaq.f4i.iw.ex.AuleWeb.data.dao;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Course;
import it.univaq.f4i.iw.framework.data.DataException;

import java.sql.Date;
import java.util.List;

public interface CourseDAO {

    // Retrieve a single Course by its ID
    Course getCourse(int courseID) throws DataException;

    // Retrieve a list of all Courses
    List<Course> getCourses() throws DataException;

    // Factory method to create an empty Course object
    Course createCourse();
    
    // CRUD operations for storing and deleting
    void storeCourse(Course course) throws DataException;

    void deleteCourse(int courseID) throws DataException;

	Course getCourseByName(String courseName) throws DataException;

	List<Course> getCoursesByDepartmentAndDateRange(int departmentId, Date startDate, Date endDate)
			throws DataException;

	List<Course> getCoursesByDepartment(int departmentId) throws DataException;
}
