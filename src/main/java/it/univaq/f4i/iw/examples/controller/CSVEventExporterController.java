package it.univaq.f4i.iw.examples.controller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.StreamResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CSVEventExporterController extends ApplicationBaseController {

    private void action_download_course(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        // Retrieve parameters
        String course = request.getParameter("course");
        String start_date_string = request.getParameter("start_date");
        String end_date_string = request.getParameter("end_date");
        File tempFile = null;
        List<Event> events = null;

        // Validate parameters
        if (start_date_string == null || end_date_string == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: start_date or end_date");
            return;
        }

        LocalDate start_date = LocalDate.parse(start_date_string);
        LocalDate end_date = LocalDate.parse(end_date_string);
        
        if (check_url_param(course)) {
        	int course_id = dl.getCourseDAO().getCourseByName(course).getKey();
            events = dl.getEventDAO().getEventsByCourseAndDateRange(course_id, start_date, end_date);
            tempFile = File.createTempFile("course_" + course + "_", "_Period.csv");
        }else {
        	events = dl.getEventDAO().getEventsByDateRange(start_date, end_date);
        	tempFile = File.createTempFile("start_date_" + start_date_string + "_end_date_"+ start_date_string,"_Period.csv");
        }
        

        // Generate temporary CSV file
        
     // Generate temporary CSV file
        try (FileWriter writer = new FileWriter(tempFile)) {
            CSVFormat csvFormat = CSVFormat.Builder.create()
                    .setHeader("Event Name", "Date", "Start Time", "End Time", "Description",
                               "Event Type", "Event Manager Name", "Event Manager Email",
                               "Course", "Classroom Name", "Building", "Floor", "Section")
                    .build();

            try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
                for (Event event : events) {
                    printer.printRecord(
                            event.getName(),                       // No HTML sanitization needed
                            event.getDate(),
                            event.getStartTime(),
                            event.getEndTime(),
                            event.getDescription(),
                            event.getEventType().getEventTypeName(),
                            event.getEventManager().getManagerName(),
                            event.getEventManager().getManagerEmail(),
                            event.getCourse() != null ? event.getCourse().getCourseName() : "",
                            event.getClassroom().getClassroomName(),
                            event.getClassroom().getLocation().getBuilding(),
                            event.getClassroom().getLocation().getFloor(),
                            event.getClassroom().getLocation().getPlace()
                    );
                }
            }
        }


         
        StreamResult result = new StreamResult(getServletContext());
        result.setResource(tempFile);
        result.activate(request, response);


        if (!tempFile.delete()) {
            System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_download_course(request, response);
    }
    private Boolean check_url_param(String param) {
        return param != null && !param.trim().isEmpty();
    }
}
