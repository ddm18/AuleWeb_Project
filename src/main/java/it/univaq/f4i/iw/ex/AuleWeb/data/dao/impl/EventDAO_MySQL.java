package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.EventDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Event;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.EventProxy;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.TypeProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.*;

public class EventDAO_MySQL extends DAO implements EventDAO {

	private PreparedStatement sEventByID, sEvents, sEventsByType, sEventsByCourse, sEventByWeek, sCurrentEvents, sEventyByWeekByCourse, sEventByDate, sEventByClassroomAndDate,
    sEventsByClassroomGroup, sEventsByClassroomGroupAndDateRange, iEvent, uEvent,sRecurrentEventsByMasterId,dEventAndRecurrence,dEventById,
    sEventsByCourseAndDateRange,sEventsByDateRange,sEventsByClassroomAndDate,sEventByWeekByClassroomAndGroup,sEventByWeekByCourseAndGroup;

public EventDAO_MySQL(DataLayer d) {
	super(d);
	}

@Override
public void init() throws DataException {
	try {
		super.init();
			
			// Initialize prepared statements
			sEventByID = connection.prepareStatement("SELECT * FROM event WHERE id = ?");
			sEvents = connection.prepareStatement("SELECT id FROM event");
			sEventsByType = connection.prepareStatement("SELECT id FROM event WHERE type_id = ?");
			sEventsByCourse = connection.prepareStatement("SELECT id FROM event WHERE course_id = ?");
			sEventByWeek = connection.prepareStatement("SELECT * FROM event WHERE date >= ? AND date <= ?");
			sCurrentEvents = connection.prepareStatement("SELECT * FROM event WHERE date = ? AND start_time <= ? AND end_time >= ? ORDER BY start_time");
			sEventyByWeekByCourse = connection.prepareStatement(
			"SELECT * FROM event JOIN course ON event.course_id = course.id WHERE date >= ? AND date <= ? AND course_name = ?");
			sEventByDate = connection.prepareCall("SELECT id FROM event WHERE date = ?");
			sEventByClassroomAndDate = connection.prepareCall(
			"SELECT event.id FROM event JOIN classroom ON classroom.id = event.classroom_id WHERE classroom.classroom_name = ? AND date >= ? AND date <= ?");
	        sEventsByDateRange = connection.prepareStatement("SELECT * FROM event WHERE date BETWEEN ? AND ?");
			// New prepared statements
			sEventsByClassroomGroup = connection.prepareStatement(
			"SELECT e.* FROM event e " +
			"JOIN classroom c ON e.classroom_id = c.id " +
			"WHERE c.group_id = ?"
			);
			sEventsByClassroomGroupAndDateRange = connection.prepareStatement(
			"SELECT e.* FROM event e " +
			"JOIN classroom c ON e.classroom_id = c.id " +
			"WHERE c.group_id = ? AND e.date BETWEEN ? AND ?"
			);
			
			iEvent = connection.prepareStatement(
			"INSERT INTO event (name, date, start_time, end_time, description, master_id, course_id, event_manager_id, event_type_id, classroom_id, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
			PreparedStatement.RETURN_GENERATED_KEYS
			);
			uEvent = connection.prepareStatement(
			"UPDATE event SET name = ?, date = ?, start_time = ?, end_time = ?, description = ?, master_id = ?, course_id = ?, event_manager_id = ?, event_type_id = ?, classroom_id = ?, version = ? WHERE id = ? AND version = ?"
			);
	        sRecurrentEventsByMasterId = connection.prepareStatement("SELECT * FROM event WHERE master_id = ?");
	        dEventAndRecurrence = connection.prepareStatement(
	        	    "DELETE e1 FROM event e1\n"
	        	    + "LEFT JOIN event e2 ON e1.master_id = e2.master_id\n"
	        	    + "WHERE e1.id = ? OR e2.id = ?;"
	        	);
	        dEventById = connection.prepareStatement("DELETE FROM event WHERE id = ?");
	        sEventsByCourseAndDateRange = connection.prepareStatement("SELECT * FROM event WHERE course_id = ? AND date BETWEEN ? AND ?");
	        sEventsByClassroomAndDate = connection.prepareStatement("SELECT * FROM event WHERE classroom_id = ? AND date = ?");
	        sEventByWeekByCourseAndGroup = connection.prepareStatement(
	        	    "SELECT e.* FROM event e " +
	        	    "JOIN course c ON e.course_id = c.id " +
	        	    "JOIN classroom cl ON e.classroom_id = cl.id " +
	        	    "JOIN classroom_group cg ON cl.group_id = cg.id " +
	        	    "WHERE e.date >= ? AND e.date <= ? AND c.course_name = ? AND cg.group_name = ?"
	        	);

	        sEventByWeekByClassroomAndGroup = connection.prepareStatement(
	        	    "SELECT e.* FROM event e " +
	        	    "JOIN classroom cl ON e.classroom_id = cl.id " +
	        	    "JOIN classroom_group cg ON cl.group_id = cg.id " +
	        	    "WHERE e.date >= ? AND e.date <= ? AND cl.classroom_name = ? AND cg.group_name = ?"
	        	);

		} catch (SQLException ex) {
		throw new DataException("Error initializing event data layer", ex);
		}
	}

