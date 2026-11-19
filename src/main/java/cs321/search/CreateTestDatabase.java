package cs321.search;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Standalone helper program to generate a local SQLite database with fake SSH data
 * for testing SSHSearchDatabase in Checkpoint 1.
 */
public class CreateTestDatabase {

    private static final Object[][] FAKE_ACCEPTED_IP = {
        {"Accepted-111.222.107.90", 25},
        {"Accepted-119.137.63.195", 14},
        {"Accepted-119.137.62.123", 9},
        {"Accepted-113.116.236.34", 6},
        {"Accepted-123.255.103.142", 5},
        {"Accepted-123.255.103.215", 5},
        {"Accepted-112.96.173.55", 3},
        {"Accepted-112.96.33.40", 3},
        {"Accepted-137.189.204.253", 3},
        {"Accepted-113.118.187.34", 2},
        {"Accepted-113.99.127.215", 2},
        {"Accepted-137.189.205.44", 2},
        {"Accepted-137.189.241.19", 2},
        {"Accepted-119.137.60.156", 1},
        {"Accepted-137.189.204.138", 1}
    };

    public static void main(String[] args) {
        String dbPath = (args.length > 0) ? args[0] : "test.db";
        String url = "jdbc:sqlite:" + dbPath;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS acceptedip");
            stmt.execute("CREATE TABLE acceptedip (key TEXT PRIMARY KEY, frequency INTEGER)");

            String insertSql = "INSERT INTO acceptedip (key, frequency) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (Object[] row : FAKE_ACCEPTED_IP) {
                    pstmt.setString(1, (String) row[0]);
                    pstmt.setInt(2, (Integer) row[1]);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            System.out.println("Created " + dbPath + " with " + FAKE_ACCEPTED_IP.length + " fake entries.");
        } catch (SQLException e) {
            System.err.println("Failed to create test database: " + e.getMessage());
            System.exit(1);
        }
    }
}

