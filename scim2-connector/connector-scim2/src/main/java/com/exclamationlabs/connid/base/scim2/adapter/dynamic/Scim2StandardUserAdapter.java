package com.exclamationlabs.connid.base.scim2.adapter.dynamic;

import com.exclamationlabs.connid.base.connector.adapter.BaseAdapter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType;
import com.exclamationlabs.connid.base.scim2.configuration.Scim2Configuration;
import com.exclamationlabs.connid.base.scim2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.ObjectClass;

public class Scim2StandardUserAdapter extends BaseAdapter<Scim2User, Scim2Configuration> {

  @Override
  public ObjectClass getType() {
    return ObjectClass.ACCOUNT;
  }

  public Scim2Schema getStandardUserSchema() {
    return standardUserSchema;
  }

  public void setStandardUserSchema(Scim2Schema standardUserSchema) {
    this.standardUserSchema = standardUserSchema;
  }

  Scim2Schema standardUserSchema;

  @Override
  public Class<Scim2User> getIdentityModelClass() {
    return Scim2User.class;
  }

  Set<ConnectorAttribute> result;

  @Override
  public Set<ConnectorAttribute> getConnectorAttributes() {

    // String rawJson = getConfiguration().getSchemaRawJson();
    String rawJson = "{}";
    // System.out.println("RAW JSON ---> " + rawJson);
    ObjectMapper objectMapper = new ObjectMapper();
    List<Scim2Schema> schemaPojo = null;
/*
    try {
      schemaPojo = objectMapper.readValue(rawJson, new TypeReference<List<Scim2Schema>>() {});
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

 */
    result = new HashSet<>();

    schemaPojo.forEach(
        obj -> {
          /*
          if (obj.getId().equalsIgnoreCase("urn:ietf:params:scim:schemas:core:2.0:User")) {
            List<Scim2Schema.Attribute> userAttributes =
                obj.getAttributes();
            for (Scim2Schema.Attribute userAttribute :
                userAttributes) {
              if (userAttribute.getType().equalsIgnoreCase("complex")) {
                buildComplexElement(userAttribute, result);
              } else {
                if (userAttribute.getType().equalsIgnoreCase("reference")) {
                  result.add(
                      new ConnectorAttribute(
                          userAttribute.getName(),
                          ConnectorAttributeDataType.valueOf("STRING"),
                          buildFlags(userAttribute)));
                } else {
                  result.add(
                      new ConnectorAttribute(
                          userAttribute.getName(),
                          ConnectorAttributeDataType.valueOf(userAttribute.getType().toUpperCase()),
                          buildFlags(userAttribute)));
                }
              }
            }
          }*/
        });
    result.forEach(e -> System.out.println(e.getName()));
    return result;
  }

  private void buildComplexElement(
      com.exclamationlabs.connid.base.scim2.model.Attribute attribute, Set result) {
    for (com.exclamationlabs.connid.base.scim2.model.SubAttribute subAttribute :
        attribute.getSubAttributes()) {
      if (subAttribute.getType().equalsIgnoreCase("binary")
          || subAttribute.getType().equalsIgnoreCase("reference")) {
        result.add(
            new ConnectorAttribute(
                attribute.getName() + "_" + subAttribute.getName(),
                ConnectorAttributeDataType.valueOf("STRING"),
                buildFlags(subAttribute)));
      } else {
        result.add(createConnectorAttribute(subAttribute, attribute.getName() + "_"));
      }
    }
  }

  private ConnectorAttribute createConnectorAttribute(
      com.exclamationlabs.connid.base.scim2.model.SubAttribute subAttribute, String prefix) {
    return new ConnectorAttribute(
        prefix + subAttribute.getName(),
        ConnectorAttributeDataType.valueOf(subAttribute.getType().toUpperCase()),
        buildFlags(subAttribute));
  }

  @Override
  protected Set<Attribute> constructAttributes(Scim2User user) {

    Set<Attribute> attributes = new HashSet<>();
    //  attributes.add(AttributeBuilder.build(user.getName().getName()));

    return null;
    // GET CALL
  }

