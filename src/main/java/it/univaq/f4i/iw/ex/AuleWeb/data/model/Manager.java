package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Manager extends DataItem<Integer> {

    String getManagerName();
    void setManagerName(String managerName);

    String getManagerEmail();
    void setManagerEmail(String managerEmail);
}
