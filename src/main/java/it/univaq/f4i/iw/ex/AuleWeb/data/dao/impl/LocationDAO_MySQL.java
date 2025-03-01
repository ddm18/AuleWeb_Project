package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.LocationDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Location;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.LocationProxy;
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

public class LocationDAO_MySQL extends DAO implements LocationDAO {

    private PreparedStatement sLocationByID, sLocations, sLocationByPlace, uLocation, iLocation, dLocation,sLocationByDetails;

    public LocationDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Precompile SQL statements
            sLocationByID = connection.prepareStatement("SELECT * FROM location WHERE ID = ?");
            sLocations = connection.prepareStatement("SELECT ID FROM location");
            sLocationByPlace = connection.prepareStatement("SELECT ID FROM location WHERE place = ?");
            uLocation = connection.prepareStatement(
                "UPDATE location SET place = ?, building = ?, floor = ?, version = ? WHERE ID = ? AND version = ?"
            );
            iLocation = connection.prepareStatement(
                "INSERT INTO location (place, building, floor, version) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            dLocation = connection.prepareStatement("DELETE FROM location WHERE ID = ?");
            sLocationByDetails = connection.prepareStatement("SELECT * FROM location WHERE place = ? AND building = ? AND floor = ?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing location data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sLocationByID != null) sLocationByID.close();
            if (sLocations != null) sLocations.close();
            if (sLocationByPlace != null) sLocationByPlace.close();
            if (uLocation != null) uLocation.close();
            if (iLocation != null) iLocation.close();
            if (dLocation != null) dLocation.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in LocationDAO", ex);
        }
        super.destroy();
    }

    @Override
    public Location createLocation() {
        return new LocationProxy(getDataLayer());
    }

    private LocationProxy createLocation(ResultSet rs) throws DataException {
        try {
            LocationProxy location = (LocationProxy) createLocation();
            location.setKey(rs.getInt("ID"));
            location.setPlace(rs.getString("place"));
            location.setBuilding(rs.getString("building"));
            location.setFloor(rs.getString("floor"));
            location.setVersion(rs.getLong("version"));
            return location;
        } catch (SQLException ex) {
            throw new DataException("Unable to create location object from ResultSet", ex);
        }
    }

    @Override
    public Location getLocation(int locationKey) throws DataException {
        Location location = null;
        if (dataLayer.getCache().has(Location.class, locationKey)) {
            location = dataLayer.getCache().get(Location.class, locationKey);
        } else {
            try {
                sLocationByID.setInt(1, locationKey);
                try (ResultSet rs = sLocationByID.executeQuery()) {
                    if (rs.next()) {
                        location = createLocation(rs);
                        dataLayer.getCache().add(Location.class, location);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load location by ID", ex);
            }
        }
        return location;
    }

    @Override
    public Location getLocationByPlace(String place) throws DataException {
        Location location = null;
        try {
            sLocationByPlace.setString(1, place);
            try (ResultSet rs = sLocationByPlace.executeQuery()) {
                if (rs.next()) {
                    location = getLocation(rs.getInt("ID"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load location by place: " + place, ex);
        }
        return location;
    }

    @Override
    public List<Location> getLocations() throws DataException {
        List<Location> result = new ArrayList<>();
        try (ResultSet rs = sLocations.executeQuery()) {
            while (rs.next()) {
                result.add(getLocation(rs.getInt("ID")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load locations", ex);
        }
        return result;
    }

    @Override
    public void storeLocation(Location location) throws DataException {
        try {
            if (location.getKey() != null && location.getKey() > 0) { // Update existing location
                if (location instanceof DataItemProxy && !((DataItemProxy) location).isModified()) {
                    return;
                }

                uLocation.setString(1, location.getPlace());
                uLocation.setString(2, location.getBuilding());
                uLocation.setString(3, location.getFloor());
                long currentVersion = location.getVersion();
                long nextVersion = currentVersion + 1;

                uLocation.setLong(4, nextVersion);
                uLocation.setInt(5, location.getKey());
                uLocation.setLong(6, currentVersion);

                if (uLocation.executeUpdate() == 0) {
                    throw new OptimisticLockException(location);
                } else {
                    location.setVersion(nextVersion);
                }
            } else { // Insert new location
                iLocation.setString(1, location.getPlace());
                iLocation.setString(2, location.getBuilding());
                iLocation.setString(3, location.getFloor());
                iLocation.setLong(4, 1); // Initial version

                if (iLocation.executeUpdate() == 1) {
                    try (ResultSet keys = iLocation.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            location.setKey(key);
                            dataLayer.getCache().add(Location.class, location);
                        }
                    }
                }
            }

            if (location instanceof DataItemProxy) {
                ((DataItemProxy) location).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store location", ex);
        }
    }

    @Override
    public void deleteLocation(int locationID) throws DataException {
        try {
            dLocation.setInt(1, locationID);
            dLocation.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete location", ex);
        }
    }

    @Override
    public Location getLocationByDetails(String place, String building, String floor) throws DataException {
        try {
            // Set parameters in the prepared statement
            sLocationByDetails.setString(1, place);
            sLocationByDetails.setString(2, building);
            sLocationByDetails.setString(3, floor);

            // Execute query and process result
            try (ResultSet rs = sLocationByDetails.executeQuery()) {
                if (rs.next()) {
                    return createLocation(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to retrieve location by details", ex);
        }
        return null; // Return null if no matching location is found
    }

}
