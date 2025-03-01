package it.univaq.f4i.iw.ex.AuleWeb.data.dao;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Location;
import it.univaq.f4i.iw.framework.data.DataException;
import java.util.List;

public interface LocationDAO {

    // Retrieve a single Location by its ID
    Location getLocation(int locationID) throws DataException;

    // Retrieve a list of all Locations
    List<Location> getLocations() throws DataException;

    // Factory method to create an empty Location object
    Location createLocation() throws DataException;

    // CRUD operations for storing and deleting
    void storeLocation(Location location) throws DataException;

    void deleteLocation(int locationID) throws DataException;

	Location getLocationByPlace(String place) throws DataException;

	Location getLocationByDetails(String place, String building, String floor) throws DataException;
}
