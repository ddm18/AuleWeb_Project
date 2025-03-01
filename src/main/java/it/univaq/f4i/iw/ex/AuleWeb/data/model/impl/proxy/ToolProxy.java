package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.ToolImpl;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;

public class ToolProxy extends ToolImpl implements DataItemProxy {

    protected boolean modified;
    protected DataLayer dataLayer;

    public ToolProxy(DataLayer dataLayer) {
        super();
        this.dataLayer = dataLayer;
        this.modified = false;
    }

    // Track modifications for the key
    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    // Track modifications for the tool name
    @Override
    public void setToolName(String toolName) {
        super.setToolName(toolName);
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
