package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Classroom;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Course;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.security.SecurityHelpers;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Controller for handling the department calendar.
 */
public class Dep_calendarController extends ApplicationBaseController {

    private static class RequestParams {
        String actualClassroom;
        String course;
        String departmentName;
        int weekCounter;
        List<Event> events;

        RequestParams(HttpServletRequest request, ApplicationDataLayer dl) throws DataException {
            actualClassroom = request.getParameter("classroom_name");
            course = request.getParameter("course");
            departmentName = request.getParameter("groupName");

            String weekCounterString = request.getParameter("week");
            weekCounter = SecurityHelpers.checkNumeric(weekCounterString);

            List<Course> allCourses = dl.getCourseDAO().getCourses();
            List<Classroom> allClassrooms = dl.getClassroomDAO().getClassrooms();

            if (checkUrlParam(course)) {
                events = dl.getEventDAO().getThisWeekEventsByCourseAndGroup(weekCounter, course,departmentName);
            } else {
                if (!checkUrlParam(actualClassroom)) {
                    actualClassroom = allClassrooms.get(0).getClassroomName();
                }
                events = dl.getEventDAO().getThisWeekEventsByClassroomNameAndGroup(actualClassroom, weekCounter,departmentName);
            }
        }

        private boolean checkUrlParam(String param) {
            return param != null && !param.trim().isEmpty();
        }
    }

    private void actionDefault(RequestParams params, HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateManagerException, DataException {
        LocalDate today = LocalDate.now();
        List<String> weekDays = getWeekDays(today.plusWeeks(params.weekCounter));


        request.setAttribute("actual_week", params.weekCounter);
        request.setAttribute("department", params.departmentName);
        request.setAttribute("events", params.events);
        request.setAttribute("actual_classroom", params.actualClassroom);
        request.setAttribute("actual_course", params.course);
        request.setAttribute("WeekDays", weekDays);
        request.setAttribute("page_title", "Calendar Department: " + params.departmentName);


        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        request.setAttribute("courses", dl.getCourseDAO().getCourses());
        request.setAttribute("classrooms", dl.getClassroomDAO().getClassrooms());


        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("dep_calendar.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
            RequestParams params = new RequestParams(request, dl);
            actionDefault(params, request, response);
        } catch (NumberFormatException | DataException ex) {
            handleError("Invalid or missing input: " + ex.getMessage(), request, response);
        }
    }

    private List<String> getWeekDays(LocalDate eventDate) {
        List<String> weekDays = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMM");

        // Calculate Monday of the current week
        LocalDate monday = eventDate.with(java.time.DayOfWeek.MONDAY);

        // Generate dates from Monday to Sunday
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = monday.plusDays(i);
            weekDays.add(currentDay.format(formatter));
        }
        return weekDays;
    }


}

