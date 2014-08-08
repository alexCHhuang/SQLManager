SQLManager
=====

Academia Sinica project

[Function Description]

public void UpdateRecord(String str)
Update table_name SET column1=value1, column2=value2,...
WHERE some_column=some_value

[Update History]

=== 2014/8/6 ===

Using employees table in employees database as a template,
the employees table structure as follows.
+------------+---------------+------+-----+---------+-------+
| Field      | Type          | Null | Key | Default | Extra |
+------------+---------------+------+-----+---------+-------+
| emp_no     | int(11)       | NO   | PRI | NULL    |       |
| birth_date | date          | NO   |     | NULL    |       |
| first_name | varchar(14)   | NO   |     | NULL    |       |
| last_name  | varchar(16)   | NO   |     | NULL    |       |
| gender     | enum('M','F') | NO   |     | NULL    |       |
| hire_date  | date          | NO   |     | NULL    |       |
+------------+---------------+------+-----+---------+-------+

1. Complete Insert Record function
	[Hard Code] 
	Syntax: INSERT INTO User (emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?)
	* The first column must be integer, the remaining columns must match the corresponding type of employees table.
	* Support parsing the remaining column repeatedly
2. Complete Update Record function

=== 2014/7/25 ===
1. Complete Select query function
2. Complete Delete function
3. Set up GitHub in Ubuntu

