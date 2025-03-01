package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Course;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyCourseController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        // Retrieve all courses to display
        List<Course> courses = dl.getCourseDAO().getCourses(); // Assumes getCourses() is implemented
        request.setAttribute("courses", courses);
        
        request.setAttribute("page_title", "Manage Courses");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_course.ftl.html", request, response);
    }

    private void action_record(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        try {
            String courseName = request.getParameter("new_course_name");


            if (courseName == null || courseName.trim().isEmpty()) {
                throw new DataException("Course name cannot be empty.");
            }


            if (dl.getCourseDAO().getCourseByName(courseName) != null) {
                throw new DataException("A course with the name '" + courseName + "' already exists.");
            }


            Course newCourse = dl.getCourseDAO().createCourse();
            newCourse.setCourseName(courseName);
            dl.getCourseDAO().storeCourse(newCourse);

            // Redirect to refresh the list of courses
            response.sendRedirect("modify_course");
        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_update(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters
            int courseId = Integer.parseInt(request.getParameter("course_id"));
            String updatedCourseName = request.getParameter("updated_course_name");

            // Validate the updated course name
            if (updatedCourseName == null || updatedCourseName.trim().isEmpty()) {
                throw new DataException("Updated course name cannot be empty.");
            }

            // Check for duplicate course names (excluding the current course)
            Course existingCourse = dl.getCourseDAO().getCourseByName(updatedCourseName);
            if (existingCourse != null && existingCourse.getKey() != courseId) {
                throw new DataException("A course with the name '" + updatedCourseName + "' already exists.");
            }

            // Retrieve the existing course
            Course course = dl.getCourseDAO().getCourse(courseId);
            if (course == null) {
                throw new DataException("The specified course does not exist.");
            }

            // Update and store the course
            course.setCourseName(updatedCourseName);
            dl.getCourseDAO().storeCourse(course);

            // Redirect to refresh the list of courses
            response.sendRedirect("modify_course");

        } catch (DataException ex) {
            // Handle validation or database errors
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            // Handle invalid numeric input
            handleError("Invalid numeric input while updating the course", request, response);
        } catch (Exception ex) {
            // Handle unexpected errors
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        int courseId = Integer.parseInt(request.getParameter("course_id"));

        // Delete the course
        dl.getCourseDAO().deleteCourse(courseId); // Assumes deleteCourse() is implemented

        // Redirect to refresh the list of courses
        response.sendRedirect("modify_course");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "record_course":
                    action_record(request, response);
                    break;
                case "update_course":
                    action_update(request, response);
                    break;
                case "delete_course":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
