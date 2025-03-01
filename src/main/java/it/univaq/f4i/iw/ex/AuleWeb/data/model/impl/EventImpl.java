package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.*;
import it.univaq.f4i.iw.framework.data.DataItemImpl;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventImpl extends DataItemImpl<Integer> implements Event {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    private Type eventType;
    private Manager eventManager;
    private Course course;
    private Integer masterId;
    private String name;
    private Classroom classroom; // New field for Classroom

    public EventImpl() {
        super();
        date = null;
        startTime = null;
        endTime = null;
        description = "";
        eventType = null;
        eventManager = null;
        course = null;
        masterId = null;
        name = "";
        classroom = null; // Initialize to null
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Type getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(Type eventType) {
        this.eventType = eventType;
    }

    @Override
    public Manager getEventManager() {
        return eventManager;
    }

    @Override
    public void setEventManager(Manager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public Course getCourse() {
        return course;
    }

    @Override
    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public Integer getMasterId() {
        return masterId;
    }

    @Override
    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Classroom getClassroom() {
        return classroom;
    }

    @Override
    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }
}
