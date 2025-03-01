package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Location extends DataItem<Integer> {

    String getPlace();
    void setPlace(String place);

    String getBuilding();
    void setBuilding(String building);

    String getFloor();
    void setFloor(String floor);
}
