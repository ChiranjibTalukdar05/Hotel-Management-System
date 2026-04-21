import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/hoteldb";
    private static final String USER = "root";
    private static final String PASS = "root";  // put your MySQL password here

    private static Connection conn;

    // ✔ Returns a single connection that stays open throughout the application
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Connected to MySQL Database!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
