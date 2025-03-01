package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.*;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PublicController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException, TemplateManagerException {
        // Get the data layer
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        // Retrieve all models data
        List<Classroom> allClassrooms = dl.getClassroomDAO().getClassrooms();
        List<Event> allEvents = dl.getEventDAO().getThisWeekEvents(0);
        List<Tool> allTools = dl.getToolDAO().getTools();
        List<Type> allTypes = dl.getTypeDAO().getTypes();
        List<Manager> allManagers = dl.getManagerDAO().getManagers();
        List<Location> allLocations = dl.getLocationDAO().getLocations();
        List<Course> allCourses = dl.getCourseDAO().getCourses();
        List<ClassroomGroup> allClassroomGroups = dl.getClassroomGroupDAO().getClassroomGroups();

        // Set attributes for the template
        request.setAttribute("classrooms", allClassrooms);
        request.setAttribute("events", allEvents);
        request.setAttribute("tools", allTools);
        request.setAttribute("types", allTypes);
        request.setAttribute("managers", allManagers);
        request.setAttribute("locations", allLocations);
        request.setAttribute("courses", allCourses);
        request.setAttribute("classroomGroups", allClassroomGroups);

        // Debug prints to console
        System.out.println("Data fetched successfully for all models");

        // Set up the template result
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "All Models Test");
        
        // Render the template
        result.activate("classrooms.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
