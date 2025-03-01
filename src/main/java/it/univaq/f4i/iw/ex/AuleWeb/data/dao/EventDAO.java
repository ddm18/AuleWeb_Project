package it.univaq.f4i.iw.ex.AuleWeb.data.dao;


import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;
import it.univaq.f4i.iw.framework.data.DataException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface EventDAO {

    // Retrieve a single Event by its ID
    Event getEvent(int eventID) throws DataException;

    // Retrieve a list of all Events
    List<Event> getEvents() throws DataException;



    // Factory method to create an empty Event object
    Event createEvent() throws DataException;

    // CRUD operations for storing and deleting
    void storeEvent(Event event) throws DataException;

    void deleteEvent(int eventID) throws DataException;

	List<Event> getAllEvents() throws DataException;


	List<Event> getThisWeekEvents(int minusWeek) throws DataException;


	List<Event> getCurrentEvents() throws DataException;

	List<Event> getThisWeekEventsByCourse(int plusWeek, String course) throws DataException;

	List<Event> getEventsByDate(Date sqlDate) throws DataException;

	List<Event> getEventsByClassroomName(String actual_classroom, int weekcounter) throws DataException;

	List<Event> getEventsByClassroomGroupAndDateRange(int groupId, LocalDate startDate, LocalDate endDate)
			throws DataException;

	List<Event> getEventsByClassroomGroup(int groupId) throws DataException;


	List<Event> getRecurrentEvents(int masterId) throws DataException;

	

	void deleteEventAndRecurrence(int eventId) throws DataException;

	List<Event> getEventsByCourseAndDateRange(int courseId, LocalDate startDate, LocalDate endDate)
			throws DataException;

	List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) throws DataException;

	List<Event> getEventsByClassroomAndDate(int classroomId, LocalDate eventDate) throws DataException;

	List<Event> getThisWeekEventsByClassroomNameAndGroup(String actualClassroom, int weekCounter, String groupName)
			throws DataException;

	List<Event> getThisWeekEventsByCourseAndGroup(int weekCounter, String course, String groupName)
			throws DataException;

	


	
}
