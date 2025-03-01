package it.univaq.f4i.iw.ex.AuleWeb.data.dao;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Manager;
import it.univaq.f4i.iw.framework.data.DataException;

import java.sql.Date;
import java.util.List;

public interface ManagerDAO {

    // Retrieve a single Manager by its ID
    Manager getManager(int managerID) throws DataException;

    // Retrieve a list of all Managers
    List<Manager> getManagers() throws DataException;

    // Factory method to create an empty Manager object
    Manager createManager() throws DataException;

    // CRUD operations for storing and deleting
    void storeManager(Manager manager) throws DataException;

    void deleteManager(int managerID) throws DataException;

	Manager getManagerByName(String managerName) throws DataException;

	List<Manager> getManagersByDepartmentAndDateRange(int departmentId, Date startDate, Date endDate)
			throws DataException;

	List<Manager> getManagersByDepartment(int departmentId) throws DataException;
}
