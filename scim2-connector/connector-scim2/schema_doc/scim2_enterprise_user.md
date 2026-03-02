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
			<td>employeeNumber</td>
			<td>string</td>
			<td>false</td>
			<td>Numeric or alphanumeric identifier assigned to a person, typically based on order of hire or association with an organization.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>costCenter</td>
			<td>string</td>
			<td>false</td>
			<td>Identifies the name of a cost center.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>organization</td>
			<td>string</td>
			<td>false</td>
			<td>Identifies the name of an organization.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>division</td>
			<td>string</td>
			<td>false</td>
			<td>Identifies the name of a division.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>department</td>
			<td>string</td>
			<td>false</td>
			<td>Identifies the name of a department.</td>
			<td>false</td>
			<td>false</td>
			<td>readWrite</td>
			<td>default</td>
			<td>none</td>
		</tr>
		<tr>
			<td>manager</td>
			<td>complex</td>
			<td>false</td>
			<td>The User's manager.  A complex type that optionally allows service providers to represent organizational hierarchy by referencing the 'id' attribute of another User.</td>
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