package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.*;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.*;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.EventImpl;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventProxy extends EventImpl implements DataItemProxy {

    protected boolean modified;
    protected int typeKey = 0; // Foreign key for Type
    protected int managerKey = 0; // Foreign key for Manager
    protected int courseKey = 0; // Foreign key for Course
    protected int classroomKey = 0; // Foreign key for Classroom
    protected DataLayer dataLayer;

    public EventProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
        this.typeKey = 0;
        this.managerKey = 0;
        this.courseKey = 0;
        this.classroomKey = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Lazy loading for Type
    @Override
    public Type getEventType() {
        if (super.getEventType() == null && typeKey > 0) {
            try {
                TypeDAO typeDAO = (TypeDAO) dataLayer.getDAO(Type.class);
                super.setEventType(typeDAO.getType(typeKey));
            } catch (DataException ex) {
                Logger.getLogger(EventProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getEventType();
    }

    @Override
    public void setEventType(Type eventType) {
        super.setEventType(eventType);
        this.typeKey = eventType != null ? eventType.getKey() : 0;
        this.modified = true;
    }

    // Lazy loading for Manager
    @Override
    public Manager getEventManager() {
        if (super.getEventManager() == null && managerKey > 0) {
            try {
                ManagerDAO managerDAO = (ManagerDAO) dataLayer.getDAO(Manager.class);
                super.setEventManager(managerDAO.getManager(managerKey));
            } catch (DataException ex) {
                Logger.getLogger(EventProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getEventManager();
    }

    @Override
    public void setEventManager(Manager eventManager) {
        super.setEventManager(eventManager);
        this.managerKey = eventManager != null ? eventManager.getKey() : 0;
        this.modified = true;
    }

    // Lazy loading for Course
    @Override
    public Course getCourse() {
        if (super.getCourse() == null && courseKey > 0) {
            try {
                CourseDAO courseDAO = (CourseDAO) dataLayer.getDAO(Course.class);
                super.setCourse(courseDAO.getCourse(courseKey));
            } catch (DataException ex) {
                Logger.getLogger(EventProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getCourse();
    }

    @Override
    public void setCourse(Course course) {
        super.setCourse(course);
        this.courseKey = course != null ? course.getKey() : 0;
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
                Logger.getLogger(EventProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getClassroom();
    }

    @Override
    public void setClassroom(Classroom classroom) {
        super.setClassroom(classroom);
        this.classroomKey = classroom != null ? classroom.getKey() : 0;
        this.modified = true;
    }

    // Track modifications for other fields
    @Override
    public void setDate(LocalDate date) {
        super.setDate(date);
        this.modified = true;
    }

    @Override
    public void setStartTime(LocalTime startTime) {
        super.setStartTime(startTime);
        this.modified = true;
    }

    @Override
    public void setEndTime(LocalTime endTime) {
        super.setEndTime(endTime);
        this.modified = true;
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        this.modified = true;
    }

    @Override
    public void setMasterId(Integer masterId) {
        super.setMasterId(masterId);
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
    public void setTypeKey(int typeKey) {
        this.typeKey = typeKey;
        super.setEventType(null); // Invalidate the cached event type
    }

    public void setManagerKey(int managerKey) {
        this.managerKey = managerKey;
        super.setEventManager(null); // Invalidate the cached event manager
    }

    public void setCourseKey(int courseKey) {
        this.courseKey = courseKey;
        super.setCourse(null); // Invalidate the cached course
    }

    public void setClassroomKey(int classroomKey) {
        this.classroomKey = classroomKey;
        super.setClassroom(null); // Invalidate the cached classroom
    }
}
