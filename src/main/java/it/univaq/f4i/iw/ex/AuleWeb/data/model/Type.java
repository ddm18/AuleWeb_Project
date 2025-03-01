package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Type extends DataItem<Integer> {

    String getEventTypeName();
    void setEventTypeName(String eventTypeName);
}
