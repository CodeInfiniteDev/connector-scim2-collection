package com.exclamationlabs.connid.base.scim2.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Scim2Address
{
  private String country;
  private String formatted;
  private String locality;
  private String name;
  @SerializedName("postalCode")
  private String postalCode;
  private String primary;
  private String region;
  @SerializedName("streetAddress")
  private String streetAddress;
  private String type;
}
