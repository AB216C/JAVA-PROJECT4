import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class JDBCTest {
    private static Connection conn;

    public static void main(String[] args) {
        String url = "jdbc:h2:mem:";

        try {
            conn = DriverManager.getConnection(url);
            createTable();

            // Create students
            createStudent(new Student("Jean", "j@gmail.com"));
            createStudent(new Student("Ben", "ben@gmail.com"));
            createStudent(new Student("Jacky", "jacky@gmail.com"));
            createStudent(new Student("Mary", "mary@gmail.com"));


            // Read all students
            System.out.println("All Students:");
            getAllStudents().forEach(s -> System.out.println(s.getId() + ":" + s.getName() + "-" + s.getEmail()));

            // Get student by ID
            System.out.println("\nStudent with ID 2:");
            Student student = getStudentById(2);
            if (student != null)
                System.out.println(student.getId() + ":" + student.getName() + "-" + student.getEmail());

            // Update student
            System.out.println("\nUpdating student with ID 2...");
            updateStudent(new Student(2, " Ben Patrick ", "benp234@gmail.com"));

            // Read updated student
            Student updated = getStudentById(2);
            System.out.println("Updated Student: " + updated.getId() + ":" + updated.getName() + "-" + updated.getEmail());

            // Delete student
            System.out.println("\nDeleting student with ID 3...");
            deleteStudent(3);

            // Final list
            System.out.println("\nStudents after deletion:");
            getAllStudents().forEach(s -> System.out.println(s.getId() + ":" + s.getName() + "-" + s.getEmail()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createTable() throws SQLException {
        Statement stmt = conn.createStatement();
        String createTableSQL =
                "CREATE TABLE users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "email VARCHAR(200)" +
                        ")";
        stmt.executeUpdate(createTableSQL);
    }

    private static void createStudent(Student student) throws SQLException {
        String insertSQL = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
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
                return new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
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
                students.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
        }
        return students;
    }

    private static void updateStudent(Student student) throws SQLException {
        String updateSQL = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setInt(3, student.getId());
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
