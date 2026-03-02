<table style="border: 1px black;">
	<thead style="border: 1px black;">
		<tr>
			<th>name</th>
			<th>type</th>
			<th>multiValued</th>
			<th>description</th>
			<th>mutability</th>
			<th>returned</th>
			<th>uniqueness</th>
			<th>required</th>
			<th>caseExact</th>
		</tr>
	</thead>
	<tbody style="vertical-align: top">
		<tr>
			<td>id</td>
			<td>string</td>
			<td>false</td>
			<td>Unique identifier for the SCIM resource as defined by the Service Provider. Each representation of the resource MUST include a non-empty id value. This identifier MUST be unique across the Service Provider’s entire set of resources. It MUST be a stable, non-reassignable identifier that does not change when the same resource is returned in subsequent requests. The value of the id attribute is always issued by the Service Provider and MUST never be specified by the Service Consumer. REQUIRED.</td>
			<td>readOnly</td>
			<td>always</td>
			<td>global</td>
			<td>true</td>
			<td>false</td>
		</tr>
		<tr>
			<td>userName</td>
			<td>string</td>
			<td>false</td>
			<td>Unique identifier for the User, typically used by the user to directly authenticate to the service provider. Often displayed to the user as their unique identifier within the system (as opposed to id or externalId, which are generally opaque and not user-friendly identifiers). Each User MUST include a non-empty userName value. This identifier MUST be unique across the Service Consumer's entire set of Users. REQUIRED.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>server</td>
			<td>true</td>
			<td>false</td>
		</tr>
		<tr>
			<td>nickName</td>
			<td>string</td>
			<td>false</td>
			<td>The casual way to address the user in real life, e.g. "Bob" or "Bobby" instead of "Robert". This attribute SHOULD NOT be used to represent a User's username (e.g. bjensen or mpepperidge).</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>name</td>
			<td>complex</td>
			<td>false</td>
			<td>The components of the user’s real name. Providers MAY return just the full name as a single string in the formatted sub-attribute, or they MAY return just the individual component attributes using the other sub-attributes, or they MAY return both. If both variants are returned, they SHOULD be describing the same name, with the formatted name indicating how the component attributes should be combined.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>emails</td>
			<td>complex</td>
			<td>true</td>
			<td>E-mail addresses for the user. The value SHOULD be canonicalized by the Service Provider, e.g. bjensen@example.com instead of bjensen@EXAMPLE.COM. Canonical Type values of work, home, and other.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>true</td>
			<td>false</td>
		</tr>
		<tr>
			<td>photos</td>
			<td>complex</td>
			<td>true</td>
			<td>URL of a photo of the User. The value SHOULD be a canonicalized URL, and MUST point to an image file (e.g. a GIF, JPEG, or PNG image file) rather than to a web page containing an image. Service Providers MAY return the same image at different sizes, though it is recognized that no standard for describing images of various sizes currently exists. Note that this attribute SHOULD NOT be used to send down arbitrary photos taken by this User, but specifically profile photos of the User suitable for display when describing the User. Instead of the standard Canonical Values for type, this attribute defines the following Canonical Values to represent popular photo sizes: photo, thumbnail.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>groups</td>
			<td>complex</td>
			<td>true</td>
			<td>A list of groups that the user belongs to, either through direct membership, or dynamically calculated. The values are meant to enable expression of common group or role based access control models, although no explicit authorization model is defined. It is intended that the semantics of group membership and any behavior or authorization granted as a result of membership are defined by the Service Provider. The Canonical types "direct" and "indirect" are defined to describe how the group membership was derived. Direct group membership indicates the User is directly associated with the group and SHOULD indicate that Consumers may modify membership through the Group Resource. Indirect membership indicates User membership is transitive or dynamic and implies that Consumers cannot modify indirect group membership through the Group resource but MAY modify direct group membership through the Group resource which MAY influence indirect memberships. If the SCIM Service Provider exposes a Group resource, the value MUST be the "id" attribute of the corresponding Group resources to which the user belongs. Since this attribute is read-only, group membership changes MUST be applied via the Group Resource. READ-ONLY.</td>
			<td>readOnly</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>active</td>
			<td>string</td>
			<td>false</td>
			<td>A Boolean value indicating the User's administrative status. The definitive meaning of this attribute is determined by the Service Provider though a value of true infers the User is, for example, able to login while a value of false implies the User's account has been suspended.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>addresses</td>
			<td>complex</td>
			<td>true</td>
			<td>A physical mailing address for this User. Canonical Type Values of work, home, and other. The value attribute is a complex type with the following sub-attributes. All Sub-Attributes are OPTIONAL.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>phoneNumbers</td>
			<td>complex</td>
			<td>true</td>
			<td>Phone numbers for the User. The value SHOULD be canonicalized by the Service Provider according to format in RFC3966 e.g. 'tel:+1-201-555-0123'. Canonical Type values of work, home, mobile, fax, pager and other.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>displayName</td>
			<td>string</td>
			<td>false</td>
			<td>The name of the User, suitable for display to end-users. Each User returned MAY include a non-empty displayName value. The name SHOULD be the full name of the User being described if known (e.g. Babs Jensen or Ms. Barbara J Jensen, III), but MAY be a username or handle, if that is all that is available (e.g. bjensen). The value provided SHOULD be the primary textual label by which this User is normally displayed by the Service Provider when presenting it to end-users.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>profileUrl</td>
			<td>string</td>
			<td>false</td>
			<td>A fully qualified URL to a page representing the User's online profile.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>userType</td>
			<td>string</td>
			<td>false</td>
			<td>Used to identify the organization to user relationship. Typical values used might be "Contractor", "Employee", "Intern", "Temp", "External", and "Unknown" but any value may be used.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>title</td>
			<td>string</td>
			<td>false</td>
			<td>The user’s title, such as “Vice President”.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>preferredLanguage</td>
			<td>string</td>
			<td>false</td>
			<td>Indicates the User's preferred written or spoken language. Generally used for selecting a localized User interface. Valid values are concatenation of the ISO 639-1 two letter language code, an underscore, and the ISO 3166-1 2 letter country code; e.g., 'en_US' specifies the language English and country US.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>locale</td>
			<td>string</td>
			<td>false</td>
			<td>Used to indicate the User's default location for purposes of localizing items such as currency, date time format, numerical representations, etc. A locale value is a concatenation of the ISO 639-1 two letter language code, an underscore, and the ISO 3166-1 2 letter country code; e.g., 'en_US' specifies the language English and country US.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>timezone</td>
			<td>string</td>
			<td>false</td>
			<td>The User's time zone in the "Olson" timezone database format; e.g.,'America/Los_Angeles'.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>password</td>
			<td>string</td>
			<td>false</td>
			<td>The User's clear text password. This attribute is intended to be used as a means to specify an initial password when creating a new User or to reset an existing User's password. No accepted standards exist to convey password policies, hence Consumers should expect Service Providers to reject password values. This value MUST never be returned by a Service Provider in any form.</td>
			<td>writeOnly</td>
			<td>never</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
		<tr>
			<td>roles</td>
			<td>complex</td>
			<td>true</td>
			<td>A list of roles for the User that collectively represent who the User is; e.g., "Student", "Faculty". No vocabulary or syntax is specified though it is expected that a role value is a String or label representing a collection of entitlements. This value has NO canonical types.</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
			<td>false</td>
			<td>false</td>
		</tr>
	</tbody>
</table>