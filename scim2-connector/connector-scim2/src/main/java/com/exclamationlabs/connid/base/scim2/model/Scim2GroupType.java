package com.exclamationlabs.connid.base.scim2.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Scim2GroupType
{
  private String value;
  @SerializedName("$ref")
  private String ref;
  private String display;
  private String type;
}
