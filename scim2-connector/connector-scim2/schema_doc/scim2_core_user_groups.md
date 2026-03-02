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
			<td>value</td>
			<td>string</td>
			<td>false</td>
			<td>The identifier of the User's group.</td>
			<td>false</td>
			<td>false</td>
			<td>readOnly</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>$ref</td>
			<td>reference</td>
			<td>false</td>
			<td>The URI of the corresponding 'Group' resource to which the user belongs.</td>
			<td>false</td>
			<td>false</td>
			<td>readOnly</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>display</td>
			<td>string</td>
			<td>false</td>
			<td>A human-readable name, primarily used for display purposes.  READ-ONLY.</td>
			<td>false</td>
			<td>false</td>
			<td>readOnly</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>type</td>
			<td>string</td>
			<td>false</td>
			<td>A label indicating the attribute's function, e.g., 'direct' or 'indirect'.</td>
			<td>false</td>
			<td>false</td>
			<td>readOnly</td>
			<td>default</td>
			<td>none</td>
		</tr>
	</tbody>
</table>