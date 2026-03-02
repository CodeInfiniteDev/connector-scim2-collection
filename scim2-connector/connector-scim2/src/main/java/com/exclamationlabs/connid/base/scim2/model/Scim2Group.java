package com.exclamationlabs.connid.base.scim2.model;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import java.util.Map;
import java.util.Set;

public class Scim2Group implements IdentityModel
{
    private transient Set<Map<String, String>> addMembers;
    private transient Set<Map<String, String>> createMembers;
    private String displayName;
    private String externalId;
    private String id;
    private Set<Map<String, String>> members;
    private Scim2Meta meta;
    private transient Set<Map<String, String>> removeMembers;
    private Set<String> schemas;

    public Set<Map<String, String>> getAddMembers()
    {
        return addMembers;
    }

    public Set<Map<String, String>> getCreateMembers()
    {
        return createMembers;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String getIdentityIdValue()
    {
        return getId();
    }

    @Override
    public String getIdentityNameValue()
    {
        return getDisplayName();
    }

    public Set<Map<String, String>> getMembers()
    {
        return members;
    }

    public Scim2Meta getMeta()
    {
        return meta;
    }

    public Set<Map<String, String>> getRemoveMembers()
    {
        return removeMembers;
    }

    public Set<String> getSchemas()
    {
        return schemas;
    }

    public void setAddMembers(Set<Map<String, String>> addMembers)
    {
        this.addMembers = addMembers;
    }

    public void setCreateMembers(Set<Map<String, String>> createMembers)
    {
        this.createMembers = createMembers;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setMembers(Set<Map<String, String>> members)
    {
        this.members = members;
    }

    public void setMeta(Scim2Meta meta)
    {
        this.meta = meta;
    }

    public void setRemoveMembers(Set<Map<String, String>> removeMembers)
    {
        this.removeMembers = removeMembers;
    }

    public void setSchemas(Set<String> schemas)
    {
        this.schemas = schemas;
    }
}
