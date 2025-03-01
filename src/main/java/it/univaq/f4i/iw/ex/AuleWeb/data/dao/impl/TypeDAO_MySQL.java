package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.TypeDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Type;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.TypeProxy;
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

public class TypeDAO_MySQL extends DAO implements TypeDAO {

    private PreparedStatement sTypeByID, sTypes, sTypeByName, uType, iType, dType;

    public TypeDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            // Initialize prepared statements
            sTypeByID = connection.prepareStatement("SELECT * FROM type WHERE id = ?");
            sTypes = connection.prepareStatement("SELECT id FROM type");
            sTypeByName = connection.prepareStatement("SELECT id FROM type WHERE event_type_name = ?");
            uType = connection.prepareStatement(
                "UPDATE type SET event_type_name = ?, version = ? WHERE id = ? AND version = ?"
            );
            iType = connection.prepareStatement(
                "INSERT INTO type (event_type_name, version) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            dType = connection.prepareStatement("DELETE FROM type WHERE id = ?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing TypeDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sTypeByID != null) sTypeByID.close();
            if (sTypes != null) sTypes.close();
            if (sTypeByName != null) sTypeByName.close();
            if (uType != null) uType.close();
            if (iType != null) iType.close();
            if (dType != null) dType.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in TypeDAO", ex);
        }
        super.destroy();
    }

    @Override
    public Type createType() {
        return new TypeProxy(getDataLayer());
    }

    private TypeProxy createType(ResultSet rs) throws DataException {
        try {
            TypeProxy type = (TypeProxy) createType();
            type.setKey(rs.getInt("id"));
            type.setEventTypeName(rs.getString("event_type_name"));
            type.setVersion(rs.getLong("version"));
            return type;
        } catch (SQLException ex) {
            throw new DataException("Unable to create Type from ResultSet", ex);
        }
    }

    @Override
    public Type getType(int typeID) throws DataException {
        Type type = null;
        if (dataLayer.getCache().has(Type.class, typeID)) {
            type = dataLayer.getCache().get(Type.class, typeID);
        } else {
            try {
                sTypeByID.setInt(1, typeID);
                try (ResultSet rs = sTypeByID.executeQuery()) {
                    if (rs.next()) {
                        type = createType(rs);
                        dataLayer.getCache().add(Type.class, type);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load Type by ID", ex);
            }
        }
        return type;
    }

    @Override
    public Type getTypeByName(String typeName) throws DataException {
        Type type = null;
        try {
            sTypeByName.setString(1, typeName);
            try (ResultSet rs = sTypeByName.executeQuery()) {
                if (rs.next()) {
                    type = getType(rs.getInt("id"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Type by name: " + typeName, ex);
        }
        return type;
    }

    @Override
    public List<Type> getTypes() throws DataException {
        List<Type> types = new ArrayList<>();
        try (ResultSet rs = sTypes.executeQuery()) {
            while (rs.next()) {
                types.add(getType(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Types", ex);
        }
        return types;
    }

    @Override
    public void storeType(Type type) throws DataException {
        try {
            if (type.getKey() != null && type.getKey() > 0) { // Update existing type
                if (type instanceof DataItemProxy && !((DataItemProxy) type).isModified()) {
                    return;
                }

                uType.setString(1, type.getEventTypeName());
                long currentVersion = type.getVersion();
                long nextVersion = currentVersion + 1;

                uType.setLong(2, nextVersion);
                uType.setInt(3, type.getKey());
                uType.setLong(4, currentVersion);

                if (uType.executeUpdate() == 0) {
                    throw new OptimisticLockException(type);
                } else {
                    type.setVersion(nextVersion);
                }
            } else { // Insert new type
                iType.setString(1, type.getEventTypeName());
                iType.setLong(2, 1); // Initial version

                if (iType.executeUpdate() == 1) {
                    try (ResultSet keys = iType.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            type.setKey(key);
                            dataLayer.getCache().add(Type.class, type);
                        }
                    }
                }
            }

            if (type instanceof DataItemProxy) {
                ((DataItemProxy) type).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store Type", ex);
        }
    }

    @Override
    public void deleteType(int typeID) throws DataException {
        try {
            dType.setInt(1, typeID);
            dType.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete Type", ex);
        }
    }
}
