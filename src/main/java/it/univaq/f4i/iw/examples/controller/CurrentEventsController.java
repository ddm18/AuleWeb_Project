package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomGroup;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller to handle current events.
 */
public class CurrentEventsController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        
        // Retrieve department name parameter
        String departmentName = request.getParameter("groupName");
        ClassroomGroup department = dl.getClassroomGroupDAO().getClassroomGroupByName(departmentName);
        List<Event> currentEvents = dl.getEventDAO().getCurrentEvents(); // Assuming you have a method to get current events

        // Set attributes for the template
        request.setAttribute("events", currentEvents);
        request.setAttribute("department", department);
        request.setAttribute("page_title", "Current Events for Department: " + department.getGroupName());

        // Render the partial template if it's an AJAX request
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("show_outline", !"XMLHttpRequest".equals(request.getHeader("X-Requested-With")));

        result.activate("current_events.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
