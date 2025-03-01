package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Classroom;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomGroup;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Location;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class ClassroomImpl extends DataItemImpl<Integer> implements Classroom {

    private int capacity;
    private String managerEmail;
    private int electricSockets;
    private int internetSockets;
    private String notes;
    private ClassroomGroup group;
    private Location location;
    private String classroomName;

    public ClassroomImpl() {
        super();
        capacity = 0;
        managerEmail = "";
        electricSockets = 0;
        internetSockets = 0;
        notes = "";
        group = null;
        location = null;
        classroomName = "";
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String getManagerEmail() {
        return managerEmail;
    }

    @Override
    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    @Override
    public int getElectricSockets() {
        return electricSockets;
    }

    @Override
    public void setElectricSockets(int electricSockets) {
        this.electricSockets = electricSockets;
    }

    @Override
    public int getInternetSockets() {
        return internetSockets;
    }

    @Override
    public void setInternetSockets(int internetSockets) {
        this.internetSockets = internetSockets;
    }

    @Override
    public String getNotes() {
        return notes;
    }

    @Override
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public ClassroomGroup getGroup() {
        return group;
    }

    @Override
    public void setGroup(ClassroomGroup group) {
        this.group = group;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String getClassroomName() {
        return classroomName;
    }

    @Override
    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }
}
