package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.ClassroomGroupImpl;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

public class ClassroomGroupProxy extends ClassroomGroupImpl implements DataItemProxy {

    private boolean modified;
    protected DataLayer dataLayer;

    public ClassroomGroupProxy(DataLayer datalayer) {
        super();
        this.dataLayer = datalayer;
        this.modified = false;
    }
    

    // Overriding setters to track modifications
    
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }
    
    @Override
    public void setGroupName(String groupName) {
        super.setGroupName(groupName);
        this.modified = true;
    }

    @Override
    public void setGroupDescription(String groupDescription) {
        super.setGroupDescription(groupDescription);
        this.modified = true;
    }

    // Proxy-only methods to check and reset modification status
    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public boolean isModified() {
        return modified;
    }
}
