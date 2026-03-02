package com.exclamationlabs.connid.base.scim2.adapter.dynamic;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.Scim2Group;
import com.exclamationlabs.connid.base.scim2.model.Scim2Schema;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.ObjectClass;

/**
 * Dynamic Groups Adapter
 */
public class Scim2DynamicGroupsAdapter extends BaseAdapter<Scim2Group, Scim2Configuration> {

  @Override
  public ObjectClass getType() {
    return ObjectClass.GROUP;
  }

  @Override
  public Class<Scim2Group> getIdentityModelClass() {
    return Scim2Group.class;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  String config;

  @Override
  public Set<ConnectorAttribute> getConnectorAttributes() {
    String rawJson = getConfig();

    ObjectMapper objectMapper = new ObjectMapper();
    List<Scim2Schema> schemaPojo = null;
    Map<String, Object> userMap = new HashMap<>();
    Set<ConnectorAttribute> attributeInfos = new HashSet<>();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
/*
    try {
      schemaPojo = objectMapper.readValue(rawJson, new TypeReference<List<Scim2Schema>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

 */

    Set<ConnectorAttribute> result = new HashSet<>();
    schemaPojo.forEach(
        obj -> {
          if (obj.getId().equalsIgnoreCase("urn:ietf:params:scim:schemas:core:2.0:Group")) {
            List<Scim2Schema.Attribute> userAttributes = obj.getAttributes();
            addAttributesToInfoSet(attributeInfos, userAttributes, "");
          }
        });
    attributeInfos.removeIf(Objects::isNull);
    return attributeInfos;
  }

  private void addAttributesToInfoSet(
      Set<ConnectorAttribute> attributeInfos,
      List<Scim2Schema.Attribute> schemaAttributes,
      String parentPath) {
    for (Scim2Schema.Attribute schemaAttr : schemaAttributes) {
      String fullPath = parentPath.isEmpty() ? schemaAttr.name : parentPath + "." + schemaAttr.name;
      // AttributeInfoBuilder builder = new AttributeInfoBuilder(fullPath);

      ConnectorAttribute builder1 = null;

      // builder.setMultiValued(schemaAttr.multiValued);
      // builder.setRequired(schemaAttr.required);

      if (schemaAttr.type.equalsIgnoreCase("string")
          || schemaAttr.type.equalsIgnoreCase("complex")) {
        // builder.setType(String.class);
        builder1 =
            new ConnectorAttribute(
                fullPath, ConnectorAttributeDataType.valueOf("STRING"), buildFlags(schemaAttr));
      } else if (schemaAttr.type.equalsIgnoreCase("boolean")) {
        // builder.setType(Boolean.class);
        builder1 =
            new ConnectorAttribute(
                fullPath, ConnectorAttributeDataType.valueOf("BOOLEAN"), buildFlags(schemaAttr));
      } else if (schemaAttr.type.equalsIgnoreCase("decimal")) {
        // builder.setType(Double.class);
        builder1 =
            new ConnectorAttribute(
                fullPath,
                ConnectorAttributeDataType.valueOf("BIG_DECIMAL"),
                buildFlags(schemaAttr));
      } else if (schemaAttr.type.equalsIgnoreCase("integer")) {
        // builder.setType(Integer.class);
        builder1 =
            new ConnectorAttribute(
                fullPath, ConnectorAttributeDataType.valueOf("INTEGER"), buildFlags(schemaAttr));
      } else if (schemaAttr.type.equalsIgnoreCase("datetime")) {
        // builder.setType(Long.class); // Typically UNIX timestamp
        builder1 =
            new ConnectorAttribute(
                fullPath,
                ConnectorAttributeDataType.valueOf("ZONED_DATE_TIME"),
                buildFlags(schemaAttr));
      }

      if (schemaAttr.subAttributes != null && !schemaAttr.subAttributes.isEmpty()) {
        addAttributesToInfoSet(attributeInfos, schemaAttr.subAttributes, fullPath);
      }

      // attributeInfos.add(builder.build());
      attributeInfos.add(builder1);
    }
  }

  Set<AttributeInfo.Flags> buildFlags(Scim2Schema.Attribute attribute) {
    return getFlags(
        attribute.multiValued,
        attribute.required,
        attribute.caseExact,
        attribute.mutability,
        attribute.returned,
        attribute.uniqueness);
  }

  private Set<AttributeInfo.Flags> getFlags(
      Boolean multiValued,
      Boolean required,
      Boolean caseExact,
      String mutability,
      String returned,
      String uniqueness) {
    Set<AttributeInfo.Flags> flagsSet = new HashSet<>();
    processAttributeFlags(
        flagsSet,
        multiValued != null ? multiValued : false,
        required != null ? required : false,
        caseExact != null ? caseExact : false,
        mutability != null ? mutability : "",
        returned != null ? returned : "",
        uniqueness != null ? uniqueness : "");
    return flagsSet;
  }

  private void processAttributeFlags(
      Set<AttributeInfo.Flags> flagsSet,
      boolean multiValued,
      boolean required,
      boolean caseExact,
      String mutability,
      String returned,
      String uniqueness) {
    if (multiValued) {
      flagsSet.add(AttributeInfo.Flags.MULTIVALUED);
    }
    if (required) {
      flagsSet.add(AttributeInfo.Flags.REQUIRED);
    }
    // if (caseExact) {
    //     list.add(AttributeInfo.Subtypes.MULTIVALUED);
    // }
    if ("readOnly".equalsIgnoreCase(mutability)) {
      flagsSet.add(AttributeInfo.Flags.NOT_UPDATEABLE);
    }
    if ("writeOnly".equalsIgnoreCase(mutability)) {
      flagsSet.add(AttributeInfo.Flags.NOT_READABLE);
    }
    if ("never".equalsIgnoreCase(returned)) {
      flagsSet.add(AttributeInfo.Flags.NOT_RETURNED_BY_DEFAULT);
    }
    if ("server".equalsIgnoreCase(uniqueness)) {
      flagsSet.add(AttributeInfo.Flags.NOT_CREATABLE);
    }
  }

  @Override
  protected Set<Attribute> constructAttributes(Scim2Group group) {
    Set<Attribute> attributes = new HashSet<>();
    return attributes;
  }

  @Override
  protected Scim2Group constructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate) {

    Scim2Group group = new Scim2Group();
    group.setId(AdapterValueTypeConverter.getIdentityIdAttributeValue(attributes));
    return group;
  }
}
