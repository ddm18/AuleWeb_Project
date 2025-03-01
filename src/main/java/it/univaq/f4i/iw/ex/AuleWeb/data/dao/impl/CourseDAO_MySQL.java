package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.CourseDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Course;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.CourseProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO_MySQL extends DAO implements CourseDAO {

    private PreparedStatement sCourseByID, sCourses, sCoursesByName, uCourse, iCourse, dCourse;
    private PreparedStatement sCoursesByDepartment, sCoursesByDepartmentAndDateRange;

    public CourseDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Precompile SQL statements
            sCourseByID = connection.prepareStatement("SELECT * FROM course WHERE id = ?");
            sCourses = connection.prepareStatement("SELECT id FROM course");
            sCoursesByName = connection.prepareStatement("SELECT id FROM course WHERE course_name = ?");
            uCourse = connection.prepareStatement(
                "UPDATE course SET course_name = ?, version = ? WHERE id = ? AND version = ?"
            );
            iCourse = connection.prepareStatement(
                "INSERT INTO course (course_name, version) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            dCourse = connection.prepareStatement("DELETE FROM course WHERE id = ?");

         // Courses by Classroom Group (Department)
            sCoursesByDepartment = connection.prepareStatement(
                "SELECT DISTINCT c.* FROM course c " +
                "JOIN event e ON c.id = e.course_id " +
                "JOIN classroom cl ON e.classroom_id = cl.id " +
                "WHERE cl.group_id = ?"
            );

            // Courses by Classroom Group (Department) and Date Range
            sCoursesByDepartmentAndDateRange = connection.prepareStatement(
                "SELECT DISTINCT c.* FROM course c " +
                "JOIN event e ON c.id = e.course_id " +
                "JOIN classroom cl ON e.classroom_id = cl.id " +
                "WHERE cl.group_id = ? AND e.event_date BETWEEN ? AND ?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing CourseDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sCourseByID != null) sCourseByID.close();
            if (sCourses != null) sCourses.close();
            if (sCoursesByName != null) sCoursesByName.close();
            if (uCourse != null) uCourse.close();
            if (iCourse != null) iCourse.close();
            if (dCourse != null) dCourse.close();

            // New prepared statements cleanup
            if (sCoursesByDepartment != null) sCoursesByDepartment.close();
            if (sCoursesByDepartmentAndDateRange != null) sCoursesByDepartmentAndDateRange.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in CourseDAO", ex);
        }
        super.destroy();
    }

    @Override
    public Course createCourse() {
        return new CourseProxy(getDataLayer());
    }

    private CourseProxy createCourse(ResultSet rs) throws DataException {
        CourseProxy course = (CourseProxy) createCourse();
        try {
            course.setKey(rs.getInt("id"));
            course.setCourseName(rs.getString("course_name"));
            course.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Course from ResultSet", ex);
        }
        return course;
    }

    @Override
    public Course getCourse(int courseID) throws DataException {
        Course course = null;
        if (dataLayer.getCache().has(Course.class, courseID)) {
            course = dataLayer.getCache().get(Course.class, courseID);
        } else {
            try {
                sCourseByID.setInt(1, courseID);
                try (ResultSet rs = sCourseByID.executeQuery()) {
                    if (rs.next()) {
                        course = createCourse(rs);
                        dataLayer.getCache().add(Course.class, course);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load Course by ID", ex);
            }
        }
        return course;
    }

    @Override
    public Course getCourseByName(String courseName) throws DataException {
        Course course = null;
        try {
            sCoursesByName.setString(1, courseName);
            try (ResultSet rs = sCoursesByName.executeQuery()) {
                if (rs.next()) {
                    course = getCourse(rs.getInt("id"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load course by name: " + courseName, ex);
        }
        return course;
    }

    @Override
    public List<Course> getCourses() throws DataException {
        List<Course> courses = new ArrayList<>();
        try (ResultSet rs = sCourses.executeQuery()) {
            while (rs.next()) {
                courses.add(getCourse(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Courses", ex);
        }
        return courses;
    }

    @Override
    public void storeCourse(Course course) throws DataException {
        try {
            if (course.getKey() != null && course.getKey() > 0) { // Update existing course
                if (course instanceof DataItemProxy && !((DataItemProxy) course).isModified()) {
                    return;
                }

                uCourse.setString(1, course.getCourseName());
                long currentVersion = course.getVersion();
                long nextVersion = currentVersion + 1;

                uCourse.setLong(2, nextVersion);
                uCourse.setInt(3, course.getKey());
                uCourse.setLong(4, currentVersion);

                if (uCourse.executeUpdate() == 0) {
                    throw new OptimisticLockException(course);
                } else {
                    course.setVersion(nextVersion);
                }
            } else { // Insert new course
                iCourse.setString(1, course.getCourseName());
                iCourse.setLong(2, 1); // Initial version

                if (iCourse.executeUpdate() == 1) {
                    try (ResultSet keys = iCourse.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            course.setKey(key);
                            dataLayer.getCache().add(Course.class, course);
                        }
                    }
                }
            }

            if (course instanceof DataItemProxy) {
                ((DataItemProxy) course).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store course", ex);
        }
    }

    @Override
    public void deleteCourse(int courseID) throws DataException {
        try {
            dCourse.setInt(1, courseID);
            dCourse.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete course", ex);
        }
    }

    @Override
    public List<Course> getCoursesByDepartment(int departmentId) throws DataException {
        List<Course> courses = new ArrayList<>();
        try {
            sCoursesByDepartment.setInt(1, departmentId);
            try (ResultSet rs = sCoursesByDepartment.executeQuery()) {
                while (rs.next()) {
                    courses.add(createCourse(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load courses by department ID", ex);
        }
        return courses;
    }

    @Override
    public List<Course> getCoursesByDepartmentAndDateRange(int departmentId, java.sql.Date startDate, java.sql.Date endDate) throws DataException {
        List<Course> courses = new ArrayList<>();
        try {
            sCoursesByDepartmentAndDateRange.setInt(1, departmentId);
            sCoursesByDepartmentAndDateRange.setDate(2, startDate);
            sCoursesByDepartmentAndDateRange.setDate(3, endDate);

            try (ResultSet rs = sCoursesByDepartmentAndDateRange.executeQuery()) {
                while (rs.next()) {
                    courses.add(createCourse(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load courses by department and date range", ex);
        }
        return courses;
    }
}
