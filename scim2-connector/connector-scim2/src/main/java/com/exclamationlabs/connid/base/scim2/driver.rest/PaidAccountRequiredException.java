package com.exclamationlabs.connid.base.scim2.driver.rest;

import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class PaidAccountRequiredException extends ConnectorException {
  public PaidAccountRequiredException(String message) {
    super(message);
  }
}
