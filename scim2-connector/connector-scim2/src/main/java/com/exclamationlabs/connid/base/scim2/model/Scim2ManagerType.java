package com.exclamationlabs.connid.base.scim2.model;


import com.google.gson.annotations.SerializedName;

public class Scim2ManagerType
{
  private String displayName;
  // Support Slack ManagerId not populated by default
  private String managerId;
  @SerializedName("$ref")
  private String ref;
  private String value;

  public String getDisplayName()
  {
    return displayName;
  }

  public String getManagerId()
  {
    return managerId;
  }

  public String getRef()
  {
    return ref;
  }

  public String getValue()
  {
    return value;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  public void setManagerId(String managerId)
  {
    this.managerId = managerId;
  }

  public void setRef(String ref)
  {
    this.ref = ref;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
}
