package com.exclamationlabs.connid.base.scim2.model;

import lombok.Data;

@Data
public class Scim2ComplexType {
  private String value;
  private String display;
  private String type;
  private boolean primary;
}
