package com.exclamationlabs.connid.base.scim2.attribute;
/**
 * field names in the scim2 enterprise user schema
 * Schema: urn:ietf:params:scim:schemas:extension:enterprise:2.0:User
 */
public enum Scim2EnterpriseUserAttribute
{
    employeeNumber,
    costCenter,
    organization,
    division,
    department,
    manager,
    manager_value,
    manager_ref,
    manager_displayName,
    // Slack Manager ID
    manager_managerId
}
