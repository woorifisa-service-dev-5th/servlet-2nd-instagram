package Dao;
import java.sql.Connection;
import java.sql.DriverManager;
public class DBUtil {
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "1234";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_SCHEMA = "instagramdb";
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL + DATABASE_SCHEMA, USER_NAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}