package com.exclamationlabs.connid.base.scim2.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Scim2OperationSingle
{
    @SerializedName("op")
    private String operation;
    private String path;
    private Map<String, String> value;

    public String getOperation()
    {
        return operation;
    }

    public String getPath()
    {
        return path;
    }

    public Map<String, String> getValue()
    {
        return value;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setValue(Map<String, String> value)
    {
        this.value = value;
    }
}
