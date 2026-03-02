package com.exclamationlabs.connid.base.scim2.model.response;

import com.exclamationlabs.connid.base.scim2.model.slack.Scim2SlackUser;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;

public class ListSlackUsersResponse {

  @SerializedName("itemsPerPage")
  private Integer itemsPerPage;
  @SerializedName("Resources")
  private List<Scim2SlackUser> resources;
  private Set<String> schemas;
  @SerializedName("startIndex")
  private Integer startIndex;
  @SerializedName("totalResults")
  private Integer totalResults;

  public ListSlackUsersResponse()
  {
  }

  public Integer getItemsPerPage()
  {
    return itemsPerPage;
  }

  public List<Scim2SlackUser> getResources() {
    return resources;
  }

  public Set<String> getSchemas()
  {
    return schemas;
  }

  public Integer getStartIndex()
  {
    return startIndex;
  }

  public Integer getTotalResults()
  {
    return totalResults;
  }

  public void setItemsPerPage(Integer itemsPerPage)
  {
    this.itemsPerPage = itemsPerPage;
  }

  public void setResources(List<Scim2SlackUser> resources) {
    this.resources = resources;
  }

  public void setSchemas(Set<String> schemas)
  {
    this.schemas = schemas;
  }

  public void setStartIndex(Integer startIndex)
  {
    this.startIndex = startIndex;
  }

  public void setTotalResults(Integer totalResults)
  {
    this.totalResults = totalResults;
  }
}
