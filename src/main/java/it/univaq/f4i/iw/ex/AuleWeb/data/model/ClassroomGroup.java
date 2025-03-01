package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface ClassroomGroup extends DataItem<Integer> {

    String getGroupName();
    void setGroupName(String group_name);

    String getGroupDescription();
    void setGroupDescription(String groupDescription);
}
