package it.univaq.f4i.iw.ex.AuleWeb.data.model.impl;

import it.univaq.f4i.iw.ex.AuleWeb.data.model.Tool;
import it.univaq.f4i.iw.framework.data.DataItemImpl;

public class ToolImpl extends DataItemImpl<Integer> implements Tool {

    private String tool_name;

    public ToolImpl() {
        super();
        tool_name = "";
    }

    @Override
    public String getToolName() {
        return tool_name;
    }

    @Override
    public void setToolName(String tool_name) {
        this.tool_name = tool_name;
    }
}
