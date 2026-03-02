package com.exclamationlabs.connid.base.scim2.adapter.slack;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static com.exclamationlabs.connid.base.scim2.attribute.slack.Scim2SlackUserAttribute.*;

import com.exclamationlabs.connid.base.connector.adapter.AdapterValueTypeConverter;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.scim2.adapter.Scim2UserAdapter;
import com.exclamationlabs.connid.base.scim2.model.*;
import com.exclamationlabs.connid.base.scim2.model.slack.Scim2SlackUser;
import java.util.*;

import com.exclamationlabs.connid.base.scim2.model.slack.SlackChannel;
import com.exclamationlabs.connid.base.scim2.model.slack.SlackGuestUser;
import com.exclamationlabs.connid.base.scim2.model.slack.SlackProfileUser;
import com.google.gson.Gson;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;

public class Scim2SlackUserAdapter extends Scim2UserAdapter {
  public static final String SLACK_GUEST_SCHEMA ="urn:ietf:params:scim:schemas:extension:slack:guest:2.0:User";
  public static final String SLACK_PROFILE_SCHEMA = "urn:ietf:params:scim:schemas:extension:slack:profile:2.0:User";

  public Set<ConnectorAttribute> getSlackConnectorAttributes(){
    Set<ConnectorAttribute> attributes = new HashSet<>();
    attributes.add(new ConnectorAttribute(guest_type.name(), STRING));
    attributes.add(new ConnectorAttribute(guest_expiration.name(), STRING));
    attributes.add(new ConnectorAttribute(guest_channels.name(), STRING, AttributeInfo.Flags.MULTIVALUED));
    attributes.add(new ConnectorAttribute(profile_startDate.name(), STRING));
    return attributes;
  }

  @Override
  public Set<ConnectorAttribute> getConnectorAttributes() {
    Set<ConnectorAttribute> result = null;
    // call Standard Attributes method
    result = getCoreConnectorAttributes();
    // call enterprise attributes method
    result.addAll(getEnterpriseConnectorAttributes());
    // call slack guest attributes method
    // call slack profile attributes method
    result.addAll(getSlackConnectorAttributes());
    return result;
  }

  @Override
  public Set<Attribute> constructAttributes(Scim2User user) {
    Set<Attribute> attributes = new HashSet<>();
    attributes.addAll(populateCoreAttributes(user));
    attributes.addAll(populateEnterpriseAttributes(user));
    attributes.addAll(populateGuestAttributes(user));
    attributes.addAll(populateProfileAttributes(user));
    return attributes;
  }

  @Override
  public Scim2SlackUser constructModel(
      Set<Attribute> attributes,
      Set<Attribute> addedMultiValueAttributes,
      Set<Attribute> removedMultiValueAttributes,
      boolean isCreate)  {
    Scim2SlackUser user = new Scim2SlackUser();
    populateCoreUser(user, attributes, addedMultiValueAttributes, removedMultiValueAttributes, isCreate);
    populateEnterpriseUser(user, attributes, addedMultiValueAttributes, removedMultiValueAttributes, isCreate);
    populateGuestUserInfo(user, attributes, addedMultiValueAttributes, removedMultiValueAttributes, isCreate);
    populateProfileInfo(user, attributes, addedMultiValueAttributes, removedMultiValueAttributes, isCreate);
    return user;
  }

