package com.exclamationlabs.connid.base.scim2.model.response.fault;

import java.util.List;


public class Scim2ErrorResponse
{
  private String detail;
  private List<String> schemas;
  private String scimType;
  private Integer status;

  public String getDetail() {
    return detail;
  }

  public List<String> getSchemas()
  {
    return schemas;
  }

  public String getScimType()
  {
    return scimType;
  }

  public Integer getStatus()
  {
    return status;
  }

  public void setDetail(String detail)
  {
    this.detail = detail;
  }

  public void setSchemas(List<String> schemas)
  {
    this.schemas = schemas;
  }

  public void setScimType(String scimType)
  {
    this.scimType = scimType;
  }

  public void setStatus(Integer status)
  {
    this.status = status;
  }
}
