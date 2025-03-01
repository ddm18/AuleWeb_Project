package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ClassroomGroupDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.dao.LocationDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomGroup;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Location;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.ClassroomImpl;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import java.util.logging.Level;
import java.util.logging.Logger;

//This extends the implementation of the model. 
public class ClassroomProxy extends ClassroomImpl implements DataItemProxy {

    protected boolean modified;
    protected int groupKey = 0;
    protected int locationKey = 0;
    protected DataLayer dataLayer;

    public ClassroomProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
        this.locationKey = 0;
        this.groupKey = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Lazy loading for ClassroomGroup. Meaning it's empty before calling either this or before setting a new one.
    @Override
    public ClassroomGroup getGroup() {
        if (super.getGroup() == null && groupKey > 0) {
            try {

                ClassroomGroupDAO groupDAO = (ClassroomGroupDAO) dataLayer.getDAO(ClassroomGroup.class); //Retrieve the groupDAO through data layer for decoupling
                
                //Retrieve and set the group in the super ClassroomImpl object.
                super.setGroup(groupDAO.getClassroomGroup(groupKey));
                
            } catch (DataException ex) {
                Logger.getLogger(ClassroomProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getGroup();
    }
    
    //setting a new one. Same method as the old one with the difference of setting a new key pointing to the new location
    //and modified flag.
    @Override
    public void setGroup(ClassroomGroup group) {
        super.setGroup(group);
        this.groupKey = group.getKey();
        this.modified = true;
    }

    // Lazy loading for Location
    @Override
    public Location getLocation() {
        if (super.getLocation() == null && locationKey > 0) {
            try {
            	LocationDAO locationDAO = (LocationDAO) dataLayer.getDAO(Location.class);
            	
                super.setLocation(locationDAO.getLocation(locationKey));
                
            } catch (DataException ex) {
                Logger.getLogger(ClassroomProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getLocation();
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        this.locationKey = location.getKey();
        this.modified = true;
    }

    // Track modifications for other fields

    @Override
    public void setCapacity(int capacity) {
        super.setCapacity(capacity);
        this.modified = true;
    }


    @Override
    public void setManagerEmail(String managerEmail) {
        super.setManagerEmail(managerEmail);
        this.modified = true;
    }


    @Override
    public void setElectricSockets(int electricSockets) {
        super.setElectricSockets(electricSockets);
        this.modified = true;
    }


    @Override
    public void setInternetSockets(int internetSockets) {
        super.setInternetSockets(internetSockets);
        this.modified = true;
    }


    @Override
    public void setNotes(String notes) {
        super.setNotes(notes);
        this.modified = true;
    }


    @Override
    public void setClassroomName(String classroomName) {
        super.setClassroomName(classroomName);
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

    // Methods for setting new foreign keys for group and locations. They need to be invalidated since the already retrieved ones 
    //aren't correct anymore.
    public void setGroupKey(int groupKey) {
        this.groupKey = groupKey;
        super.setGroup(null); // Invalidate the cached group
    }

    public void setLocationKey(int locationKey) {
        this.locationKey = locationKey;
        super.setLocation(null); // Invalidate the cached location
    }
}
