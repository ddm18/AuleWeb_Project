package it.univaq.f4i.iw.examples.controller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Classroom;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomTool;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.StreamResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVClassroomExporterController extends ApplicationBaseController {

    private void action_download_classroom(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        String classroomName = request.getParameter("classroom_name");
        File tempFile;
        List<Classroom> classrooms;

        if (check_url_param(classroomName)) {
            classrooms = List.of(dl.getClassroomDAO().getClassroomByName(classroomName));
            tempFile = File.createTempFile("classroom_" + classroomName + "_", "_Details.csv");
        } else {
            classrooms = dl.getClassroomDAO().getClassrooms();
            tempFile = File.createTempFile("all_classrooms_", "_Details.csv");
        }

        // Generate temporary CSV file
        try (FileWriter writer = new FileWriter(tempFile)) {
            CSVFormat csvFormat = CSVFormat.Builder.create()
                    .setHeader("Classroom Name", "Building", "Floor", "Section", "Capacity", 
                               "Manager Email", "Electric Sockets", "Internet Sockets", "Tools", "Notes")
                    .build();

            try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
                for (Classroom classroom : classrooms) {
                    List<ClassroomTool> classroomTools = dl.getClassroomToolDAO().getToolsByClassroom(classroom.getKey());
                    String toolNames = classroomTools.stream()
                                                     .map(ct -> ct.getTool().getToolName())
                                                     .reduce((t1, t2) -> t1 + ", " + t2)
                                                     .orElse("None");

                    printer.printRecord(
                            classroom.getClassroomName(),
                            classroom.getLocation().getBuilding(),
                            classroom.getLocation().getFloor(),
                            classroom.getLocation().getPlace(),
                            classroom.getCapacity(),
                            classroom.getManagerEmail(),
                            classroom.getElectricSockets(),
                            classroom.getInternetSockets(),
                            toolNames,
                            classroom.getNotes()
                    );
                }
            }
        }


        StreamResult result = new StreamResult(getServletContext());
        result.setResource(tempFile);
        result.activate(request, response);

        // Delete the temporary file
        if (!tempFile.delete()) {
            System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_download_classroom(request, response);
    }

    private Boolean check_url_param(String param) {
        return param != null && !param.trim().isEmpty();
    }
}
