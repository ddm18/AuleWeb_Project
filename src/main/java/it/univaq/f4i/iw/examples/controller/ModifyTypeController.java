package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Type;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyTypeController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        List<Type> types = dl.getTypeDAO().getTypes();
        request.setAttribute("types", types);
        request.setAttribute("page_title", "Manage Types");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_type.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for the new type
            String typeName = request.getParameter("type_name");

            // Validate input
            if (typeName == null || typeName.trim().isEmpty()) {
                throw new DataException("Type name cannot be empty.");
            }

            // Check if a type with the same name already exists
            if (dl.getTypeDAO().getTypeByName(typeName) != null) {
                throw new DataException("Cannot add type: duplicate name.");
            }

            // Create and store the new type
            Type newType = dl.getTypeDAO().createType();
            newType.setEventTypeName(typeName);
            dl.getTypeDAO().storeType(newType);

            // Redirect to refresh the list of types
            response.sendRedirect("modify_type");
        } catch (DataException ex) {
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (Exception ex) {
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_modify(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters
            int typeKey = Integer.parseInt(request.getParameter("type_key"));
            String newTypeName = request.getParameter("type_name").trim();

            // Validate input
            if (newTypeName == null || newTypeName.isEmpty()) {
                throw new DataException("Type name cannot be empty.");
            }

            // Check if the type exists
            Type type = dl.getTypeDAO().getType(typeKey);
            if (type == null) {
                throw new DataException("Type with the specified ID does not exist.");
            }

            // Check for duplicate type name (excluding the current type being updated)
            Type existingType = dl.getTypeDAO().getTypeByName(newTypeName);
            if (existingType != null && existingType.getKey() != typeKey) {
                throw new DataException("Cannot modify type: a type with the name '" + newTypeName + "' already exists.");
            }

            // Update the type details
            type.setEventTypeName(newTypeName);

            // Store updated type
            dl.getTypeDAO().storeType(type);

            // Redirect to refresh the list of types
            response.sendRedirect("modify_type");

        } catch (DataException ex) {
            // Handle specific validation or database-related errors
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            // Handle invalid numeric input for type key
            handleError("Invalid type key. Please provide a valid number.", request, response);
        } catch (Exception ex) {
            // Handle unexpected errors
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }

    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        int typeKey = Integer.parseInt(request.getParameter("type_key"));

        dl.getTypeDAO().deleteType(typeKey);

        response.sendRedirect("modify_type");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_type":
                    action_add(request, response);
                    break;
                case "modify_type":
                    action_modify(request, response);
                    break;
                case "delete_type":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
