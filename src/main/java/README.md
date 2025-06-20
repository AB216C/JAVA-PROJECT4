## Advanced Student Management System ##

The main goal of the project was to utilize advanced Java Concepts to implement CRUD functionality.

The first part involved setting up an H2 database to store student data, creating a Singleton class to best utilize the DB connection,
implementing CRUD operations for students using JDBC, implement methods to get data by id for students,
and make sure IDs for the students are generated by the program itself, not manually entered.

The next part involved adding functionality to export student data to a text file (students.txt),
and implementing functionality to read data from a file and populate the database.

The next part involved using the Streams API to process collections of students through filtering (displaying all students who are in the same selected grade),
counting (counting how many students are in the same grade), and sorting (returning a list where the students are sorted alphabetically).

The last part of the program involved writing a program that fetches and displays a list of students concurrently using two threads.
The first thread focused on printing the first half of the students, and the second thread printed the second half of the students. 
Both threads were expected to run simultaneously.



