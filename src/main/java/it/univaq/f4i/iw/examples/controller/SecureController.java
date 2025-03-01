package it.univaq.f4i.iw.examples.controller;

import it.univaq.f4i.iw.examples.application.ApplicationDataLayer;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.*;
import it.univaq.f4i.iw.examples.application.ApplicationBaseController;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.TemplateManagerException;
import it.univaq.f4i.iw.framework.view.TemplateResult;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecureController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException, TemplateManagerException {
        // Get the data layer
        ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");

        // Retrieve all tools
        List<Tool> allTools = dl.getToolDAO().getTools();

        // Debug print to console
        if (allTools != null) {
            System.out.println("Tools found: " + allTools.size());
            for (Tool tool : allTools) {
                System.out.println("Tool ID: " + tool.getKey() + ", Name: " + tool.getToolName());
            }
        } else {
            System.out.println("No tools found!");
        }

        // Set the attribute for the template
        request.setAttribute("tools", allTools);

        // Set up the template result
        TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "tools");
        
        // Render the template
        result.activate("classrooms.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
