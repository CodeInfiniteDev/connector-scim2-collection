package com.exclamationlabs.connid.base.scim2.model;

public class Scim2EnterpriseUser {
  private String costCenter;
  private String department;
  private String division;
  private String employeeNumber;
  private Scim2ManagerType manager;
  private String organization;

  public String getCostCenter()
  {
    return costCenter;
  }

  public String getDepartment()
  {
    return department;
  }

  public String getDivision()
  {
    return division;
  }

  public String getEmployeeNumber()
  {
    return employeeNumber;
  }

  public Scim2ManagerType getManager()
  {
    return manager;
  }

  public String getOrganization()
  {
    return organization;
  }

  public void setCostCenter(String costCenter)
  {
    this.costCenter = costCenter;
  }

  public void setDepartment(String department)
  {
    this.department = department;
  }

  public void setDivision(String division)
  {
    this.division = division;
  }

  public void setEmployeeNumber(String employeeNumber)
  {
    this.employeeNumber = employeeNumber;
  }

  public void setManager(Scim2ManagerType manager)
  {
    this.manager = manager;
  }

  public void setOrganization(String organization)
  {
    this.organization = organization;
  }
}
