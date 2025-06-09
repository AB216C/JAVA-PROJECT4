import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.io.*;

public class JDBCTest {
    private static Connection conn;

    private static void exportStudentsToFile(String filename) throws SQLException, IOException {
        List<Student> students = getAllStudents();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Student s : students) {
                writer.write(s.getId() + "," + s.getName() + "," + s.getEmail()+","+s.getGrade());
                writer.newLine();
            }
        }
        System.out.println("Exported students to " + filename);
    }

    private static void importStudentsFromFile(String filename) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String grade = parts[3].trim();
                    createStudent(new Student(name, email,grade));
                }
            }
        }
        System.out.println("Imported students from " + filename);
    }

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:h2:mem:";

        try {
            conn = DriverManager.getConnection(url);
            createTable();

            // Create students
            createStudent(new Student("Jean", "j@gmail.com","A"));
            createStudent(new Student("Ben", "ben@gmail.com","B"));
            createStudent(new Student("Jacky", "jacky@gmail.com", "A"));
            createStudent(new Student("Mary", "mary@gmail.com", "C"));
            createStudent(new Student("Peter", "peter@gmail.com", "B"));
            createStudent(new Student("Anna", "anna@gmail.com", "C"));

            List<Student> allStudents = getAllStudents();

            // Filter: Show students in grade "A"
            System.out.println("\nStudents in Grade A:");
            allStudents.stream()
                    .filter(s -> "A".equalsIgnoreCase(s.getGrade()))
                    .forEach(s -> System.out.println(s.getName() + " - " + s.getGrade()));

            // Count: How many students in grade "A"
            long countInGradeA = allStudents.stream()
                    .filter(s -> "A".equalsIgnoreCase(s.getGrade()))
                    .count();
            System.out.println("\nNumber of students in Grade A: " + countInGradeA);

            // Sort: Alphabetically by name
            System.out.println("\nStudents sorted by name:");
            allStudents.stream()
                    .sorted((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()))
                    .forEach(s -> System.out.println(s.getName()));
            // Read all students
            System.out.println("All Students:");
            getAllStudents().forEach(s -> System.out.println(s.getId() + ":" + s.getName() +","+ s.getGrade()+"-" + s.getEmail()));

            // Get student by ID
            System.out.println("\nStudent with ID 2:");
            Student student = getStudentById(2);
            if (student != null)
                System.out.println(student.getId() + ":" + student.getName() + ","+ student.getGrade()+ "," + student.getEmail());

            // Update student
            System.out.println("\nUpdating student with ID 2...");
            updateStudent(new Student(2, " Ben Patrick ", "benp234@gmail.com", "B"));

            // Read updated student
            Student updated = getStudentById(2);
            System.out.println("Updated Student: " + updated.getId() + ":" + updated.getName() +","+ updated.getGrade()+ "," + updated.getEmail());

            // Delete student
            System.out.println("\nDeleting student with ID 3...");
            deleteStudent(3);

            // Final list
            System.out.println("\nStudents after deletion:");
            getAllStudents().forEach(s -> System.out.println(s.getId() + ":" + s.getName() +"," + s.getGrade()+ "," + s.getEmail()));

            // ✅ Fix: Move export/import code inside main
            exportStudentsToFile("students.txt");

            System.out.println("\nClearing table and re-importing students...");
            createTable(); // recreates empty table
            importStudentsFromFile("students.txt");

            System.out.println("\nStudents after import:");
            getAllStudents().forEach(s -> System.out.println(s.getId() + ":" + s.getName() +"," + s.getGrade()+"," + s.getEmail()));

            //Split into two halves

            int mid = allStudents.size()/2;
            List<Student>firstHalf = allStudents.subList(0,mid);
            List<Student>secondHalf = allStudents.subList(mid,allStudents.size());

            //Printing first half
            Thread t1 = new Thread (()->{
                System.out.println("\n------ Thread 1 : First half--------");
                for(Student s:firstHalf){
                    System.out.println(Thread.currentThread().getName() + ": " + s.getId() + " - " + s.getName() + ", " + s.getGrade());
                    try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                }

            });

            //Printing Second Half

            Thread t2 = new Thread (()->{
                System.out.println("\n------ Thread 2 : Second half--------");
                for(Student s:secondHalf){
                    System.out.println(Thread.currentThread().getName() + ": " + s.getId() + " - " + s.getName() + ", " + s.getGrade());
                    try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
                }

            });

            //Start threads
            t1.start();
            t2.start();

            //Waiting for both threads to finish

            try {
                t1.join();
                t2.join();
                System.out.println("\nBoth threads have finished processing.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createTable() throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("DROP TABLE IF EXISTS users");

        String createTableSQL =
                "CREATE TABLE users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "email VARCHAR(200)," +
                        "Grade Varchar(10)"+
                        ")";
        stmt.executeUpdate(createTableSQL);
    }

    private static void createStudent(Student student) throws SQLException {
        String insertSQL = "INSERT INTO users (name, email,grade) VALUES (?, ?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3,student.getGrade());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                student.setId(generatedKeys.getInt(1));
            }
        }
    }

    private static Student getStudentById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("grade"));
            }
        }
        return null;
    }

    private static List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("grade")));
            }
        }
        return students;
    }

    private static void updateStudent(Student student) throws SQLException {
        String updateSQL = "UPDATE users SET name = ?, email = ?, grade = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getGrade());
            stmt.setInt(4, student.getId());
            stmt.executeUpdate();
        }
    }

    private static void deleteStudent(int id) throws SQLException {
        String deleteSQL = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}


