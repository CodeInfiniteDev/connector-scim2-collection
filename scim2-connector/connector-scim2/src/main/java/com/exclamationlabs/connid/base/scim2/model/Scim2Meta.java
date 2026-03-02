package com.exclamationlabs.connid.base.scim2.model;

import java.util.Date;

/**
 * SCIM2 Meta Type is a common attribute included with most resources such as Users, Groups, Schemas
 */
public class Scim2Meta
{
    private Date created;
    private Date lastModified;
    private String location;
    private String resourceType;
    private String version;

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getResourceType()
    {
        return resourceType;
    }

    public void setResourceType(String resourceType)
    {
        this.resourceType = resourceType;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
}
