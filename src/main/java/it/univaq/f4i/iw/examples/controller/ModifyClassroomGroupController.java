package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomGroup;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyClassroomGroupController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        List<ClassroomGroup> departments = dl.getClassroomGroupDAO().getClassroomGroups();
        request.setAttribute("departments", departments);
        request.setAttribute("page_title", "Manage Classroom Groups");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_classroom_group.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for the new group
            String groupName = request.getParameter("group_name");
            String groupDescription = request.getParameter("group_description");

            // Check if a group with the same name already exists
            ClassroomGroup existingGroup = dl.getClassroomGroupDAO().getClassroomGroupByName(groupName);
            if (existingGroup != null) {
                throw new DataException("A classroom group with the name '" + groupName + "' already exists.");
            }

            // Create and store the new classroom group
            ClassroomGroup newGroup = dl.getClassroomGroupDAO().createClassroomGroup();
            newGroup.setGroupName(groupName);
            newGroup.setGroupDescription(groupDescription);
            dl.getClassroomGroupDAO().storeClassroomGroup(newGroup);

            response.sendRedirect("modify_classroom_group");
        } catch (DataException ex) {
            handleError("A department with the given name already exists: " + ex.getMessage(), request, response);
        }
    }


    private void action_modify(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for modifying an existing classroom group
            int departmentKey = Integer.parseInt(request.getParameter("department_key"));
            String newGroupName = request.getParameter("group_name");
            String newGroupDescription = request.getParameter("group_description");

            // Validate the group name
            if (newGroupName == null || newGroupName.trim().isEmpty()) {
                throw new DataException("Classroom group name cannot be empty.");
            }

            // Check if a group with the new name already exists (excluding the current group)
            ClassroomGroup existingGroup = dl.getClassroomGroupDAO().getClassroomGroupByName(newGroupName);
            if (existingGroup != null && existingGroup.getKey() != departmentKey) {
                throw new DataException("A classroom group with the name '" + newGroupName + "' already exists.");
            }

            // Retrieve the group to modify
            ClassroomGroup group = dl.getClassroomGroupDAO().getClassroomGroup(departmentKey);
            if (group == null) {
                throw new DataException("The specified classroom group does not exist.");
            }

            // Update group details
            group.setGroupName(newGroupName);
            group.setGroupDescription(newGroupDescription);

            // Store the updated group
            dl.getClassroomGroupDAO().storeClassroomGroup(group);

            // Redirect to refresh the list of groups
            response.sendRedirect("modify_classroom_group");

        } catch (DataException ex) {
            // Handle errors related to validation or database operations
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            // Handle invalid numeric inputs
            handleError("Invalid numeric input while modifying a classroom group", request, response);
        }
    }


    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        int departmentKey = Integer.parseInt(request.getParameter("department_key"));

        dl.getClassroomGroupDAO().deleteClassroomGroup(departmentKey);

        response.sendRedirect("modify_classroom_group");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_classroom_group":
                    action_add(request, response);
                    break;
                case "modify_classroom_group":
                    action_modify(request, response);
                    break;
                case "delete_classroom_group":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
