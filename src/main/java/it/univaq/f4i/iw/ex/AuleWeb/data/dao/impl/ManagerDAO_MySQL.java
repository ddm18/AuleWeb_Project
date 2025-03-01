package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ManagerDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Manager;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.ManagerProxy;
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

public class ManagerDAO_MySQL extends DAO implements ManagerDAO {

    private PreparedStatement sManagerByID, sManagers, sManagerByName, uManager, iManager, dManager;
    private PreparedStatement sManagersByDepartment, sManagersByDepartmentAndDateRange;

    public ManagerDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Precompile SQL statements
            sManagerByID = connection.prepareStatement("SELECT * FROM manager WHERE id = ?");
            sManagers = connection.prepareStatement("SELECT id FROM manager");
            sManagerByName = connection.prepareStatement("SELECT id FROM manager WHERE manager_name = ?");
            uManager = connection.prepareStatement(
                "UPDATE manager SET manager_name = ?, manager_email = ?, version = ? WHERE id = ? AND version = ?"
            );
            iManager = connection.prepareStatement(
                "INSERT INTO manager (manager_name, manager_email, version) VALUES (?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            dManager = connection.prepareStatement("DELETE FROM manager WHERE id = ?");

         // Managers by Classroom Group (Department)
            sManagersByDepartment = connection.prepareStatement(
                "SELECT DISTINCT m.* FROM manager m " +
                "JOIN classroom cl ON m.manager_email = cl.manager_email " +
                "WHERE cl.group_id = ?"
            );

            // Managers by Classroom Group (Department) and Date Range
            sManagersByDepartmentAndDateRange = connection.prepareStatement(
                "SELECT DISTINCT m.* FROM manager m " +
                "JOIN classroom cl ON m.manager_email = cl.manager_email " +
                "JOIN event e ON e.classroom_id = cl.id " +
                "WHERE cl.group_id = ? AND e.event_date BETWEEN ? AND ?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing ManagerDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sManagerByID != null) sManagerByID.close();
            if (sManagers != null) sManagers.close();
            if (sManagerByName != null) sManagerByName.close();
            if (uManager != null) uManager.close();
            if (iManager != null) iManager.close();
            if (dManager != null) dManager.close();

            // New prepared statements cleanup
            if (sManagersByDepartment != null) sManagersByDepartment.close();
            if (sManagersByDepartmentAndDateRange != null) sManagersByDepartmentAndDateRange.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in ManagerDAO", ex);
        }
        super.destroy();
    }

    @Override
    public Manager createManager() {
        return new ManagerProxy(getDataLayer());
    }

    private ManagerProxy createManager(ResultSet rs) throws DataException {
        try {
            ManagerProxy manager = (ManagerProxy) createManager();
            manager.setKey(rs.getInt("id"));
            manager.setManagerName(rs.getString("manager_name"));
            manager.setManagerEmail(rs.getString("manager_email"));
            manager.setVersion(rs.getLong("version"));
            return manager;
        } catch (SQLException ex) {
            throw new DataException("Unable to create Manager from ResultSet", ex);
        }
    }

    @Override
    public Manager getManager(int managerID) throws DataException {
        Manager manager = null;
        if (dataLayer.getCache().has(Manager.class, managerID)) {
            manager = dataLayer.getCache().get(Manager.class, managerID);
        } else {
            try {
                sManagerByID.setInt(1, managerID);
                try (ResultSet rs = sManagerByID.executeQuery()) {
                    if (rs.next()) {
                        manager = createManager(rs);
                        dataLayer.getCache().add(Manager.class, manager);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load Manager by ID", ex);
            }
        }
        return manager;
    }

    @Override
    public Manager getManagerByName(String managerName) throws DataException {
        Manager manager = null;
        try {
            sManagerByName.setString(1, managerName);
            try (ResultSet rs = sManagerByName.executeQuery()) {
                if (rs.next()) {
                    manager = getManager(rs.getInt("id"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Manager by name: " + managerName, ex);
        }
        return manager;
    }

    @Override
    public List<Manager> getManagers() throws DataException {
        List<Manager> managers = new ArrayList<>();
        try (ResultSet rs = sManagers.executeQuery()) {
            while (rs.next()) {
                managers.add(getManager(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Managers", ex);
        }
        return managers;
    }

    @Override
    public void storeManager(Manager manager) throws DataException {
        try {
            if (manager.getKey() != null && manager.getKey() > 0) { // Update existing manager
                if (manager instanceof DataItemProxy && !((DataItemProxy) manager).isModified()) {
                    return;
                }

                uManager.setString(1, manager.getManagerName());
                uManager.setString(2, manager.getManagerEmail());
                long currentVersion = manager.getVersion();
                long nextVersion = currentVersion + 1;

                uManager.setLong(3, nextVersion);
                uManager.setInt(4, manager.getKey());
                uManager.setLong(5, currentVersion);

                if (uManager.executeUpdate() == 0) {
                    throw new OptimisticLockException(manager);
                } else {
                    manager.setVersion(nextVersion);
                }
            } else { // Insert new manager
                iManager.setString(1, manager.getManagerName());
                iManager.setString(2, manager.getManagerEmail());
                iManager.setLong(3, 1); // Initial version

                if (iManager.executeUpdate() == 1) {
                    try (ResultSet keys = iManager.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            manager.setKey(key);
                            dataLayer.getCache().add(Manager.class, manager);
                        }
                    }
                }
            }

            if (manager instanceof DataItemProxy) {
                ((DataItemProxy) manager).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store Manager", ex);
        }
    }

    @Override
    public void deleteManager(int managerID) throws DataException {
        try {
            dManager.setInt(1, managerID);
            dManager.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete Manager", ex);
        }
    }

    @Override
    public List<Manager> getManagersByDepartment(int departmentId) throws DataException {
        List<Manager> managers = new ArrayList<>();
        try {
            sManagersByDepartment.setInt(1, departmentId);
            try (ResultSet rs = sManagersByDepartment.executeQuery()) {
                while (rs.next()) {
                    managers.add(createManager(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load managers by department ID", ex);
        }
        return managers;
    }

    @Override
    public List<Manager> getManagersByDepartmentAndDateRange(int departmentId, java.sql.Date startDate, java.sql.Date endDate) throws DataException {
        List<Manager> managers = new ArrayList<>();
        try {
            sManagersByDepartmentAndDateRange.setInt(1, departmentId);
            sManagersByDepartmentAndDateRange.setDate(2, startDate);
            sManagersByDepartmentAndDateRange.setDate(3, endDate);

            try (ResultSet rs = sManagersByDepartmentAndDateRange.executeQuery()) {
                while (rs.next()) {
                    managers.add(createManager(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load managers by department and date range", ex);
        }
        return managers;
    }
}
