package com.exclamationlabs.connid.base.scim2.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scim2PatchOpSingle
{
    private static final String SCIM2_PATCH_OP = "urn:ietf:params:scim:api:messages:2.0:PatchOp";
    private Set<String> schemas;
    @SerializedName("Operations")
    private List<Scim2OperationSingle> operations;

    public Scim2PatchOpSingle()
    {
        schemas = new HashSet<>();
        schemas.add(SCIM2_PATCH_OP);
    }

    public Set<String> getSchemas()
    {
        return schemas;
    }

    public void setSchemas(Set<String> schemas)
    {
        this.schemas = schemas;
        if ( this.schemas == null ) {
            schemas = new HashSet<>();
            schemas.add(SCIM2_PATCH_OP);
        } else if ( !this.schemas.contains(SCIM2_PATCH_OP))
        {
            schemas.add(SCIM2_PATCH_OP);
        }
    }

    public List<Scim2OperationSingle> getOperations()
    {
        return operations;
    }

    public void setOperations(List<Scim2OperationSingle> operations)
    {
        this.operations = operations;
    }
}