	@Override
	public void destroy() throws DataException {
	    try {
	        if (sEventByID != null) sEventByID.close();
	        if (sEvents != null) sEvents.close();
	        if (sEventsByType != null) sEventsByType.close();
	        if (sEventsByCourse != null) sEventsByCourse.close();
	        if (sEventByWeek != null) sEventByWeek.close();
	        if (sCurrentEvents != null) sCurrentEvents.close();
	        if (sEventyByWeekByCourse != null) sEventyByWeekByCourse.close();
	        if (sEventByDate != null) sEventByDate.close();
	        if (sEventByClassroomAndDate != null) sEventByClassroomAndDate.close();
	        if (sEventsByClassroomGroup != null) sEventsByClassroomGroup.close();
	        if (sEventsByClassroomGroupAndDateRange != null) sEventsByClassroomGroupAndDateRange.close();
	        if (sRecurrentEventsByMasterId != null) sRecurrentEventsByMasterId.close();
	        if (dEventAndRecurrence != null) dEventAndRecurrence.close();
	        if (dEventById != null) dEventById.close();
	        if (sEventsByCourseAndDateRange != null) sEventsByCourseAndDateRange.close();
	        
	    } catch (SQLException ex) {
	        throw new DataException("Error closing prepared statements in EventDAO", ex);
	    }
	    super.destroy();
	}

    @Override
    public Event createEvent() throws DataException {
        return new EventProxy(getDataLayer());
    }

    private EventProxy createEvent(ResultSet rs) throws DataException {
    	EventProxy eventProxy = (EventProxy) createEvent();
        try {
            eventProxy.setKey(rs.getInt("id"));
            eventProxy.setName(rs.getString("name"));
            eventProxy.setDate(rs.getObject("date", LocalDate.class));
            eventProxy.setStartTime(rs.getObject("start_time", LocalTime.class));
            eventProxy.setEndTime(rs.getObject("end_time", LocalTime.class));
            eventProxy.setDescription(rs.getString("description"));
            eventProxy.setMasterId(rs.getInt("master_id"));
            eventProxy.setCourseKey(rs.getInt("course_id"));            
            eventProxy.setManagerKey(rs.getInt("event_manager_id")); 
            eventProxy.setTypeKey(rs.getInt("event_type_id"));       
            eventProxy.setClassroomKey(rs.getInt("classroom_id"));
            eventProxy.setVersion(rs.getLong("version"));
            
        } catch (SQLException ex) {
            throw new DataException("Unable to create event from ResultSet", ex);
        }
        return eventProxy;
    }

