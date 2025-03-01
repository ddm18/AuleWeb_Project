package it.univaq.f4i.iw.ex.AuleWeb.data.model;

import it.univaq.f4i.iw.framework.data.DataItem;
import java.time.LocalDate;
import java.time.LocalTime;

public interface Event extends DataItem<Integer> {

    // Use LocalDate for date-only values
    LocalDate getDate();
    void setDate(LocalDate date);

    // Use LocalTime for time-only values
    LocalTime getStartTime();
    void setStartTime(LocalTime startTime);

    LocalTime getEndTime();
    void setEndTime(LocalTime endTime);

    String getDescription();
    void setDescription(String description);

    Type getEventType();
    void setEventType(Type type);

    Manager getEventManager();
    void setEventManager(Manager eventManager);

    Course getCourse();
    void setCourse(Course course);

    Integer getMasterId();
    void setMasterId(Integer masterId);
    
    String getName();
    void setName(String name);
    
    Classroom getClassroom();
	void setClassroom(Classroom classroom);
	
}
