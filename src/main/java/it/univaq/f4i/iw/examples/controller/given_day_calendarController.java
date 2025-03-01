package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class given_day_calendarController extends ApplicationBaseController {
    
    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        
        // Get the date parameter and parse it
        String date = request.getParameter("date");
        LocalDate localDate = LocalDate.parse(date);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        
        // Retrieve events by date
        List<Event> events = dl.getEventDAO().getEventsByDate(sqlDate);
        
        // Check if clevents is empty to avoid IndexOutOfBoundsException
        if (events.isEmpty()) {
            request.setAttribute("classroomEventsMap", Map.of());  // Set an empty map if there are no events
        } else {
            // Group events by classroom name if there are events
            Map<String, List<Event>> classroomEventsMap = events.stream()
                    .collect(Collectors.groupingBy(event -> event.getClassroom().getClassroomName()));            
            request.setAttribute("classroomEventsMap", classroomEventsMap);
        }
        
        // Prepare and activate the template
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("show_outline", !"XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
        result.activate("given_day_events.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