  private Scim2SlackUser populateGuestUserInfo(Scim2SlackUser user,
                                     Set<Attribute> attributes,
                                     Set<Attribute> addedMultiValueAttributes,
                                     Set<Attribute> removedMultiValueAttributes,
                                     boolean isCreate)
  {
    SlackGuestUser guest = new SlackGuestUser();
    boolean exists = false;
    String guestType = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, guest_type);
    if ( guestType != null )
    {
      guest.setType(guestType);
      exists = true;
    }
    String expiration = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, guest_expiration);
    if ( expiration != null )
    {
      guest.setExpiration(expiration);
      exists = true;
    }
    Set<String> channelList = readAssignments(attributes, guest_channels);
    guest.setChannels(getSlackChannels(channelList));
    if ( guest.getChannels() != null && guest.getChannels().size() > 0 )
    {
      exists = true;
    }
    channelList = readAssignments(addedMultiValueAttributes, guest_channels);
    guest.setChannelsAdded(getSlackChannels(channelList));
    if ( guest.getChannelsAdded() != null && guest.getChannelsAdded().size() > 0 )
    {
      exists = true;
    }
    channelList = readAssignments(removedMultiValueAttributes, guest_channels);
    guest.setChannelsRemoved(getSlackChannels(channelList));
    if ( guest.getChannelsRemoved() != null && guest.getChannelsRemoved().size() > 0 )
    {
      exists = true;
    }
    if ( exists )
    {
      user.setGuestInfo(guest);
      if ( user.getSchemas() == null )
      {
        List<String> schemas = new ArrayList<>();
        schemas.add(SLACK_GUEST_SCHEMA);
        user.setSchemas(schemas);
      }
      else
      {
        user.getSchemas().add(SLACK_GUEST_SCHEMA);
      }
    }
    return user;
  }

  private Scim2SlackUser populateProfileInfo(Scim2SlackUser user,
                                               Set<Attribute> attributes,
                                               Set<Attribute> addedMultiValueAttributes,
                                               Set<Attribute> removedMultiValueAttributes,
                                               boolean isCreate) {
    String profileStart = AdapterValueTypeConverter.getSingleAttributeValue(String.class, attributes, profile_startDate);
    SlackProfileUser profile = new SlackProfileUser();
    if ( profileStart != null && profileStart.length() > 0 )
    {
      profile.setStartDate(profileStart);
      user.setProfileInfo(profile);
      if ( user.getSchemas() == null ) {
        List<String> schemas = new ArrayList<>();
        schemas.add(SLACK_PROFILE_SCHEMA);
        user.setSchemas(schemas);
      }
      else
      {
        user.getSchemas().add(SLACK_PROFILE_SCHEMA);
      }
    }

    return user;
  }
  /**
   * @param user Scim2Slack User inbound from the endpoint
   * @return Set of Guest Attributes
   */
  public Set<Attribute> populateGuestAttributes(Scim2User user) {
    Set<Attribute> attributes = new HashSet<>();
    if ( user != null && user instanceof Scim2SlackUser) {
      Scim2SlackUser sUser = (Scim2SlackUser) user;
      SlackGuestUser guest = sUser.getGuestInfo();
      if ( guest != null ) {
        attributes.add(AttributeBuilder.build(guest_type.name(), guest.getType()));
        attributes.add(AttributeBuilder.build(guest_expiration.name(), guest.getExpiration()));
        attributes.add(AttributeBuilder.build(guest_channels.name(), putSlackChannels(guest.getChannels())));
      }
    }
    return attributes;
  }

  /**
   * @param user Scim2Slack User inbound from the endpoint
   * @return Set of Guest Attributes
   */
  public Set<Attribute> populateProfileAttributes(Scim2User user) {
    Set<Attribute> attributes = new HashSet<>();
    if ( user != null && user instanceof Scim2SlackUser) {
      Scim2SlackUser sUser = (Scim2SlackUser) user;
      SlackProfileUser profile = sUser.getProfileInfo();
      if ( profile != null ) {
        attributes.add(AttributeBuilder.build(profile_startDate.name(), profile.getStartDate()));
      }
    }
    return attributes;
  }
  /**
   * Convert Collection of Slack Channels to Set of JSON Strings
   * @param channels Collection of Slack Channels to be converted
   * @return Set of JSON String
   */
  public Set<String> putSlackChannels(Collection<SlackChannel> channels) {
    Set<String> result = new HashSet<>();
    if ( channels == null || channels.isEmpty() ) {
      return null;
    }
    Gson gson = new Gson();
    channels.forEach( channel -> {
      String json = gson.toJson(channel);
      if ( json != null && !json.isEmpty() ) {
        result.add(json);
      }
    });
    return result;
  }
  /**
   * Convert Collection of Slack Channels to Set of JSON Strings
   * @param channels Collection of Slack Channels to be converted
   * @return Set of JSON String
   */
  public List<SlackChannel> getSlackChannels(Collection<String> channels) {
    List<SlackChannel> list = null;
    if ( channels != null && !channels.isEmpty() ) {
      try {
        list = new ArrayList<>();
        Gson gson = new Gson();
        for( String jsonItem : channels) {
          SlackChannel channel = gson.fromJson(jsonItem, SlackChannel.class);
          list.add(channel);
        }
      }
      catch (Exception e) {
        Logger.error(this, "Converting to List of Slack Channel ", e);
      }
      finally
      {
        // do not return an empty list
        if (list != null && list.isEmpty()) {
          list = null;
        }
      }
    }
    return list;
  }
}
