package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Manager;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class ManagerImpl extends DataItemImpl<Integer> implements Manager {

    private String managerName;
    private String managerEmail;

    public ManagerImpl() {
        super();
        managerName = "";
        managerEmail = "";
    }

    @Override
    public String getManagerName() {
        return managerName;
    }

    @Override
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    @Override
    public String getManagerEmail() {
        return managerEmail;
    }

    @Override
    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}
