package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ClassroomDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ToolDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Classroom;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Tool;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.ClassroomToolImpl;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassroomToolProxy extends ClassroomToolImpl implements DataItemProxy {

    private boolean modified;
    private int classroomKey = 0;
    private int toolKey = 0;
    private DataLayer dataLayer;

    public ClassroomToolProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
        this.classroomKey = 0;
        this.toolKey = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Lazy loading for Classroom
    @Override
    public Classroom getClassroom() {
        if (super.getClassroom() == null && classroomKey > 0) {
            try {
                ClassroomDAO classroomDAO = (ClassroomDAO) dataLayer.getDAO(Classroom.class);
                super.setClassroom(classroomDAO.getClassroom(classroomKey));
            } catch (DataException ex) {
                Logger.getLogger(ClassroomToolProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getClassroom();
    }

    @Override
    public void setClassroom(Classroom classroom) {
        super.setClassroom(classroom);
        this.classroomKey = classroom.getKey();
        this.modified = true;
    }

    // Lazy loading for Tool
    @Override
    public Tool getTool() {
        if (super.getTool() == null && toolKey > 0) {
            try {
                ToolDAO toolDAO = (ToolDAO) dataLayer.getDAO(Tool.class);
                super.setTool(toolDAO.getTool(toolKey));
            } catch (DataException ex) {
                Logger.getLogger(ClassroomToolProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getTool();
    }

    @Override
    public void setTool(Tool tool) {
        super.setTool(tool);
        this.toolKey = tool.getKey();
        this.modified = true;
    }

    // Proxy-specific methods for modification tracking
    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    // Methods to set foreign keys and invalidate cached objects
    public void setClassroomKey(int classroomKey) {
        this.classroomKey = classroomKey;
        super.setClassroom(null); // Invalidate the cached classroom
    }

    public void setToolKey(int toolKey) {
        this.toolKey = toolKey;
        super.setTool(null); // Invalidate the cached tool
    }
}
