package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface Course extends DataItem<Integer> {

    String getCourseName();
    void setCourseName(String courseName);
}
