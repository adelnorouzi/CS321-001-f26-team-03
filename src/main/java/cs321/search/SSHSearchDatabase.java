package cs321.search;

import cs321.common.ParseArgumentException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Driver class for querying top-frequency SSH keys from a SQLite database.
 */
public class SSHSearchDatabase {

    public static void main(String[] args) {
        try {
            SSHSearchDatabaseArguments parsedArgs = SSHSearchDatabaseArguments.parseArguments(args);
            String url = "jdbc:sqlite:" + parsedArgs.getDatabasePath();

            try (Connection conn = DriverManager.getConnection(url)) {
                queryTopFrequencies(conn, parsedArgs.getTreeType(), parsedArgs.getTopFrequency());
            }
        } catch (ParseArgumentException e) {
            System.err.println("Argument error: " + e.getMessage());
            printUsageAndExit();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Queries the specified table and prints the top N keys ordered by descending frequency.
     *
     * @param conn         active JDBC connection to the SQLite database
     * @param treeType     SSH category flag (e.g., "accepted-ip")
     * @param topFrequency maximum number of rows to print (10, 25, or 50)
     * @throws SQLException if the table does not exist or the query fails
     */
    private static void queryTopFrequencies(Connection conn, String treeType, int topFrequency) throws SQLException {
        String tableName = treeType.replace("-", "");
        String sql = "SELECT key, frequency FROM " + tableName + " ORDER BY frequency DESC, key ASC LIMIT ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, topFrequency);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(rs.getString("key") + " " + rs.getInt("frequency"));
                }
            }
        }
    }

    /**
     * Prints correct command-line usage and exits with a non-zero status code.
     */
    private static void printUsageAndExit() {
        System.err.println("Usage: java -jar build/libs/SSHSearchDatabase.jar "
                + "--type=<tree-type> --database=<sqlite-database-path> --top-frequency=<10/25/50>");
        System.exit(1);
    }
}