package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomGroup;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class ClassroomGroupImpl extends DataItemImpl<Integer> implements ClassroomGroup {

    private String group_name;
    private String groupDescription;

    public ClassroomGroupImpl() {
        super();
        group_name = "";
        groupDescription = "";
    }

    @Override
    public String getGroupName() {
        return group_name;
    }

    @Override
    public void setGroupName(String group_name) {
        this.group_name = group_name;
    }

    @Override
    public String getGroupDescription() {
        return groupDescription;
    }

    @Override
    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }
}
