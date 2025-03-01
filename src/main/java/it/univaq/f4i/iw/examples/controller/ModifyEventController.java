package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.*;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ModifyEventController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        TemplateResult result = new TemplateResult(getServletContext());

        List<Classroom> classrooms = dl.getClassroomDAO().getClassrooms();
        List<Course> courses = dl.getCourseDAO().getCourses();
        List<Manager> eventManagers = dl.getManagerDAO().getManagers();
        List<Type> eventTypes = dl.getTypeDAO().getTypes();
        List<Event> events = dl.getEventDAO().getEvents();
        List<ClassroomGroup> departments = dl.getClassroomGroupDAO().getClassroomGroups();
        
        request.setAttribute("classrooms", classrooms);
        request.setAttribute("courses", courses);
        request.setAttribute("eventManagers", eventManagers);
        request.setAttribute("eventTypes", eventTypes);
        request.setAttribute("events", events);
        request.setAttribute("departments", departments);
        request.setAttribute("page_title", "Manage Events");

        result.activate("modify_event.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            

            String eventName = request.getParameter("event_name");
            if (eventName == null || eventName.trim().isEmpty()) {
                throw new DataException("Event name is required.");
            }

            LocalDate eventDate;
            LocalTime startTime, endTime;
            try {
                eventDate = LocalDate.parse(request.getParameter("event_date"));
                startTime = LocalTime.parse(request.getParameter("start_time"));
                endTime = LocalTime.parse(request.getParameter("end_time"));

                if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                    throw new DataException("Event start time must be before end time.");
                }
            } catch (DateTimeParseException dtpe) {

                throw new DataException("Invalid date or time format: " + dtpe.getParsedString());
            } catch (DataException de) {

                throw de; 
            } catch (Exception ex) {
                throw new DataException("Unexpected error while parsing dates", ex);
            }

            String description = request.getParameter("description");

            int classroomId, eventManagerId, eventTypeId;
            String courseIdStr = request.getParameter("course_id");

            try {
                classroomId = Integer.parseInt(request.getParameter("classroom_id"));
                eventManagerId = Integer.parseInt(request.getParameter("event_manager_id"));
                eventTypeId = Integer.parseInt(request.getParameter("event_type_id"));
            } catch (NumberFormatException ex) {
                throw new DataException("Invalid numeric values for classroom, manager, or type.");
            }

            // Check for overlapping events in the same classroom
            List<Event> existingEvents = dl.getEventDAO().getEventsByClassroomAndDate(classroomId, eventDate);
            for (Event event : existingEvents) {
                if ((startTime.isBefore(event.getEndTime()) && endTime.isAfter(event.getStartTime()))) {
                    throw new DataException("An event already exists in the same classroom during the given time range.");
                }
            }

            // Recurrence parameters
            String recurrence = request.getParameter("event_recurrence");
            String recurrenceEndStr = request.getParameter("recurrence_end");
            LocalDate recurrenceEndDate = null;

            if (recurrence != null && !recurrence.equalsIgnoreCase("False")) {
                try {
                    recurrenceEndDate = LocalDate.parse(recurrenceEndStr);
                } catch (Exception ex) {
                    throw new DataException("Invalid recurrence end date.");
                }
            }


            Integer masterId = null;

            LocalDate currentEventDate = eventDate;
            

            while (currentEventDate != null && (recurrenceEndDate == null || !currentEventDate.isAfter(recurrenceEndDate))) {
            	
                Event newEvent = dl.getEventDAO().createEvent();
                newEvent.setName(eventName);
                newEvent.setDate(currentEventDate);
                newEvent.setStartTime(startTime);
                newEvent.setEndTime(endTime);
                newEvent.setDescription(description);


                try {
                    newEvent.setClassroom(dl.getClassroomDAO().getClassroom(classroomId));
                    newEvent.setEventManager(dl.getManagerDAO().getManager(eventManagerId));
                    newEvent.setEventType(dl.getTypeDAO().getType(eventTypeId));

                    if (courseIdStr != null && !courseIdStr.isEmpty()) {
                        int courseId = Integer.parseInt(courseIdStr);
                        newEvent.setCourse(dl.getCourseDAO().getCourse(courseId));
                    }
                } catch (DataException ex) {
                    throw new DataException("Invalid classroom, manager, type, or course details.");
                }

                // For the first event, set master_id and store it
                if (masterId == null) {
                	
                    dl.getEventDAO().storeEvent(newEvent);

                    if (recurrence != null && !recurrence.equalsIgnoreCase("False")) {
                    	
                        masterId = newEvent.getKey();
                        newEvent.setMasterId(masterId);
                        newEvent.setVersion(newEvent.getVersion() + 1);
                        dl.getEventDAO().storeEvent(newEvent);
                    }
                } else {
                	
                    newEvent.setMasterId(masterId);
                    dl.getEventDAO().storeEvent(newEvent);
                }

                // Update currentEventDate based on recurrence
                if (recurrence != null) {
                    switch (recurrence) {
                        case "Daily":
                            currentEventDate = currentEventDate.plusDays(1);
                            break;
                        case "Weekly":
                            currentEventDate = currentEventDate.plusWeeks(1);
                            break;
                        case "Monthly":
                            currentEventDate = currentEventDate.plusMonths(1);
                            break;
                        default:
                            currentEventDate = null; 
                    }
                } else {
                    currentEventDate = null; 
                }
            }

            response.sendRedirect("modify_event");

        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }






    private void action_modify(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            int eventId = Integer.parseInt(request.getParameter("event_id"));
            String eventName = request.getParameter("event_name");
            if (eventName == null || eventName.trim().isEmpty()) {
                throw new DataException("Event name is required.");
            }

            LocalDate eventDate;
            LocalTime startTime, endTime;
            try {
                eventDate = LocalDate.parse(request.getParameter("event_date"));
                startTime = LocalTime.parse(request.getParameter("start_time"));
                endTime = LocalTime.parse(request.getParameter("end_time"));

                if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                    throw new DataException("Event start time must be before end time.");
                }
            } catch (Exception ex) {
                throw new DataException("Invalid date or time format.");
            }

            String description = request.getParameter("description");

            int classroomId, eventManagerId, eventTypeId;
            String courseIdStr = request.getParameter("course_id");

            try {
                classroomId = Integer.parseInt(request.getParameter("classroom_id"));
                eventManagerId = Integer.parseInt(request.getParameter("event_manager_id"));
                eventTypeId = Integer.parseInt(request.getParameter("event_type_id"));
            } catch (NumberFormatException ex) {
                throw new DataException("Invalid numeric values for classroom, manager, or type.");
            }


            List<Event> allEvents = dl.getEventDAO().getEvents();

            List<Event> existingEvents = dl.getEventDAO().getEventsByClassroomAndDate(classroomId, eventDate);
            for (Event event : existingEvents) {
                if (event.getKey() != eventId && (startTime.isBefore(event.getEndTime()) && endTime.isAfter(event.getStartTime()))) {
                    throw new DataException("An event already exists in the same classroom during the given time range.");
                }
            }

            Event event = dl.getEventDAO().getEvent(eventId);
            event.setName(eventName);
            event.setDate(eventDate);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setDescription(description);
            event.setClassroom(dl.getClassroomDAO().getClassroom(classroomId));
            event.setEventManager(dl.getManagerDAO().getManager(eventManagerId));
            event.setEventType(dl.getTypeDAO().getType(eventTypeId));

            if (courseIdStr != null && !courseIdStr.isEmpty()) {
                int courseId = Integer.parseInt(courseIdStr);
                event.setCourse(dl.getCourseDAO().getCourse(courseId));
            } else {
                event.setCourse(null);
            }

            // Check if the checkbox is checked
            String applyToAll = request.getParameter("recurrence_checkbox");
            if (applyToAll != null) { 
                Integer masterId = event.getMasterId();
                if (masterId != null) {
                    List<Event> recurrentEvents = dl.getEventDAO().getRecurrentEvents(masterId);

                    for (Event recurrentEvent : recurrentEvents) {
                        if (recurrentEvent.getKey() == eventId) {
                            continue; 
                        }

                        recurrentEvent.setName(eventName);
                        recurrentEvent.setDate(recurrentEvent.getDate());
                        recurrentEvent.setStartTime(recurrentEvent.getStartTime());
                        recurrentEvent.setEndTime(recurrentEvent.getEndTime());
                        recurrentEvent.setDescription(description);
                        recurrentEvent.setClassroom(dl.getClassroomDAO().getClassroom(classroomId));
                        recurrentEvent.setEventManager(dl.getManagerDAO().getManager(eventManagerId));
                        recurrentEvent.setEventType(dl.getTypeDAO().getType(eventTypeId));

                        if (courseIdStr != null && !courseIdStr.isEmpty()) {
                            int courseId = Integer.parseInt(courseIdStr);
                            recurrentEvent.setCourse(dl.getCourseDAO().getCourse(courseId));
                        } else {
                            recurrentEvent.setCourse(null);
                        }

                        dl.getEventDAO().storeEvent(recurrentEvent);
                    }
                }
            } else {
                
                event.setMasterId(null);
            }

            dl.getEventDAO().storeEvent(event);

            response.sendRedirect("modify_event");
        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }




    private void action_delete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        
        int eventId = Integer.parseInt(request.getParameter("event_id"));
        String applyToAll = request.getParameter("recurrence_checkbox_delete"); // Checkbox parameter name
        if (applyToAll != null) { 
        	dl.getEventDAO().deleteEventAndRecurrence(eventId);
        }
        else {
        dl.getEventDAO().deleteEvent(eventId);
        }

        response.sendRedirect("modify_event");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_event":
                    action_add(request, response);
                    break;
                case "modify_event":
                    action_modify(request, response);
                    break;
                case "delete_event":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
