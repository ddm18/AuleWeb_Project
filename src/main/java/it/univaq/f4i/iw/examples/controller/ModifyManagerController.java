package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Manager;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyManagerController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        List<Manager> managers = dl.getManagerDAO().getManagers();
        request.setAttribute("managers", managers);
        request.setAttribute("page_title", "Manage Managers");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_manager.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for the new manager
            String managerName = request.getParameter("manager_name");
            String managerEmail = request.getParameter("manager_email");

            // Validate inputs
            if (managerName == null || managerName.trim().isEmpty() || managerEmail == null || managerEmail.trim().isEmpty()) {
                throw new DataException("Manager name and email cannot be empty.");
            }

            // Check for duplicate name
            if (dl.getManagerDAO().getManagerByName(managerName) != null) {
                throw new DataException("Cannot add manager: duplicate name.");
            }

            // Create and store the new manager
            Manager newManager = dl.getManagerDAO().createManager();
            newManager.setManagerName(managerName);
            newManager.setManagerEmail(managerEmail);
            dl.getManagerDAO().storeManager(newManager);

            // Redirect to refresh the list of managers
            response.sendRedirect("modify_manager");
        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_modify(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather and validate parameters
            int managerKey = Integer.parseInt(request.getParameter("manager_key"));
            String newManagerName = request.getParameter("manager_name").trim();
            String newManagerEmail = request.getParameter("manager_email").trim();

            // Validate inputs
            if (newManagerName.isEmpty() || newManagerEmail.isEmpty()) {
                throw new DataException("Manager name and email cannot be empty.");
            }

            // Check if the manager exists
            Manager manager = dl.getManagerDAO().getManager(managerKey);
            if (manager == null) {
                throw new DataException("Manager with the specified ID does not exist.");
            }

            // Check for duplicate manager name (excluding the current manager being updated)
            Manager existingManager = dl.getManagerDAO().getManagerByName(newManagerName);
            if (existingManager != null && existingManager.getKey() != managerKey) {
                throw new DataException("Cannot modify manager: a manager with the name '" + newManagerName + "' already exists.");
            }

            // Update manager details
            manager.setManagerName(newManagerName);
            manager.setManagerEmail(newManagerEmail);

            // Store updated manager
            dl.getManagerDAO().storeManager(manager);

            // Redirect to refresh the list of managers
            response.sendRedirect("modify_manager");

        } catch (DataException ex) {
            // Handle specific validation or database-related errors
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            // Handle invalid numeric input for manager key
            handleError("Invalid manager key. Please provide a valid number.", request, response);
        } catch (Exception ex) {
            // Handle unexpected errors
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        int managerKey = Integer.parseInt(request.getParameter("manager_key"));

        dl.getManagerDAO().deleteManager(managerKey);

        response.sendRedirect("modify_manager");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_manager":
                    action_add(request, response);
                    break;
                case "modify_manager":
                    action_modify(request, response);
                    break;
                case "delete_manager":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
