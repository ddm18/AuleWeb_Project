package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.CourseImpl;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

public class CourseProxy extends CourseImpl implements DataItemProxy {

    protected boolean modified;
    protected DataLayer dataLayer;

    public CourseProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Overriding the setter to track modifications
    @Override
    public void setCourseName(String courseName) {
        super.setCourseName(courseName);
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
