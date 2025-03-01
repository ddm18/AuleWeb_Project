package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Course;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class main_adminController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Main Admin Page");
        result.activate("admin_main.ftl.html", request, response);
    }

    private void action_something(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        // Logic for when the first form is submitted
    	ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        System.out.println("Form 1 submitted!");
        String course_name = request.getParameter("new_course_name");
        System.out.println(course_name);        
        Course new_course = dl.getCourseDAO().createCourse();
        new_course.setCourseName(course_name);
        
        if (dl.getCourseDAO().getCourseByName(course_name)==null) {
	        dl.getCourseDAO().storeCourse(new_course);
	        response.sendRedirect("admin_main"); // Redirect to refresh or navigate to a specific page
	        System.out.println("course created");
        }
        else {
        	handleError("Cannot add course:", request, response);
        }
        
    }

    private void action_something2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Logic for when the second form is submitted
        System.out.println("Form 2 submitted!");
        response.sendRedirect("admin_main"); // Redirect to refresh or navigate to a specific page
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Check which form was submitted
        String action = request.getParameter("action");
        if ("something".equals(action)) {
            action_something(request, response);
        } else if ("something2".equals(action)) {
            action_something2(request, response);
        } else {
            action_default(request, response);
        }
    }
}
