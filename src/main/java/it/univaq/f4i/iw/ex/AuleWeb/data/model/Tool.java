package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Tool extends DataItem<Integer> {

    String getToolName();
    void setToolName(String tool_name);
}
