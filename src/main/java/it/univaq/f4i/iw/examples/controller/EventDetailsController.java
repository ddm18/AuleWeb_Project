package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EventDetailsController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Retrieve and validate numeric input
            String eventIdString = request.getParameter("eventId");
            int eventId = SecurityHelpers.checkNumeric(eventIdString);

            // Retrieve other parameters
            String groupName = request.getParameter("groupName");   // For group name
            String course = request.getParameter("course");         // For course (if present)
            String classroom = request.getParameter("classroom");   // For classroom (if present)
            String week = request.getParameter("week");

            Event event = dl.getEventDAO().getEvent(eventId);

            String baseUrl = "http://localhost:8080/Java_Example_BaseApplicationMVC/department";

            // Rebuild query parameters dynamically
            StringBuilder urlBuilder = new StringBuilder(baseUrl);
            urlBuilder.append("?groupName=").append(URLEncoder.encode(groupName, "UTF-8"));
            urlBuilder.append("&week=").append(URLEncoder.encode(week, "UTF-8"));

            if (classroom != null) {
                urlBuilder.append("&classroom_name=").append(URLEncoder.encode(classroom, "UTF-8"));
            }

            if (course != null) {
                urlBuilder.append("&course=").append(URLEncoder.encode(course, "UTF-8"));
            }

            // Constructed URL
            String rebuiltUrl = urlBuilder.toString();

            TemplateResult result = new TemplateResult(getServletContext());
            request.setAttribute("event", event);
            request.setAttribute("department", groupName);
            request.setAttribute("BackURL", rebuiltUrl);
            request.setAttribute("page_title", "Event_Details");
            result.activate("event-details.ftl.html", request, response);

        } catch (NumberFormatException ex) {
            handleError("Invalid numeric input for event ID", request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
