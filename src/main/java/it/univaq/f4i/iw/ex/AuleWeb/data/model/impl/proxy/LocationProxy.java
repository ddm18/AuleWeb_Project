package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Location;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.LocationImpl;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

public class LocationProxy extends LocationImpl implements DataItemProxy {

    private boolean modified;
    private DataLayer dataLayer;

    public LocationProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Overriding setters to track modifications
    @Override
    public void setPlace(String place) {
        super.setPlace(place);
        this.modified = true;
    }

    @Override
    public void setBuilding(String building) {
        super.setBuilding(building);
        this.modified = true;
    }

    @Override
    public void setFloor(String floor) {
        super.setFloor(floor);
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
