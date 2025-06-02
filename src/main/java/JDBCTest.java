import java.sql.*;
public class JDBCTest {
    public static void main(String[] args) {
        String url = "jdbc:h2:mem:";

        try(Connection conn = DriverManager.getConnection(url)){
            Statement stmt = conn.createStatement();

            //Create student

            String createTableSQL = "CREATE TABLE users(id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(200))";
            stmt.executeUpdate(createTableSQL);

            Student student1= new Student(1,"Briana", "b@gmail.com");
            Student student2 = new Student(2,"Nema", "n@gmail.com");
            Student student3 = new Student(3, "Bini", "b@gmail.com");

            //OUR SQL statement that we need to modify into a prepared statement
            //It takes 2 parameters that we represent with "?"
            String insertDataSQL = "INSERT INTO users (id,name,email) VALUES(?,?,?)";

            PreparedStatement insertStmt = conn.prepareStatement(insertDataSQL);

            insertStmt.setInt(1,student1.getId());
            insertStmt.setString(2,student1.getName());
            insertStmt.setString(3,student1.getEmail());

            //Execute our statement for the first created user
            insertStmt.executeUpdate();

            insertStmt.setInt(1,student2.getId());
            insertStmt.setString(2,student2.getName());
            insertStmt.setString(3,student2.getEmail());

            //Execute our statement for the second created user
            insertStmt.executeUpdate();

            insertStmt.setInt(1,student3.getId());
            insertStmt.setString(2,student3.getName());
            insertStmt.setString(3,student3.getEmail());

            //Execute our statement for the third created user
            insertStmt.executeUpdate();

            String query = "SELECT * FROM users";
            ResultSet rs=stmt.executeQuery(query);

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                System.out.println(id + "_" + name+ "_" +email);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
