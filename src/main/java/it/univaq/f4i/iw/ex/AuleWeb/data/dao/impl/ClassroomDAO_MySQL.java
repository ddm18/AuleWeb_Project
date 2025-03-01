package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ClassroomDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Classroom;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.ClassroomProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDAO_MySQL extends DAO implements ClassroomDAO {

    private PreparedStatement sClassroomByID, sClassrooms, iClassroom, uClassroom,sClassroomByDepartment,sClassroomByDepartmentAndDateRange,sClassroomByName;

    public ClassroomDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Existing prepared statements
            sClassroomByID = connection.prepareStatement("SELECT * FROM classroom WHERE id = ?");
            sClassrooms = connection.prepareStatement("SELECT id FROM classroom");
            uClassroom = connection.prepareStatement(
                "UPDATE classroom SET classroom_name = ?, capacity = ?, manager_email = ?, n_electric_sockets = ?, n_internet_sockets = ?, notes = ?, group_id = ?, location_id = ?, version = ? WHERE id = ? AND version = ?"
            );
            iClassroom = connection.prepareStatement(
                "INSERT INTO classroom (classroom_name, capacity, manager_email, n_electric_sockets, n_internet_sockets, notes, group_id, location_id, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            // New prepared statements
            sClassroomByDepartment = connection.prepareStatement(
                "SELECT * FROM classroom WHERE group_id = ?"
            );
            sClassroomByDepartmentAndDateRange = connection.prepareStatement(
                "SELECT c.* FROM classroom c " +
                "JOIN event e ON c.id = e.classroom_id " +
                "WHERE c.group_id = ? AND e.event_date BETWEEN ? AND ?"
            );
            sClassroomByName = connection.prepareStatement("SELECT * FROM classroom WHERE classroom_name = ?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing classroom data layer", ex);
        }
    }


    @Override
    public void destroy() throws DataException {
        try {
            if (sClassroomByID != null) sClassroomByID.close();
            if (sClassrooms != null) sClassrooms.close();
            if (uClassroom != null) uClassroom.close();
            if (iClassroom != null) iClassroom.close();
            if (sClassroomByName != null) sClassroomByName.close();

            // New prepared statements cleanup
            if (sClassroomByDepartment != null) sClassroomByDepartment.close();
            if (sClassroomByDepartmentAndDateRange != null) sClassroomByDepartmentAndDateRange.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in ClassroomDAO", ex);
        }
        super.destroy();
    }

    public Classroom createClassroom() {
        return new ClassroomProxy(getDataLayer());
    }

    private ClassroomProxy createClassroom(ResultSet rs) throws DataException {
        ClassroomProxy classroomProxy = new ClassroomProxy(getDataLayer());
        try {
            classroomProxy.setKey(rs.getInt("id"));
            classroomProxy.setClassroomName(rs.getString("classroom_name"));
            classroomProxy.setCapacity(rs.getInt("capacity"));
            classroomProxy.setManagerEmail(rs.getString("manager_email"));
            classroomProxy.setElectricSockets(rs.getInt("n_electric_sockets"));
            classroomProxy.setInternetSockets(rs.getInt("n_internet_sockets"));
            classroomProxy.setNotes(rs.getString("notes"));
            classroomProxy.setGroupKey(rs.getInt("group_id")); // Correct foreign key for group
            classroomProxy.setLocationKey(rs.getInt("location_id")); // Correct foreign key for location
            classroomProxy.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create classroom from ResultSet", ex);
        }
        return classroomProxy;
    }

    @Override
    public Classroom getClassroom(int classroomID) throws DataException {
        Classroom classroom = null;
        if (dataLayer.getCache().has(Classroom.class, classroomID)) {
            classroom = dataLayer.getCache().get(Classroom.class, classroomID);
        } else {
            try {
                sClassroomByID.setInt(1, classroomID);
                try (ResultSet rs = sClassroomByID.executeQuery()) {
                    if (rs.next()) {
                        classroom = createClassroom(rs);
                        dataLayer.getCache().add(Classroom.class, classroom);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load classroom by ID", ex);
            }
        }
        return classroom;
    }

    public List<Classroom> getClassrooms() throws DataException {
        List<Classroom> classrooms = new ArrayList<>();
        try (ResultSet rs = sClassrooms.executeQuery()) {
            while (rs.next()) {
                classrooms.add(getClassroom(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load classrooms", ex);
        }
        return classrooms;
    }
    
    @Override
    public List<Classroom> getClassroomByDepartment(int departmentId) throws DataException {
        List<Classroom> classrooms = new ArrayList<>();
        try {
            sClassroomByDepartment.setInt(1, departmentId);
            try (ResultSet rs = sClassroomByDepartment.executeQuery()) {
                while (rs.next()) {
                    classrooms.add(createClassroom(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load classrooms by department ID", ex);
        }
        return classrooms;
    }
    
    @Override
    public Classroom getClassroomByName(String name) throws DataException {
        Classroom classroom = null;
        try {
            sClassroomByName.setString(1, name);
            try (ResultSet rs = sClassroomByName.executeQuery()) {
                if (rs.next()) {
                    classroom = createClassroom(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load classroom by name", ex);
        }
        return classroom;
    }


    @Override
    public List<Classroom> getClassroomByDepartmentAndDateRange(int departmentId, LocalDate startDate, LocalDate endDate) throws DataException {
        List<Classroom> classrooms = new ArrayList<>();
        try {
            sClassroomByDepartmentAndDateRange.setInt(1, departmentId);
            sClassroomByDepartmentAndDateRange.setDate(2, java.sql.Date.valueOf(startDate));
            sClassroomByDepartmentAndDateRange.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = sClassroomByDepartmentAndDateRange.executeQuery()) {
                while (rs.next()) {
                    classrooms.add(createClassroom(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load classrooms by department ID and date range", ex);
        }
        return classrooms;
    }


    @Override
    public void storeClassroom(Classroom classroom) throws DataException {
        try {
            if (classroom.getKey() != null && classroom.getKey() > 0) { // Update
                if (classroom instanceof DataItemProxy && !((DataItemProxy) classroom).isModified()) {
                    return;
                }

                uClassroom.setString(1, classroom.getClassroomName());
                uClassroom.setInt(2, classroom.getCapacity());
                uClassroom.setString(3, classroom.getManagerEmail());
                uClassroom.setInt(4, classroom.getElectricSockets());
                uClassroom.setInt(5, classroom.getInternetSockets());
                uClassroom.setString(6, classroom.getNotes());

                // Handle foreign keys with null checks
                if (classroom.getGroup() != null) {
                    uClassroom.setInt(7, classroom.getGroup().getKey());
                } else {
                    uClassroom.setNull(7, java.sql.Types.INTEGER);
                }

                if (classroom.getLocation() != null) {
                    uClassroom.setInt(8, classroom.getLocation().getKey());
                } else {
                    uClassroom.setNull(8, java.sql.Types.INTEGER);
                }

                long currentVersion = classroom.getVersion();
                long nextVersion = currentVersion + 1;

                uClassroom.setLong(9, nextVersion);
                uClassroom.setInt(10, classroom.getKey());
                uClassroom.setLong(11, currentVersion);

                if (uClassroom.executeUpdate() == 0) {
                    throw new OptimisticLockException(classroom);
                } else {
                    classroom.setVersion(nextVersion);
                }
            } else { // Insert
                iClassroom.setString(1, classroom.getClassroomName());
                iClassroom.setInt(2, classroom.getCapacity());
                iClassroom.setString(3, classroom.getManagerEmail());
                iClassroom.setInt(4, classroom.getElectricSockets());
                iClassroom.setInt(5, classroom.getInternetSockets());
                iClassroom.setString(6, classroom.getNotes());

                // Handle foreign keys with null checks
                if (classroom.getGroup() != null) {
                    iClassroom.setInt(7, classroom.getGroup().getKey());
                } else {
                    iClassroom.setNull(7, java.sql.Types.INTEGER);
                }

                if (classroom.getLocation() != null) {
                    iClassroom.setInt(8, classroom.getLocation().getKey());
                } else {
                    iClassroom.setNull(8, java.sql.Types.INTEGER);
                }

                iClassroom.setLong(9, 1); // Initial version

                if (iClassroom.executeUpdate() == 1) {
                    try (ResultSet keys = iClassroom.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            classroom.setKey(key);
                            dataLayer.getCache().add(Classroom.class, classroom);
                        }
                    }
                }
            }

            if (classroom instanceof DataItemProxy) {
                ((DataItemProxy) classroom).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store classroom", ex);
        }
    }
    

    @Override
    public void deleteClassroom(int classroomID) throws DataException {
        try (PreparedStatement dClassroom = connection.prepareStatement("DELETE FROM classroom WHERE id = ?")) {
            dClassroom.setInt(1, classroomID);
            dClassroom.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete classroom", ex);
        }
    }
}
