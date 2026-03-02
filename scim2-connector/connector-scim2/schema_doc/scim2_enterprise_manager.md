<table style="border: 1px black;">
	<thead style="text-align: left;">
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
			<td>value</td>
			<td>string</td>
			<td>false</td>
			<td>The id of the SCIM resource representing the User's manager.  REQUIRED.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>$ref</td>
			<td>reference</td>
			<td>false</td>
			<td>The URI of the SCIM resource representing the User's manager.  REQUIRED.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>displayName</td>
			<td>string</td>
			<td>false</td>
			<td>The displayName of the User's manager. OPTIONAL and READ-ONLY.</td>
			<td>false</td>
			<td>false</td>
			<td>readOnly</td>
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