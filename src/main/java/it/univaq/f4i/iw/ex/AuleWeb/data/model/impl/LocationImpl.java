package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Location;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class LocationImpl extends DataItemImpl<Integer> implements Location {

    private String place;
    private String building;
    private String floor;

    public LocationImpl() {
        super();
        place = "";
        building = "";
        floor = "";
    }

    @Override
    public String getPlace() {
        return place;
    }

    @Override
    public void setPlace(String place) {
        this.place = place;
    }

    @Override
    public String getBuilding() {
        return building;
    }

    @Override
    public void setBuilding(String building) {
        this.building = building;
    }

    @Override
    public String getFloor() {
        return floor;
    }

    @Override
    public void setFloor(String floor) {
        this.floor = floor;
    }
}
