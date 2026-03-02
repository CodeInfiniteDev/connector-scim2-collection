package com.exclamationlabs.connid.base.scim2.adapter.dynamic;

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.scim2.adapter.Scim2UserAdapter;
import com.exclamationlabs.connid.base.scim2.model.Scim2Schema;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.identityconnectors.framework.common.objects.AttributeInfo;

import java.io.IOException;
import java.util.*;

public class Scim2DynamicUserAdapter extends Scim2UserAdapter {
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    String config;
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

    public static final Set<AttributeInfo.Flags> buildFlags(
            com.exclamationlabs.connid.base.scim2.model.Attribute attribute) {

        Set<AttributeInfo.Flags> flagsSet = new HashSet<>();
        boolean multiValued = attribute.getMultiValued() != null ? attribute.getMultiValued() : false;
        boolean required = attribute.getRequired() != null ? attribute.getRequired() : false;
        boolean caseExact = attribute.getCaseExact() != null ? attribute.getCaseExact() : false;
        String mutability = attribute.getMutability() != null ? attribute.getMutability() : "";
        String returned = attribute.getReturned() != null ? attribute.getReturned() : "";
        String uniqueness = attribute.getUniqueness() != null ? attribute.getUniqueness() : "";
        if (multiValued) flagsSet.add(AttributeInfo.Flags.valueOf("MULTIVALUED"));

        if (required) flagsSet.add(AttributeInfo.Flags.valueOf("REQUIRED"));

        // if(caseExact)
        //   list.add(AttributeInfo.Subtypes.valueOf("MULTIVALUED"));

        if (mutability.equalsIgnoreCase("readOnly"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_UPDATEABLE"));

        if (mutability.equalsIgnoreCase("writeOnly"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_READABLE"));

        if (returned.equalsIgnoreCase("never"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_RETURNED_BY_DEFAULT"));

        if (uniqueness.equalsIgnoreCase("server"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_CREATABLE"));

        return flagsSet;
    }

    /**
     * Compose connid attributes flags from a SCIM2 sub attribute
     * @param attribute
     * @return Set of connid AttributeInfo Flags
     */
    public static final Set<AttributeInfo.Flags> buildFlags(
            com.exclamationlabs.connid.base.scim2.model.SubAttribute attribute) {

        Set<AttributeInfo.Flags> flagsSet = new HashSet<>();
        boolean multiValued = attribute.getMultiValued() != null ? attribute.getMultiValued() : false;
        boolean required = attribute.getRequired() != null ? attribute.getRequired() : false;
        boolean caseExact = attribute.getCaseExact() != null ? attribute.getCaseExact() : false;
        String mutability = attribute.getMutability() != null ? attribute.getMutability() : "";
        String returned = attribute.getReturned() != null ? attribute.getReturned() : "";
        String uniqueness = attribute.getUniqueness() != null ? attribute.getUniqueness() : "";
        if (multiValued) flagsSet.add(AttributeInfo.Flags.valueOf("MULTIVALUED"));

        if (required) flagsSet.add(AttributeInfo.Flags.valueOf("REQUIRED"));

        // if(caseExact)
        //   list.add(AttributeInfo.Subtypes.valueOf("MULTIVALUED"));

        if (mutability.equalsIgnoreCase("readOnly"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_UPDATEABLE"));

        if (mutability.equalsIgnoreCase("writeOnly"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_READABLE"));

        if (returned.equalsIgnoreCase("never"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_RETURNED_BY_DEFAULT"));

        if (uniqueness.equalsIgnoreCase("server"))
            flagsSet.add(AttributeInfo.Flags.valueOf("NOT_CREATABLE"));

        return flagsSet;
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
    Set<AttributeInfo.Flags> buildFlags(Scim2Schema.Attribute attribute) {
        return getFlags(
                attribute.multiValued,
                attribute.required,
                attribute.caseExact,
                attribute.mutability,
                attribute.returned,
                attribute.uniqueness);
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
    public Set<ConnectorAttribute> getConnectorAttributes() {
        String rawJson = getConfig();
        System.out.println("RAW JSON ---> " + rawJson);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Scim2Schema> schemaPojo = null;
        Map<String, Object> userMap = new HashMap<>();
        Set<ConnectorAttribute> attributeInfos = new HashSet<>();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            schemaPojo = objectMapper.readValue(rawJson, new TypeReference<List<Scim2Schema>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<ConnectorAttribute> result = new HashSet<>();
        schemaPojo.forEach(
                obj -> {
                    if (obj.getId().equalsIgnoreCase("urn:ietf:params:scim:schemas:core:2.0:User")) {
                        // scim2SlackUserAdapter.setStandardUserSchema(obj);
                        List<Scim2Schema.Attribute> userAttributes = obj.getAttributes();
                        addAttributesToInfoSet(attributeInfos, userAttributes, "");
                    }
                });
        attributeInfos.removeIf(Objects::isNull);
        return attributeInfos;
    }
}
