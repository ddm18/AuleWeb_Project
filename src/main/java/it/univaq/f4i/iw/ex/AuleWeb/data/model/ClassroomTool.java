package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;

public interface ClassroomTool extends DataItem<Integer> {

    Classroom getClassroom();
    void setClassroom(Classroom classroom);

    Tool getTool();
    void setTool(Tool tool);
}
