package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;
import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ClassroomGroupDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.ClassroomGroup;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.ClassroomGroupProxy;
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

public class ClassroomGroupDAO_MySQL extends DAO implements ClassroomGroupDAO {

    private PreparedStatement sClassroomGroupByID, sClassroomGroups, sClassroomGroupsByName, iGroup, uGroup, dGroup;

    public ClassroomGroupDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Prepare SQL statements
            sClassroomGroupByID = connection.prepareStatement("SELECT * FROM classroom_group WHERE ID = ?");
            sClassroomGroups = connection.prepareStatement("SELECT ID FROM classroom_group");
            sClassroomGroupsByName = connection.prepareStatement("SELECT ID FROM classroom_group WHERE group_name = ?");
            uGroup = connection.prepareStatement(
                "UPDATE classroom_group SET group_name = ?, groupDescription = ?, version = ? WHERE ID = ? AND version = ?"
            );
            iGroup = connection.prepareStatement(
                "INSERT INTO classroom_group (group_name, groupDescription, version) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            dGroup = connection.prepareStatement("DELETE FROM classroom_group WHERE ID = ?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing classroom group data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sClassroomGroupByID != null) sClassroomGroupByID.close();
            if (sClassroomGroups != null) sClassroomGroups.close();
            if (sClassroomGroupsByName != null) sClassroomGroupsByName.close();
            if (iGroup != null) iGroup.close();
            if (uGroup != null) uGroup.close();
            if (dGroup != null) dGroup.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in ClassroomGroupDAO", ex);
        }
        super.destroy();
    }

    @Override
    public ClassroomGroup createClassroomGroup() {
        return new ClassroomGroupProxy(getDataLayer());
    }

    private ClassroomGroupProxy createClassroomGroup(ResultSet rs) throws DataException {
        try {
            ClassroomGroupProxy group = (ClassroomGroupProxy) createClassroomGroup();
            group.setKey(rs.getInt("ID"));
            group.setGroupName(rs.getString("group_name"));
            group.setGroupDescription(rs.getString("groupDescription"));
            group.setVersion(rs.getLong("version"));
            return group;
        } catch (SQLException ex) {
            throw new DataException("Unable to create classroom group object from ResultSet", ex);
        }
    }

    @Override
    public ClassroomGroup getClassroomGroup(int groupKey) throws DataException {
        ClassroomGroup group = null;
        if (dataLayer.getCache().has(ClassroomGroup.class, groupKey)) {
            group = dataLayer.getCache().get(ClassroomGroup.class, groupKey);
        } else {
            try {
                sClassroomGroupByID.setInt(1, groupKey);
                try (ResultSet rs = sClassroomGroupByID.executeQuery()) {
                    if (rs.next()) {
                        group = createClassroomGroup(rs);
                        dataLayer.getCache().add(ClassroomGroup.class, group);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load classroom group by ID", ex);
            }
        }
        return group;
    }

    @Override
    public ClassroomGroup getClassroomGroupByName(String groupName) throws DataException {
        ClassroomGroup group = null;
        try {
            sClassroomGroupsByName.setString(1, groupName);
            try (ResultSet rs = sClassroomGroupsByName.executeQuery()) {
                if (rs.next()) {
                    group = getClassroomGroup(rs.getInt("ID"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load classroom group by name", ex);
        }
        return group;
    }

    @Override
    public List<ClassroomGroup> getClassroomGroups() throws DataException {
        List<ClassroomGroup> groups = new ArrayList<>();
        try (ResultSet rs = sClassroomGroups.executeQuery()) {
            while (rs.next()) {
                groups.add(getClassroomGroup(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load classroom groups", ex);
        }
        return groups;
    }

    @Override
    public void storeClassroomGroup(ClassroomGroup group) throws DataException {
        try {
            if (group.getKey() != null && group.getKey() > 0) { // Update
                if (group instanceof DataItemProxy && !((DataItemProxy) group).isModified()) {
                    return;
                }

                uGroup.setString(1, group.getGroupName());
                uGroup.setString(2, group.getGroupDescription());
                long currentVersion = group.getVersion();
                long nextVersion = currentVersion + 1;

                uGroup.setLong(3, nextVersion);
                uGroup.setInt(4, group.getKey());
                uGroup.setLong(5, currentVersion);

                if (uGroup.executeUpdate() == 0) {
                    throw new OptimisticLockException(group); // Handle version mismatch
                } else {
                    group.setVersion(nextVersion);
                }
            } else { // Insert
                iGroup.setString(1, group.getGroupName());
                iGroup.setString(2, group.getGroupDescription());
                iGroup.setLong(3, 1); // Initial version

                if (iGroup.executeUpdate() == 1) {
                    try (ResultSet keys = iGroup.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            group.setKey(key);
                            dataLayer.getCache().add(ClassroomGroup.class, group);
                        }
                    }
                }
            }

            // Reset the modified flag for proxies
            if (group instanceof DataItemProxy) {
                ((DataItemProxy) group).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store classroom group", ex);
        }
    }

    @Override
    public void deleteClassroomGroup(int groupID) throws DataException {
        try {
            dGroup.setInt(1, groupID);
            dGroup.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete classroom group", ex);
        }
    }
}
