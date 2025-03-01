package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.ManagerImpl;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

public class ManagerProxy extends ManagerImpl implements DataItemProxy {

    protected boolean modified;
    protected DataLayer dataLayer;

    // Constructor with DataLayer injection
    public ManagerProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
    }

    // Overriding setKey to mark as modified
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Overriding setters to track modifications
    @Override
    public void setManagerName(String managerName) {
        super.setManagerName(managerName);
        this.modified = true;
    }

    @Override
    public void setManagerEmail(String managerEmail) {
        super.setManagerEmail(managerEmail);
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
}
