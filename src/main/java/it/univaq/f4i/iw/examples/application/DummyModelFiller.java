package it.univaq.f4i.iw.examples.application;


import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.view.DataModelFiller;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author giuse
 */
public class DummyModelFiller implements DataModelFiller {

    @Override
    public void fillDataModel(Map datamodel, HttpServletRequest request, ServletContext context) {        
    	ApplicationDataLayer dl = (ApplicationDataLayer) request.getAttribute("datalayer");
    	if (request.getParameterMap().containsKey("groupName")) {
    		try {
    		datamodel.put("department",dl.getClassroomGroupDAO().getClassroomGroupByName(request.getParameter("groupName")));
    		}
	    	catch (DataException ex) {
	            Logger.getLogger(DummyModelFiller.class.getName()).log(Level.SEVERE, null, ex);
	        }
    	}
    }

}
