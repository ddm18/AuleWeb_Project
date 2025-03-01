package it.univaq.f4i.iw.ex.AuleWeb.data.dao;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Type;
import it.univaq.f4i.iw.framework.data.DataException;
import java.util.List;

public interface TypeDAO {

    // Retrieve a single Type by its ID
    Type getType(int typeID) throws DataException;

    // Retrieve a list of all Types
    List<Type> getTypes() throws DataException;

    // Factory method to create an empty Type object
    Type createType() throws DataException;

    // CRUD operations for storing and deleting
    void storeType(Type type) throws DataException;

    void deleteType(int typeID) throws DataException;

	Type getTypeByName(String typeName) throws DataException;
}
