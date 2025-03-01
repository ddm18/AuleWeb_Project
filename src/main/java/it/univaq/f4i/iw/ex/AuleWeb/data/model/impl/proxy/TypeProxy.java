package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.TypeImpl;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

public class TypeProxy extends TypeImpl implements DataItemProxy {

    private boolean modified;
    private DataLayer dataLayer;

    public TypeProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
    }

    // Overriding the setKey method to track modifications
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Overriding the setEventTypeName method to track modifications
    @Override
    public void setEventTypeName(String eventTypeName) {
        super.setEventTypeName(eventTypeName);
        this.modified = true;
    }

    // Proxy-specific methods for tracking modifications
    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public boolean isModified() {
        return modified;
    }
}