  @Override
  protected Scim2User constructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate) {

    Scim2User user = new Scim2User();
    for (ConnectorAttribute attr : result) {

      Field[] fields = ConnectorAttribute.class.getDeclaredFields();
      if (attr.getName().contains("_")) {
        String tokens[] = attr.getName().split("_");
        Set miniSet =
            result.stream()
                .filter(a -> a.getName().startsWith(tokens[0]))
                .collect(Collectors.toSet());
        System.out.println("Attributes " + miniSet);
        Object obj = createObject("Scim2" + capitalizeFirstLetter(tokens[0]), user);
        continue;
        // For complex type objects -  as we are working on all elements of complex type,
        // so need to further going, that is the reason we have 'continue' here
      }

      for (Field field : fields) {
        field.setAccessible(true); // Set field accessible since it's private

        // Get the value of the field from the attribute object
        Object value;
        try {
          value = field.get(attr);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }

        // Set the value of the field in the user object using reflection
        try {
          if (value != null) {

            Field userField = Scim2User.class.getDeclaredField(String.valueOf(value));
            userField.setAccessible(true); // Set field accessible since it's private

            if (value instanceof String && ((String) value).contains("active")) {
              // If value is a string containing "active", set it to true
              userField.set(user, true);
            } else if (value instanceof String && ((String) value).contains("inactive")) {
              // Otherwise, set it to false
              userField.set(user, false);
            } else {
              userField.set(user, String.valueOf(value));
            }
          }
        } catch (NoSuchFieldException | IllegalAccessException e) {
          // Field doesn't exist in User class, handle accordingly
          System.out.println("Field " + field.getName() + " does not exist in User class.");
        }
      }
    }
    return user;
  }

  public static String capitalizeFirstLetter(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return Character.toUpperCase(str.charAt(0)) + str.substring(1);
  }

  public static String lowerizeFirstLetter(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  public static Object createObject(String className, Scim2User user) {
    Object dd;
    try {
      Class<?> clazz = Class.forName("com.exclamationlabs.connid.base.scim2.model." + className);
      dd = clazz.getDeclaredConstructor().newInstance();
      List<String> methodNames =
          Arrays.asList(
              "Name",
              "Display",
              "Value",
              "Type",
              "Formatted",
              "FamilyName",
              "GivenName",
              "MiddleName",
              "HonorificPrefix",
              "HonorificSuffix",
              "Streetaddress",
              "Locality",
              "Region",
              "Postalcode",
              "Country");

      for (String methodName : methodNames) {
        invokeMethod(dd, "get" + methodName);

        try {
          Field userField = clazz.getDeclaredField(lowerizeFirstLetter(methodName.toLowerCase()));
          userField.setAccessible(true); // Set field accessible since it's private
          userField.set(dd, methodName.toLowerCase());
        } catch (NoSuchFieldException ex) {
          System.out.println("Eating Exception ");
        }
      }

    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | NoSuchMethodException
        | InvocationTargetException e) {
      e.printStackTrace(); // Handle the exception as needed
      return null;
    }
    return dd;
  }

  public static void invokeMethod(Object obj, String methodName) {
    try {
      Method method = obj.getClass().getMethod(methodName);
      // If the method is non-static, you need to provide the object instance on which to invoke the
      // method
      method.invoke(obj);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace(); // Handle the exception as needed
    }
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

  Set<AttributeInfo.Flags> buildFlags(
      com.exclamationlabs.connid.base.scim2.model.Attribute attribute) {
    return getFlags(
        attribute.getMultiValued(),
        attribute.getRequired(),
        attribute.getCaseExact(),
        attribute.getMutability(),
        attribute.getReturned(),
        attribute.getUniqueness());
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

  Set<AttributeInfo.Flags> buildFlags(
      com.exclamationlabs.connid.base.scim2.model.SubAttribute attribute) {
    return getFlags(
        attribute.getMultiValued(),
        attribute.getRequired(),
        attribute.getCaseExact(),
        attribute.getMutability(),
        attribute.getReturned(),
        attribute.getUniqueness());
  }
}
