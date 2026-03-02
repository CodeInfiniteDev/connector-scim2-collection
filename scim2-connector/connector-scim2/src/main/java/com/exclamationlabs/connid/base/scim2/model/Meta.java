package com.exclamationlabs.connid.base.scim2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"resourceType", "location"})
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Meta {

  @JsonProperty("resourceType")
  String resourceType;

  @JsonProperty("location")
  String location;
}
