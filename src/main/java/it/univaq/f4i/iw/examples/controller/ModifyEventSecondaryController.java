package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.*;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ModifyEventSecondaryController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateManagerException, DataException {
    	
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        TemplateResult result = new TemplateResult(getServletContext());


        
        String startDateParam = request.getParameter("start_date");
        String endDateParam = request.getParameter("end_date");
        String departmentName = request.getParameter("department_name");
        LocalDate startDate;
        LocalDate endDate;
        startDate = LocalDate.parse(startDateParam);
        endDate = LocalDate.parse(endDateParam);


        ClassroomGroup department = dl.getClassroomGroupDAO().getClassroomGroupByName(departmentName);
        int dep_key = department.getKey();

        
        List<Classroom> classrooms = dl.getClassroomDAO().getClassroomByDepartment(dep_key);
        List<Course> courses = dl.getCourseDAO().getCoursesByDepartment(dep_key);
        List<Manager> eventManagers = dl.getManagerDAO().getManagers();
        List<Type> eventTypes = dl.getTypeDAO().getTypes();
        List<Event> events = dl.getEventDAO().getEventsByClassroomGroupAndDateRange(dep_key, startDate, endDate);
        List<ClassroomGroup> departments = dl.getClassroomGroupDAO().getClassroomGroups();

        
        
        // Set attributes for the template
        request.setAttribute("classrooms", classrooms);
        request.setAttribute("courses", courses);
        request.setAttribute("eventManagers", eventManagers);
        request.setAttribute("eventTypes", eventTypes);
        request.setAttribute("events", events);
        request.setAttribute("departments", departments);
        request.setAttribute("page_title", "Manage Events");
        request.setAttribute("show_outline", false);

        // Render the result
        result.activate("modify_event_secondary.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
