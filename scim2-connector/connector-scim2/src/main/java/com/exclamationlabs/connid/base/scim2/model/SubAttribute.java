package com.exclamationlabs.connid.base.scim2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "name",
  "type",
  "multiValued",
  "description",
  "required",
  "caseExact",
  "mutability",
  "returned",
  "uniqueness",
  "canonicalValues",
  "referenceTypes"
})
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubAttribute {

  @JsonProperty("name")
  String name;

  @JsonProperty("type")
  String type;

  @JsonProperty("multiValued")
  Boolean multiValued;

  @JsonProperty("description")
  String description;

  @JsonProperty("required")
  Boolean required;

  @JsonProperty("caseExact")
  Boolean caseExact;

  @JsonProperty("mutability")
  String mutability;

  @JsonProperty("returned")
  String returned;

  @JsonProperty("uniqueness")
  String uniqueness;

  @JsonProperty("canonicalValues")
  List<String> canonicalValues;

  @JsonProperty("referenceTypes")
  List<String> referenceTypes;
}
