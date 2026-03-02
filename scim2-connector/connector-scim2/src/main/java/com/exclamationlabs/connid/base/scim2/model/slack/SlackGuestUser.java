package com.exclamationlabs.connid.base.scim2.model.slack;

import java.util.List;

public class SlackGuestUser {
  private List<SlackChannel> channels;
  private transient List<SlackChannel> channelsAdded;
  private transient List<SlackChannel> channelsRemoved;
  private String expiration;
  private String type;

  public List<SlackChannel> getChannels()
  {
    return channels;
  }

  public List<SlackChannel> getChannelsAdded()
  {
    return channelsAdded;
  }

  public List<SlackChannel> getChannelsRemoved()
  {
    return channelsRemoved;
  }

  public String getExpiration()
  {
    return expiration;
  }

  public String getType()
  {
    return type;
  }

  public void setChannels(List<SlackChannel> channels)
  {
    this.channels = channels;
  }

  public void setChannelsAdded(List<SlackChannel> channelsAdded)
  {
    this.channelsAdded = channelsAdded;
  }

  public void setChannelsRemoved(List<SlackChannel> channelsRemoved)
  {
    this.channelsRemoved = channelsRemoved;
  }

  public void setExpiration(String expiration)
  {
    this.expiration = expiration;
  }

  public void setType(String type)
  {
    this.type = type;
  }
}
