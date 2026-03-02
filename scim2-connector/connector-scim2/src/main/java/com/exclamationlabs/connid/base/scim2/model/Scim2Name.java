package com.exclamationlabs.connid.base.scim2.model;




public class Scim2Name {

  public String formatted;
  public String familyName;
  public String givenName;
  public String middleName;
  public String honorificPrefix;


  public String honorificSuffix;

  public String getFamilyName()
  {
    return familyName;
  }

  public String getFormatted()
  {
    return formatted;
  }

  public String getGivenName()
  {
    return givenName;
  }

  public String getHonorificPrefix()
  {
    return honorificPrefix;
  }

  public String getHonorificSuffix()
  {
    return honorificSuffix;
  }

  public String getMiddleName()
  {
    return middleName;
  }

  public void setFamilyName(String familyName)
  {
    this.familyName = familyName;
  }

  public void setFormatted(String formatted)
  {
    this.formatted = formatted;
  }

  public void setGivenName(String givenName)
  {
    this.givenName = givenName;
  }

  public void setHonorificPrefix(String honorificPrefix)
  {
    this.honorificPrefix = honorificPrefix;
  }

  public void setHonorificSuffix(String honorificSuffix)
  {
    this.honorificSuffix = honorificSuffix;
  }

  public void setMiddleName(String middleName)
  {
    this.middleName = middleName;
  }
}
