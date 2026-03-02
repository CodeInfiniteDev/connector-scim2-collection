<table style="border: 1px black;">
	<thead style="border: 1px black;">
		<tr>
			<th>name</th>
			<th>type</th>
			<th>multiValued</th>
			<th>description</th>
			<th>required</th>
			<th>caseExact</th>
			<th>mutability</th>
			<th>returned</th>
			<th>uniqueness</th>
		</tr>
	</thead>
	<tbody style="vertical-align: top">
		<tr>
			<td>formatted</td>
			<td>string</td>
			<td>false</td>
			<td>The full name, including all middle names, titles, and suffixes as appropriate, formatted for display (e.g., 'Ms. Barbara J Jensen, III').</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>familyName</td>
			<td>string</td>
			<td>false</td>
			<td>The family name of the User, or last name in most Western languages (e.g., 'Jensen' given the full name 'Ms. Barbara J Jensen, III').</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>givenName</td>
			<td>string</td>
			<td>false</td>
			<td>The given name of the User, or first name in most Western languages (e.g., 'Barbara' given the full name 'Ms. Barbara J Jensen, III').</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>middleName</td>
			<td>string</td>
			<td>false</td>
			<td>The middle name(s) of the User (e.g., 'Jane' given the full name 'Ms. Barbara J Jensen, III').</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>honorificPrefix</td>
			<td>string</td>
			<td>false</td>
			<td>The honorific prefix(es) of the User, or title in most Western languages (e.g., 'Ms.' given the full name 'Ms. Barbara J Jensen, III').</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>honorificSuffix</td>
			<td>string</td>
			<td>false</td>
			<td>The honorific suffix(es) of the User, or suffix in most Western languages (e.g., 'III' given the full name 'Ms. Barbara J Jensen, III').</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td></td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	</tbody>
</table>