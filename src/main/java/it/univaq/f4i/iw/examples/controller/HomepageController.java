/*
 * HomepageController.java
 *
 *
 */
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

/**
 *
 * @author Ingegneria del Web
 * @version
 */
public class HomepageController extends ApplicationBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, TemplateManagerException, DataException {
    	ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
    	List<ClassroomGroup> allClassroomGroups = dl.getClassroomGroupDAO().getClassroomGroups();
    	request.setAttribute("classroomGroups", allClassroomGroups);
    	
    	
    	TemplateResult result = new TemplateResult(getServletContext());
        request.setAttribute("page_title", "Homepage");
        result.activate("homepage.ftl.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        action_default(request, response);

    }

}
