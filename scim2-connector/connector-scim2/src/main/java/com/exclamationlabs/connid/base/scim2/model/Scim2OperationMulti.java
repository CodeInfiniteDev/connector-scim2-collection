package com.exclamationlabs.connid.base.scim2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Scim2OperationMulti
{
    @SerializedName("op")
    private String operation;
    private String path;
    private List<Map<String, String>> value;

    public String getOperation()
    {
        return operation;
    }

    public String getPath()
    {
        return path;
    }

    public List<Map<String, String>> getValue()
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

    public void setValue(List<Map<String, String>> value)
    {
        this.value = value;
    }
}
