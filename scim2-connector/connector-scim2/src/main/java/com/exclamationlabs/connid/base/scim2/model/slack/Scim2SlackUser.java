package com.exclamationlabs.connid.base.scim2.model.slack;

import com.exclamationlabs.connid.base.scim2.model.*;
import com.google.gson.annotations.SerializedName;
import lombok.Data;


public class Scim2SlackUser extends Scim2User {

  @SerializedName("urn:ietf:params:scim:schemas:extension:slack:guest:2.0:User")
  private SlackGuestUser guestInfo;
  @SerializedName("urn:ietf:params:scim:schemas:extension:slack:profile:2.0:User")
  private SlackProfileUser profileInfo;

  public SlackGuestUser getGuestInfo()
  {
    return guestInfo;
  }

  public SlackProfileUser getProfileInfo()
  {
    return profileInfo;
  }

  public void setGuestInfo(SlackGuestUser guestInfo)
  {
    this.guestInfo = guestInfo;
  }

  public void setProfileInfo(SlackProfileUser profileInfo)
  {
    this.profileInfo = profileInfo;
  }
}
