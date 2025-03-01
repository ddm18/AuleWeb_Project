package it.univaq.f4i.iw.examples.application;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.*;
import it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl.*;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.*;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Data Layer class to register and manage all DAOs.
 * Author: Giuseppe Della Penna
 */
public class ApplicationDataLayer extends DataLayer {

    public ApplicationDataLayer(DataSource datasource) throws SQLException {
        super(datasource);
    }

    @Override
    public void init() throws DataException {
        // Register all the DAOs
        registerDAO(Classroom.class, new ClassroomDAO_MySQL(this));
        registerDAO(User.class, new UserDAO_MySQL(this));
        registerDAO(ClassroomGroup.class, new ClassroomGroupDAO_MySQL(this));
        registerDAO(Location.class, new LocationDAO_MySQL(this));
        registerDAO(Event.class, new EventDAO_MySQL(this));
        registerDAO(Manager.class, new ManagerDAO_MySQL(this));
        registerDAO(Type.class, new TypeDAO_MySQL(this));
        registerDAO(Tool.class, new ToolDAO_MySQL(this));
        registerDAO(ClassroomTool.class, new ClassroomToolDAO_MySQL(this));
        registerDAO(Course.class, new CourseDAO_MySQL(this));
    }

    // Helper methods to access DAOs
    public ClassroomDAO getClassroomDAO() {
        return (ClassroomDAO) getDAO(Classroom.class);
    }

    public UserDAO getUserDAO() {
        return (UserDAO) getDAO(User.class);
    }

    public ClassroomGroupDAO getClassroomGroupDAO() {
        return (ClassroomGroupDAO) getDAO(ClassroomGroup.class);
    }

    public LocationDAO getLocationDAO() {
        return (LocationDAO) getDAO(Location.class);
    }

    public EventDAO getEventDAO() {
        return (EventDAO) getDAO(Event.class);
    }

    public ManagerDAO getManagerDAO() {
        return (ManagerDAO) getDAO(Manager.class);
    }

    public TypeDAO getTypeDAO() {
        return (TypeDAO) getDAO(Type.class);
    }

    public ToolDAO getToolDAO() {
        return (ToolDAO) getDAO(Tool.class);
    }

    public ClassroomToolDAO getClassroomToolDAO() {
        return (ClassroomToolDAO) getDAO(ClassroomTool.class);
    }

    public CourseDAO getCourseDAO() {
        return (CourseDAO) getDAO(Course.class);
    }


}
