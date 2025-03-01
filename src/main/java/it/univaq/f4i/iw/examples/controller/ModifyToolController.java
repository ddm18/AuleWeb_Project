package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Tool;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyToolController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        List<Tool> tools = dl.getToolDAO().getTools();
        request.setAttribute("tools", tools);
        request.setAttribute("page_title", "Manage Tools");

        TemplateResult result = new TemplateResult(getServletContext());
        result.activate("modify_tool.ftl.html", request, response);
    }

    private void action_add(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        try {
            // Gather parameters for the new tool
            String toolName = request.getParameter("tool_name");

            // Validate input
            if (toolName == null || toolName.trim().isEmpty()) {
                throw new DataException("Tool name cannot be empty.");
            }

            // Check if a tool with the same name already exists
            if (dl.getToolDAO().getToolByName(toolName) != null) {
                throw new DataException("Cannot add tool: duplicate name.");
            }

            // Create and store the new tool
            Tool newTool = dl.getToolDAO().createTool();
            newTool.setToolName(toolName);
            dl.getToolDAO().storeTool(newTool);

            // Redirect to refresh the list of tools
            response.sendRedirect("modify_tool");
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
            int toolKey = Integer.parseInt(request.getParameter("tool_key"));
            String newToolName = request.getParameter("tool_name").trim();

            // Validate input
            if (newToolName == null || newToolName.isEmpty()) {
                throw new DataException("Tool name cannot be empty.");
            }

            // Check if the tool exists
            Tool tool = dl.getToolDAO().getTool(toolKey);
            if (tool == null) {
                throw new DataException("Tool with the specified ID does not exist.");
            }

            // Check for duplicate tool name (excluding the current tool being updated)
            Tool existingTool = dl.getToolDAO().getToolByName(newToolName);
            if (existingTool != null && existingTool.getKey() != toolKey) {
                throw new DataException("Cannot modify tool: a tool with the name '" + newToolName + "' already exists.");
            }

            // Update tool details
            tool.setToolName(newToolName);

            // Store updated tool
            dl.getToolDAO().storeTool(tool);

            // Redirect to refresh the list of tools
            response.sendRedirect("modify_tool");

        } catch (DataException ex) {
            // Handle specific validation or database-related errors
            handleError("Error: " + ex.getMessage(), request, response);
        } catch (NumberFormatException ex) {
            // Handle invalid numeric input for tool key
            handleError("Invalid tool key. Please provide a valid number.", request, response);
        } catch (Exception ex) {
            // Handle unexpected errors
            handleError("An unexpected error occurred: " + ex.getMessage(), request, response);
        }
    }


    private void action_delete(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
        int toolKey = Integer.parseInt(request.getParameter("tool_key"));

        dl.getToolDAO().deleteTool(toolKey);

        response.sendRedirect("modify_tool");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String formInput = request.getParameter("form_input");

        if (formInput == null) {
            action_default(request, response);
        } else {
            switch (formInput) {
                case "add_tool":
                    action_add(request, response);
                    break;
                case "modify_tool":
                    action_modify(request, response);
                    break;
                case "delete_tool":
                    action_delete(request, response);
                    break;
                default:
                    action_default(request, response);
            }
        }
    }
}
