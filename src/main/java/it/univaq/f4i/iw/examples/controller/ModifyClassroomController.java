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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModifyClassroomController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        // Load classrooms, managers, and locations
        List<Classroom> classrooms = dl.getClassroomDAO().getClassrooms();
        request.setAttribute("classrooms", classrooms);
        request.setAttribute("managers", dl.getManagerDAO().getManagers());
        request.setAttribute("locations", dl.getLocationDAO().getLocations());
        request.setAttribute("departments", dl.getClassroomGroupDAO().getClassroomGroups());
        request.setAttribute("tools", dl.getToolDAO().getTools());
        request.setAttribute("departments", dl.getClassroomGroupDAO().getClassroomGroups());
        request.setAttribute("page_title", "Manage Classrooms");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_classroom.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for the new classroom
            String classroomName = request.getParameter("classroom_name");
            int capacity = SecurityHelpers.checkNumeric(request.getParameter("capacity"));
            String managerEmail = request.getParameter("manager_email");
            int electricSockets = SecurityHelpers.checkNumeric(request.getParameter("electric_sockets"));
            int internetSockets = SecurityHelpers.checkNumeric(request.getParameter("internet_sockets"));
            String notes = request.getParameter("notes");
            int groupId = SecurityHelpers.checkNumeric(request.getParameter("group_id"));
            int locationId = SecurityHelpers.checkNumeric(request.getParameter("location_id"));
            String[] selectedToolIds = request.getParameterValues("tool_id");

            // Check if a classroom with the same name already exists
            Classroom existingClassroom = dl.getClassroomDAO().getClassroomByName(classroomName);
            if (existingClassroom != null) {
                throw new DataException("A classroom with the name '" + classroomName + "' already exists.");
            }

            // Create and store the new classroom
            Classroom newClassroom = dl.getClassroomDAO().createClassroom();
            newClassroom.setClassroomName(classroomName);
            newClassroom.setCapacity(capacity);
            newClassroom.setManagerEmail(managerEmail);
            newClassroom.setElectricSockets(electricSockets);
            newClassroom.setInternetSockets(internetSockets);
            newClassroom.setNotes(notes);
            newClassroom.setLocation(dl.getLocationDAO().getLocation(locationId));
            newClassroom.setGroup(dl.getClassroomGroupDAO().getClassroomGroup(groupId));
            dl.getClassroomDAO().storeClassroom(newClassroom);

            // Add tools to the classroom if provided
            if (selectedToolIds != null) {
                for (String tool_id : selectedToolIds) {
                    int tool_id_int = SecurityHelpers.checkNumeric(tool_id);

                    ClassroomTool newClassroomTool = dl.getClassroomToolDAO().createClassroomTool();
                    newClassroomTool.setClassroom(newClassroom);
                    newClassroomTool.setTool(dl.getToolDAO().getTool(tool_id_int));
                    dl.getClassroomToolDAO().storeClassroomTool(newClassroomTool);
                }
            }

            response.sendRedirect("modify_classroom");
        } catch (DataException ex) {
            handleError("A classroom with the given name already exists: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            handleError("Invalid numeric input while adding a classroom", request, response);
        }
    }


    private void action_modify(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for modifying an existing classroom
            int classroomId = SecurityHelpers.checkNumeric(request.getParameter("classroom_id"));
            String newClassroomName = request.getParameter("classroom_name");
            int newCapacity = SecurityHelpers.checkNumeric(request.getParameter("capacity"));
            String newManagerEmail = request.getParameter("manager_email");
            int newElectricSockets = SecurityHelpers.checkNumeric(request.getParameter("electric_sockets"));
            int newInternetSockets = SecurityHelpers.checkNumeric(request.getParameter("internet_sockets"));
            String newNotes = request.getParameter("notes");
            int newGroupId = SecurityHelpers.checkNumeric(request.getParameter("group_id"));
            int newLocationId = SecurityHelpers.checkNumeric(request.getParameter("location_id"));
            String[] selectedToolIdsArray = request.getParameterValues("tool_id");

            // Validate classroom name
            if (newClassroomName == null || newClassroomName.trim().isEmpty()) {
                throw new DataException("Classroom name cannot be empty.");
            }

            // Check if a classroom with the new name already exists (excluding the current classroom)
            Classroom existingClassroom = dl.getClassroomDAO().getClassroomByName(newClassroomName);
            if (existingClassroom != null && existingClassroom.getKey() != classroomId) {
                throw new DataException("A classroom with the name '" + newClassroomName + "' already exists.");
            }

            // Retrieve the classroom to modify
            Classroom classroom = dl.getClassroomDAO().getClassroom(classroomId);
            if (classroom == null) {
                throw new DataException("The classroom does not exist.");
            }

            // Update classroom properties
            classroom.setClassroomName(newClassroomName);
            classroom.setCapacity(newCapacity);
            classroom.setManagerEmail(newManagerEmail);
            classroom.setElectricSockets(newElectricSockets);
            classroom.setInternetSockets(newInternetSockets);
            classroom.setNotes(newNotes);
            classroom.setLocation(dl.getLocationDAO().getLocation(newLocationId));
            classroom.setGroup(dl.getClassroomGroupDAO().getClassroomGroup(newGroupId));

            dl.getClassroomDAO().storeClassroom(classroom);

            // Update tools associated with the classroom
            if (selectedToolIdsArray != null) {
                List<String> selectedToolIds = new ArrayList<>(Arrays.asList(selectedToolIdsArray));
                List<ClassroomTool> existingTools = dl.getClassroomToolDAO().getToolsByClassroom(classroomId);

                for (ClassroomTool ctool : existingTools) {
                    String toolKey = Integer.toString(ctool.getTool().getKey());
                    if (selectedToolIds.contains(toolKey)) {
                        selectedToolIds.remove(toolKey); // Remove existing tools from the new list
                    } else {
                        dl.getClassroomToolDAO().deleteClassroomTool(ctool.getKey()); // Delete tools no longer needed
                    }
                }

                for (String toolId : selectedToolIds) {
                    int toolIdInt = SecurityHelpers.checkNumeric(toolId);

                    ClassroomTool newTool = dl.getClassroomToolDAO().createClassroomTool();
                    newTool.setClassroom(classroom);
                    newTool.setTool(dl.getToolDAO().getTool(toolIdInt));
                    dl.getClassroomToolDAO().storeClassroomTool(newTool);
                }
            }

            // Redirect after successful modification
            response.sendRedirect("modify_classroom");

        } catch (DataException ex) {
            // Handle errors related to validation or database operations
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            // Handle invalid numeric inputs
            handleError("Invalid numeric input while modifying a classroom", request, response);
        }
    }


    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Get the ID of the classroom to delete
            int classroomId = SecurityHelpers.checkNumeric(request.getParameter("classroom_id"));
            dl.getClassroomDAO().deleteClassroom(classroomId);
            response.sendRedirect("modify_classroom");
        } catch (NumberFormatException ex) {
            handleError("Invalid classroom ID for deletion", request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_classroom":
                    action_add(request, response);
                    break;
                case "modify_classroom":
                    action_modify(request, response);
                    break;
                case "delete_classroom":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
