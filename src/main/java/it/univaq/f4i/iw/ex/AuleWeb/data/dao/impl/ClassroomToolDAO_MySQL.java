package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ClassroomToolDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomTool;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.ClassroomToolProxy;
import it.univaq.f4i.iw.framework.data.DAO;
import it.univaq.f4i.iw.framework.data.DataException;
import it.univaq.f4i.iw.framework.data.DataItemProxy;
import it.univaq.f4i.iw.framework.data.DataLayer;
import it.univaq.f4i.iw.framework.data.OptimisticLockException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassroomToolDAO_MySQL extends DAO implements ClassroomToolDAO {

    private PreparedStatement sClassroomToolByID, sClassroomTools, sToolsByClassroom, iClassroomTool, uClassroomTool,dClassroomTool;

    public ClassroomToolDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Precompile SQL statements
            sClassroomToolByID = connection.prepareStatement("SELECT * FROM classroomtool WHERE id = ?");
            sClassroomTools = connection.prepareStatement("SELECT id FROM classroomtool");
            sToolsByClassroom = connection.prepareStatement("SELECT id FROM classroomtool WHERE classroom_id = ?");
            iClassroomTool = connection.prepareStatement(
                "INSERT INTO classroomtool (classroom_id, tool_id, version) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            uClassroomTool = connection.prepareStatement(
                "UPDATE classroomtool SET classroom_id = ?, tool_id = ?, version = ? WHERE id = ? AND version = ?"
            );
            dClassroomTool = connection.prepareStatement(("DELETE FROM classroomtool WHERE id = ?"));
            		
           
        } catch (SQLException ex) {
            throw new DataException("Error initializing ClassroomToolDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sClassroomToolByID != null) sClassroomToolByID.close();
            if (sClassroomTools != null) sClassroomTools.close();
            if (sToolsByClassroom != null) sToolsByClassroom.close();
            if (iClassroomTool != null) iClassroomTool.close();
            if (uClassroomTool != null) uClassroomTool.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in ClassroomToolDAO", ex);
        }
        super.destroy();
    }

    @Override
    public ClassroomTool createClassroomTool() {
        return new ClassroomToolProxy(getDataLayer());
    }

    private ClassroomToolProxy createClassroomTool(ResultSet rs) throws DataException {
        ClassroomToolProxy classroomTool = (ClassroomToolProxy) createClassroomTool();
        try {
            classroomTool.setKey(rs.getInt("id"));
            classroomTool.setClassroomKey(rs.getInt("classroom_id"));
            classroomTool.setToolKey(rs.getInt("tool_id"));
            classroomTool.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create ClassroomTool from ResultSet", ex);
        }
        return classroomTool;
    }

    @Override
    public ClassroomTool getClassroomTool(int classroomToolID) throws DataException {
        ClassroomTool classroomTool = null;
        if (dataLayer.getCache().has(ClassroomTool.class, classroomToolID)) {
            classroomTool = dataLayer.getCache().get(ClassroomTool.class, classroomToolID);
        } else {
            try {
                sClassroomToolByID.setInt(1, classroomToolID);
                try (ResultSet rs = sClassroomToolByID.executeQuery()) {
                    if (rs.next()) {
                        classroomTool = createClassroomTool(rs);
                        dataLayer.getCache().add(ClassroomTool.class, classroomTool);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load ClassroomTool by ID", ex);
            }
        }
        return classroomTool;
    }

    @Override
    public List<ClassroomTool> getClassroomTools() throws DataException {
        List<ClassroomTool> classroomTools = new ArrayList<>();
        try (ResultSet rs = sClassroomTools.executeQuery()) {
            while (rs.next()) {
                classroomTools.add(getClassroomTool(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load ClassroomTools", ex);
        }
        return classroomTools;
    }

    @Override
    public List<ClassroomTool> getToolsByClassroom(int classroomID) throws DataException {
        List<ClassroomTool> classroomTools = new ArrayList<>();
        try {
            sToolsByClassroom.setInt(1, classroomID);
            try (ResultSet rs = sToolsByClassroom.executeQuery()) {
                while (rs.next()) {
                    classroomTools.add(getClassroomTool(rs.getInt("id")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Tools by Classroom ID", ex);
        }
        return classroomTools;
    }

    @Override
    public void storeClassroomTool(ClassroomTool classroomTool) throws DataException {
        try {
            if (classroomTool.getKey() != null && classroomTool.getKey() > 0) { // Update
                if (classroomTool instanceof DataItemProxy && !((DataItemProxy) classroomTool).isModified()) {
                    return;
                }

                uClassroomTool.setInt(1, classroomTool.getClassroom().getKey());
                uClassroomTool.setInt(2, classroomTool.getTool().getKey());

                long currentVersion = classroomTool.getVersion();
                long nextVersion = currentVersion + 1;

                uClassroomTool.setLong(3, nextVersion);
                uClassroomTool.setInt(4, classroomTool.getKey());
                uClassroomTool.setLong(5, currentVersion);

                if (uClassroomTool.executeUpdate() == 0) {
                    throw new OptimisticLockException(classroomTool);
                } else {
                    classroomTool.setVersion(nextVersion);
                }
            } else { // Insert
                iClassroomTool.setInt(1, classroomTool.getClassroom().getKey());
                iClassroomTool.setInt(2, classroomTool.getTool().getKey());
                iClassroomTool.setLong(3, 1); // Initial version

                if (iClassroomTool.executeUpdate() == 1) {
                    try (ResultSet keys = iClassroomTool.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            classroomTool.setKey(key);
                            dataLayer.getCache().add(ClassroomTool.class, classroomTool);
                        }
                    }
                }
            }

            if (classroomTool instanceof DataItemProxy) {
                ((DataItemProxy) classroomTool).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store classroom tool", ex);
        }
    }

    @Override
    public void deleteClassroomTool(int classroomToolID) throws DataException {
        try {
            // Set the classroomToolID in the prepared statement
            dClassroomTool.setInt(1, classroomToolID);
            
            // Execute the delete statement
            dClassroomTool.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete classroom tool", ex);
        }
    }
}
