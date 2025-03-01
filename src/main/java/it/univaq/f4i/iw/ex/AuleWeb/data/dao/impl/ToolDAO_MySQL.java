package it.univaq.f4i.iw.ex.AuleWeb.data.dao.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.dao.ToolDAO;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.Tool;
import it.univaq.f4i.iw.ex.AuleWeb.data.model.impl.proxy.ToolProxy;
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

public class ToolDAO_MySQL extends DAO implements ToolDAO {

    private PreparedStatement sToolByID, sTools, sToolByName, uTool, iTool, dTool;

    public ToolDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            // Precompile SQL statements
            sToolByID = connection.prepareStatement("SELECT * FROM tool WHERE id = ?");
            sTools = connection.prepareStatement("SELECT id FROM tool");
            sToolByName = connection.prepareStatement("SELECT id FROM tool WHERE tool_name = ?");
            uTool = connection.prepareStatement(
                "UPDATE tool SET tool_name = ?, version = ? WHERE id = ? AND version = ?"
            );
            iTool = connection.prepareStatement(
                "INSERT INTO tool (tool_name, version) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );
            dTool = connection.prepareStatement("DELETE FROM tool WHERE id = ?");
        } catch (SQLException ex) {
            throw new DataException("Error initializing ToolDAO", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sToolByID != null) sToolByID.close();
            if (sTools != null) sTools.close();
            if (sToolByName != null) sToolByName.close();
            if (uTool != null) uTool.close();
            if (iTool != null) iTool.close();
            if (dTool != null) dTool.close();
        } catch (SQLException ex) {
            throw new DataException("Error closing prepared statements in ToolDAO", ex);
        }
        super.destroy();
    }

    @Override
    public Tool createTool() {
        return new ToolProxy(getDataLayer());
    }

    private ToolProxy createTool(ResultSet rs) throws DataException {
        try {
            ToolProxy tool = (ToolProxy) createTool();
            tool.setKey(rs.getInt("id"));
            tool.setToolName(rs.getString("tool_name"));
            tool.setVersion(rs.getLong("version"));
            return tool;
        } catch (SQLException ex) {
            throw new DataException("Unable to create Tool from ResultSet", ex);
        }
    }

    @Override
    public Tool getTool(int toolID) throws DataException {
        Tool tool = null;
        if (dataLayer.getCache().has(Tool.class, toolID)) {
            tool = dataLayer.getCache().get(Tool.class, toolID);
        } else {
            try {
                sToolByID.setInt(1, toolID);
                try (ResultSet rs = sToolByID.executeQuery()) {
                    if (rs.next()) {
                        tool = createTool(rs);
                        dataLayer.getCache().add(Tool.class, tool);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load Tool by ID", ex);
            }
        }
        return tool;
    }

    @Override
    public Tool getToolByName(String toolName) throws DataException {
        Tool tool = null;
        try {
            sToolByName.setString(1, toolName);
            try (ResultSet rs = sToolByName.executeQuery()) {
                if (rs.next()) {
                    tool = getTool(rs.getInt("id"));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Tool by name: " + toolName, ex);
        }
        return tool;
    }

    @Override
    public List<Tool> getTools() throws DataException {
        List<Tool> tools = new ArrayList<>();
        try (ResultSet rs = sTools.executeQuery()) {
            while (rs.next()) {
                tools.add(getTool(rs.getInt("id")));
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load Tools", ex);
        }
        return tools;
    }

    @Override
    public void storeTool(Tool tool) throws DataException {
        try {
            if (tool.getKey() != null && tool.getKey() > 0) { // Update existing tool
                if (tool instanceof DataItemProxy && !((DataItemProxy) tool).isModified()) {
                    return;
                }

                uTool.setString(1, tool.getToolName());
                long currentVersion = tool.getVersion();
                long nextVersion = currentVersion + 1;

                uTool.setLong(2, nextVersion);
                uTool.setInt(3, tool.getKey());
                uTool.setLong(4, currentVersion);

                if (uTool.executeUpdate() == 0) {
                    throw new OptimisticLockException(tool);
                } else {
                    tool.setVersion(nextVersion);
                }
            } else { // Insert new tool
                iTool.setString(1, tool.getToolName());
                iTool.setLong(2, 1); // Initial version

                if (iTool.executeUpdate() == 1) {
                    try (ResultSet keys = iTool.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            tool.setKey(key);
                            dataLayer.getCache().add(Tool.class, tool);
                        }
                    }
                }
            }

            if (tool instanceof DataItemProxy) {
                ((DataItemProxy) tool).setModified(false);
            }
        } catch (SQLException | OptimisticLockException ex) {
            throw new DataException("Unable to store Tool", ex);
        }
    }

    @Override
    public void deleteTool(int toolID) throws DataException {
        try {
            dTool.setInt(1, toolID);
            dTool.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete Tool", ex);
        }
    }
}
