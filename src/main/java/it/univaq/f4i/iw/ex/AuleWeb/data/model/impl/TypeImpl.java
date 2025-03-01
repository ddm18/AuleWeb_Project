package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Type;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class TypeImpl extends DataItemImpl<Integer> implements Type {

    private String eventTypeName;

    public TypeImpl() {
        super();
        eventTypeName = "";
    }

    @Override
    public String getEventTypeName() {
        return eventTypeName;
    }

    @Override
    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }
}
