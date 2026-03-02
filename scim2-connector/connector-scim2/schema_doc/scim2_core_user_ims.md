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
			<td>Instant messaging address for the User.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
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
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>type</td>
			<td>string</td>
			<td>false</td>
			<td>A label indicating the attribute's function, e.g., 'aim', 'gtalk', 'xmpp'.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>primary</td>
			<td>boolean</td>
			<td>false</td>
			<td>A Boolean value indicating the 'primary' or preferred attribute value for this attribute, e.g., the preferred messenger or primary messenger.  The primary attribute value 'true' MUST appear no more than once.</td>
			<td>false</td>
			<td></td>
			<td>readWrite</td>
			<td>default</td>
			<td></td>
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