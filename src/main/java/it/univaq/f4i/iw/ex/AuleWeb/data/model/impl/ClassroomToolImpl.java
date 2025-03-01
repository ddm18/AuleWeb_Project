package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Classroom;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomTool;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Tool;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class ClassroomToolImpl extends DataItemImpl<Integer> implements ClassroomTool {

    private Classroom classroom;
    private Tool tool;

    public ClassroomToolImpl() {
        super();
        classroom = null;
        tool = null;
    }

    @Override
    public Classroom getClassroom() {
        return classroom;
    }

    @Override
    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    @Override
    public Tool getTool() {
        return tool;
    }

    @Override
    public void setTool(Tool tool) {
        this.tool = tool;
    } 
    

}