    @Override
    public Event getEvent(int eventID) throws DataException {
        Event event = null;
        if (dataLayer.getCache().has(Event.class, eventID)) {
            event = dataLayer.getCache().get(Event.class, eventID);
        } else {
            try {
                sEventByID.setInt(1, eventID);
                try (ResultSet rs = sEventByID.executeQuery()) {
                    if (rs.next()) {
                        event = createEvent(rs);
                        dataLayer.getCache().add(Event.class, event);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load event by ID", ex);
            }
        }
        return event;
    }

    @Override
    public List<Event> getEvents() throws DataException {
        List<Event> events = new ArrayList<>();
        try (ResultSet rs = sEvents.executeQuery()) {
            while (rs.next()) {
                events.add(getEvent(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load events", ex);
        }
        return events;
    }
    
    @Override
    public List<Event> getAllEvents() throws DataException {
        List<Event> events = new ArrayList<>();
        try (ResultSet rs = sEvents.executeQuery()) {
            while (rs.next()) {
                Event event = getEvent(rs.getInt("id"));
                events.add(event);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all events", ex);
        }
        return events;
    }
    
    @Override
    public List<Event> getEventsByDate(Date date) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
        	sEventByDate.setDate(1, date);
        	try (ResultSet rs = sEventByDate.executeQuery()){
            while (rs.next()) {
                Event event = getEvent(rs.getInt("id"));
                events.add(event);
            }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all events", ex);
        }
        return events;
    }
    @Override
    public List<Event> getEventsByCourseAndDateRange(int courseId, LocalDate startDate, LocalDate endDate) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Set parameters for the query
            sEventsByCourseAndDateRange.setInt(1, courseId);
            sEventsByCourseAndDateRange.setDate(2, java.sql.Date.valueOf(startDate));
            sEventsByCourseAndDateRange.setDate(3, java.sql.Date.valueOf(endDate));

            // Execute the query and process the results
            try (ResultSet rs = sEventsByCourseAndDateRange.executeQuery()) {
                while (rs.next()) {
                    events.add(createEvent(rs)); // Reuse the existing createEvent method
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve events for course ID " + courseId +
                                     " in the date range " + startDate + " to " + endDate, ex);
        }
        return events;
    }

    
    @Override
    public List<Event> getRecurrentEvents(int masterId) throws DataException {
        List<Event> recurrentEvents = new ArrayList<>();
        try {
            // Set the master_id and date parameters
            sRecurrentEventsByMasterId.setInt(1, masterId);
            

            // Execute the query
            try (ResultSet rs = sRecurrentEventsByMasterId.executeQuery()) {
                while (rs.next()) {
                    // Create and add events to the list
                    Event event = createEvent(rs);
                    recurrentEvents.add(event);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve recurrent events by master_id: " + masterId );
        }
        return recurrentEvents;
    }

    @Override
    public List<Event> getEventsByClassroomAndDate(int classroomId, LocalDate eventDate) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Set parameters for the query
            sEventsByClassroomAndDate.setInt(1, classroomId);
            sEventsByClassroomAndDate.setDate(2, java.sql.Date.valueOf(eventDate));

            // Execute the query and process the results
            try (ResultSet rs = sEventsByClassroomAndDate.executeQuery()) {
                while (rs.next()) {
                    events.add(createEvent(rs)); // Use the existing createEvent method
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve events for classroom ID " + classroomId + " on date " + eventDate, ex);
        }
        return events;
    }
    

    @Override
    public List<Event> getThisWeekEvents(int plusWeek) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Get the current date adjusted by `plusWeek`
            LocalDate now = LocalDate.now().plusWeeks(plusWeek);
            
            // Calculate Monday as the start of the week and Sunday as the end
            LocalDate startOfMonday = now.with(DayOfWeek.MONDAY);
            LocalDate endOfSunday = now.with(DayOfWeek.SUNDAY);

            sEventByWeek.setDate(1, Date.valueOf(startOfMonday));
            sEventByWeek.setDate(2, Date.valueOf(endOfSunday)); 

            // Execute the query
            try (ResultSet rs = sEventByWeek.executeQuery()) {
                while (rs.next()) {
                    Event event = getEvent(rs.getInt("id")); // Checking if in cache
                    events.add(event);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load weekly events", ex);
        }
        return events;
    }
    @Override
    public List<Event> getThisWeekEventsByCourseAndGroup(int weekCounter, String course, String groupName) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Calculate the start and end of the week
            LocalDate now = LocalDate.now().plusWeeks(weekCounter);
            LocalDate startOfMonday = now.with(DayOfWeek.MONDAY);
            LocalDate endOfSunday = now.with(DayOfWeek.SUNDAY);

            // Set parameters for the prepared statement
            sEventByWeekByCourseAndGroup.setDate(1, Date.valueOf(startOfMonday));
            sEventByWeekByCourseAndGroup.setDate(2, Date.valueOf(endOfSunday));
            sEventByWeekByCourseAndGroup.setString(3, course);
            sEventByWeekByCourseAndGroup.setString(4, groupName);

            // Execute the query and process results
            try (ResultSet rs = sEventByWeekByCourseAndGroup.executeQuery()) {
                while (rs.next()) {
                    events.add(createEvent(rs)); // Use the existing createEvent method
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve events for course " + course + 
                                     " and group " + groupName + " in week " + weekCounter, ex);
        }
        return events;
    }
    @Override
    public List<Event> getThisWeekEventsByClassroomNameAndGroup(String actualClassroom, int weekCounter, String groupName) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Calculate the start and end of the week
            LocalDate now = LocalDate.now().plusWeeks(weekCounter);
            LocalDate startOfMonday = now.with(DayOfWeek.MONDAY);
            LocalDate endOfSunday = now.with(DayOfWeek.SUNDAY);

            // Set parameters for the prepared statement
            sEventByWeekByClassroomAndGroup.setDate(1, Date.valueOf(startOfMonday));
            sEventByWeekByClassroomAndGroup.setDate(2, Date.valueOf(endOfSunday));
            sEventByWeekByClassroomAndGroup.setString(3, actualClassroom);
            sEventByWeekByClassroomAndGroup.setString(4, groupName);

            // Execute the query and process results
            try (ResultSet rs = sEventByWeekByClassroomAndGroup.executeQuery()) {
                while (rs.next()) {
                    events.add(createEvent(rs)); // Use the existing createEvent method
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve events for classroom " + actualClassroom + 
                                     " and group " + groupName + " in week " + weekCounter, ex);
        }
        return events;
    }


    @Override
    public List<Event> getThisWeekEventsByCourse(int plusWeek,String course) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Get the current date adjusted by `plusWeek`
            LocalDate now = LocalDate.now().plusWeeks(plusWeek);
            
            // Calculate Monday as the start of the week and Sunday as the end
            LocalDate startOfMonday = now.with(DayOfWeek.MONDAY);
            LocalDate endOfSunday = now.with(DayOfWeek.SUNDAY);

            sEventyByWeekByCourse.setDate(1, Date.valueOf(startOfMonday));
            sEventyByWeekByCourse.setDate(2, Date.valueOf(endOfSunday));
            sEventyByWeekByCourse.setString(3, course);

            // Execute the query
            try (ResultSet rs = sEventyByWeekByCourse.executeQuery()) {
                while (rs.next()) {
                    Event event = getEvent(rs.getInt("id")); // Checking if in cache
                    events.add(event);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load weekly events", ex);
        }
        return events;
    }
    
    
    @Override
    public List<Event> getCurrentEvents() throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Get the current date adjusted by `plusWeek`
            LocalDate now_date = LocalDate.now();
            LocalTime now_time = LocalTime.now();
            LocalTime three_hours_time = now_time.plusHours(3);

            // Set the start and end timestamps for the query
            sCurrentEvents.setDate(1, Date.valueOf(now_date)); 
            sCurrentEvents.setTime(2, Time.valueOf(three_hours_time)); // iniziano prima di 3 ore da adesso
            sCurrentEvents.setTime(3, Time.valueOf(now_time)); // finiscono dopo di ora

            // Execute the query
            try (ResultSet rs = sCurrentEvents.executeQuery()) {
                while (rs.next()) {
                    Event event = getEvent(rs.getInt("id")); // Checking if in cache
                    events.add(event);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load weekly events", ex);
        }
        return events;
    }
    
    @Override
    public List<Event> getEventsByClassroomName(String actual_classroom, int plusWeek) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            LocalDate now = LocalDate.now().plusWeeks(plusWeek);

            // Calculate Monday as the start of the week and Sunday as the end
            LocalDate startOfMonday = now.with(DayOfWeek.MONDAY);
            LocalDate endOfSunday = now.with(DayOfWeek.SUNDAY);

            // Set parameters for the prepared statement
            sEventByClassroomAndDate.setString(1, actual_classroom);
            sEventByClassroomAndDate.setDate(2, Date.valueOf(startOfMonday));
            sEventByClassroomAndDate.setDate(3, Date.valueOf(endOfSunday));

            // Execute the query
            try (ResultSet rs = sEventByClassroomAndDate.executeQuery()) {
                while (rs.next()) {
                    Event event = getEvent(rs.getInt("id")); // Use cache if available
                    events.add(event);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load events for classroom: " + actual_classroom, ex);
        }
        return events;
    }
    @Override
    public List<Event> getEventsByClassroomGroup(int groupId) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            sEventsByClassroomGroup.setInt(1, groupId);
            try (ResultSet rs = sEventsByClassroomGroup.executeQuery()) {
                while (rs.next()) {
                    events.add(createEvent(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve events by classroom group (department)", ex);
        }
        return events;
    }
    
    @Override
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            // Set parameters for the query
            sEventsByDateRange.setDate(1, java.sql.Date.valueOf(startDate));
            sEventsByDateRange.setDate(2, java.sql.Date.valueOf(endDate));

            // Execute the query and process the results
            try (ResultSet rs = sEventsByDateRange.executeQuery()) {
                while (rs.next()) {
                    events.add(createEvent(rs)); // Reuse the existing createEvent method
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve events in the date range " 
                                     + startDate + " to " + endDate, ex);
        }
        return events;
    }


    @Override
    public List<Event> getEventsByClassroomGroupAndDateRange(int groupId, LocalDate startDate, LocalDate endDate) throws DataException {
        List<Event> events = new ArrayList<>();
        try {
            sEventsByClassroomGroupAndDateRange.setInt(1, groupId);
            sEventsByClassroomGroupAndDateRange.setDate(2, java.sql.Date.valueOf(startDate));
            sEventsByClassroomGroupAndDateRange.setDate(3, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = sEventsByClassroomGroupAndDateRange.executeQuery()) {
                while (rs.next()) {
                    events.add(createEvent(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve events by classroom group (department) and date range", ex);
        }
        return events;
    }
	

    
    @Override
    public void storeEvent(Event event) throws DataException {
        try {
            if (event.getKey() != null && event.getKey() > 0) { // Update existing event
                if (event instanceof DataItemProxy && !((DataItemProxy) event).isModified()) {
                    return; // Skip if no modifications
                }

                uEvent.setString(1, event.getName());
                uEvent.setDate(2, Date.valueOf(event.getDate()));
                uEvent.setTime(3, Time.valueOf(event.getStartTime()));
                uEvent.setTime(4, Time.valueOf(event.getEndTime()));
                uEvent.setString(5, event.getDescription());

                if (event.getMasterId() != null) {
                    uEvent.setInt(6, event.getMasterId());
                } else {
                    uEvent.setNull(6, java.sql.Types.INTEGER);
                }

                if (event.getCourse() != null) {
                    uEvent.setInt(7, event.getCourse().getKey());
                } else {
                    uEvent.setNull(7, java.sql.Types.INTEGER);
                }

                if (event.getEventManager() != null) {
                    uEvent.setInt(8, event.getEventManager().getKey());
                } else {
                    uEvent.setNull(8, java.sql.Types.INTEGER);
                }

                if (event.getEventType() != null) {
                    uEvent.setInt(9, event.getEventType().getKey());
                } else {
                    uEvent.setNull(9, java.sql.Types.INTEGER);
                }

                if (event.getClassroom() != null) {
                    uEvent.setInt(10, event.getClassroom().getKey());
                } else {
                    uEvent.setNull(10, java.sql.Types.INTEGER);
                }

                long currentVersion = event.getVersion();
                long nextVersion = currentVersion + 1;

                uEvent.setLong(11, nextVersion);
                uEvent.setInt(12, event.getKey());
                uEvent.setLong(13, currentVersion);

                if (uEvent.executeUpdate() == 0) {
                    throw new OptimisticLockException(event);
                } else {
                    event.setVersion(nextVersion);
                }
            } else {

            	iEvent.setString(1, event.getName());

            	// 1) Handle date carefully
            	if (event.getDate() != null) {
            	    iEvent.setDate(2, Date.valueOf(event.getDate())); 
            	} else {
            	    // pass a NULL if the date is optional
            	    iEvent.setNull(2, java.sql.Types.DATE);
            	}

            	// 2) Handle start_time carefully
            	if (event.getStartTime() != null) {
            	    iEvent.setTime(3, Time.valueOf(event.getStartTime()));
            	} else {
            	    iEvent.setNull(3, java.sql.Types.TIME);
            	}

            	// 3) Handle end_time carefully
            	if (event.getEndTime() != null) {
            	    iEvent.setTime(4, Time.valueOf(event.getEndTime()));
            	} else {
            	    iEvent.setNull(4, java.sql.Types.TIME);
            	}

            	// 4) Description (string) can be empty or null, so decide how to handle that
            	iEvent.setString(5, event.getDescription() != null ? event.getDescription() : "");

            	// 5) Check masterId, course, etc. are either set or null (same logic)
            	if (event.getMasterId() != null) {
            	    iEvent.setInt(6, event.getMasterId());
            	} else {
            	    iEvent.setNull(6, java.sql.Types.INTEGER);
            	}

            	if (event.getCourse() != null) {
            	    iEvent.setInt(7, event.getCourse().getKey());
            	} else {
            	    iEvent.setNull(7, java.sql.Types.INTEGER);
            	}

            	if (event.getEventManager() != null) {
            	    iEvent.setInt(8, event.getEventManager().getKey());
            	} else {
            	    iEvent.setNull(8, java.sql.Types.INTEGER);
            	}

            	if (event.getEventType() != null) {
            	    iEvent.setInt(9, event.getEventType().getKey());
            	} else {
            	    iEvent.setNull(9, java.sql.Types.INTEGER);
            	}

            	if (event.getClassroom() != null) {
            	    iEvent.setInt(10, event.getClassroom().getKey());
            	} else {
            	    iEvent.setNull(10, java.sql.Types.INTEGER);
            	}

            	iEvent.setLong(11, 1); // initial version

            	// Execute
            	if (iEvent.executeUpdate() == 1) {
            	    try (ResultSet keys = iEvent.getGeneratedKeys()) {
            	        if (keys.next()) {
            	            event.setKey(keys.getInt(1));
            	            dataLayer.getCache().add(Event.class, event);
            	        }
            	    }
            	}
            }

            // Reset modification flag
            if (event instanceof DataItemProxy) {
                ((DataItemProxy) event).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store event", ex);
        }
    }



    @Override
    public void deleteEvent(int eventId) throws DataException {
        try {
            // Set the parameter for the prepared statement
            dEventById.setInt(1, eventId);

            // Execute the deletion
            dEventById.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete event with ID: " + eventId, ex);
        }
    }

	
	@Override
	public void deleteEventAndRecurrence(int eventId) throws DataException {
	    try {
	        // Set the parameters for the prepared statement
	        dEventAndRecurrence.setInt(1, eventId);
	        dEventAndRecurrence.setInt(2, eventId);

	        // Execute the deletion
	        dEventAndRecurrence.executeUpdate();
	    } catch (SQLException ex) {
	        throw new DataException("Unable to delete event and its recurrences for event ID: " + eventId, ex);
	    }
	}


}