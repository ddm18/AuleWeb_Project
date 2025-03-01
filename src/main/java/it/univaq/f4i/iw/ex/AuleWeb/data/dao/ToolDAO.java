package it.univaq.f4i.iw.ex.AuleWeb.data.dao;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Tool;
import it.univaq.f4i.iw.framework.data.DataException;
import java.util.List;

public interface ToolDAO {

    // Retrieve a single Tool by its ID
    Tool getTool(int toolID) throws DataException;

    // Retrieve a list of all Tools
    List<Tool> getTools() throws DataException;

    // Factory method to create an empty Tool object
    Tool createTool() throws DataException;

    // CRUD operations for storing and deleting
    void storeTool(Tool tool) throws DataException;

    void deleteTool(int toolID) throws DataException;

	Tool getToolByName(String toolName) throws DataException;
}
