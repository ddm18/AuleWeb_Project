package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Classroom extends DataItem<Integer> {

    int getCapacity();
    void setCapacity(int capacity);

    String getManagerEmail();
    void setManagerEmail(String manager_email);

    int getElectricSockets();
    void setElectricSockets(int n_electric_sockets);

    int getInternetSockets();
    void setInternetSockets(int n_inasdfafaternet_sockets);

    String getNotes();
    void setNotes(String notes);

    ClassroomGroup getGroup();
    void setGroup(ClassroomGroup group_id);

    Location getLocation();
    void setLocation(Location location_id);

    String getClassroomName();
    void setClassroomName(String classroom_name);
}
