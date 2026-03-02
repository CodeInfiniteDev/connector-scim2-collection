package com.exclamationlabs.connid.base.scim2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;



public class Scim2Schema {

  private String id;
  private String name;
  private String description;
  public List<Attribute> attributes;
  private Meta meta;

  public static class Attribute {
    @SerializedName("name")
    public String name;

    @SerializedName("type")
    public String type;

    @SerializedName("multiValued")
    public Boolean multiValued;

    @SerializedName("description")
    public String description;

    @SerializedName("required")
    public Boolean required;

    @SerializedName("caseExact")
    public Boolean caseExact;

    @SerializedName("mutability")
    public String mutability;

    @SerializedName("returned")
    public String returned;

    @SerializedName("uniqueness")
    public String uniqueness;

    @SerializedName("subAttributes")
    public List<Attribute> subAttributes;

  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public List<Attribute> getAttributes()
  {
    return attributes;
  }

  public void setAttributes(List<Attribute> attributes)
  {
    this.attributes = attributes;
  }

  public Meta getMeta()
  {
    return meta;
  }

  public void setMeta(Meta meta)
  {
    this.meta = meta;
  }
}
