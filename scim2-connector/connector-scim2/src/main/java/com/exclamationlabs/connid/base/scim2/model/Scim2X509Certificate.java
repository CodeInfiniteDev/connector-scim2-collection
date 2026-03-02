package com.exclamationlabs.connid.base.scim2.model;

import lombok.Data;

/**
 * Scim2x509Certificate is exactly like a Scim2ComplexType except the
 * Value has special characteristics.
 * The value contains exactly one DER-encoded X.509 certificate (see Section 4 of [RFC5280]),
 * which MUST be base64 encoded (See Section 4 of [RFC4648])
 */
@Data
public class Scim2X509Certificate {
  private String value;
  private String display;
  private String type;
  private boolean primary;
}
