package cs321.search;

import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Parses and validates command-line arguments for SSHSearchDatabase.
 */

public class SSHSearchDatabaseArguments {

    private static final Set<String> VALID_TYPES = new HashSet<String>(Arrays.asList(
            "accepted-ip", "accepted-time",
            "invalid-ip", "invalid-time",
            "failed-ip", "failed-time",
            "reverseaddress-ip", "reverseaddress-time",
            "user-ip"
    ));

    private final String treeType;
    private final String databasePath;
    private final int topFrequency;

    /**
     * Constructs a validated container of command-line arguments.
     *
     * @param treeType     the SSH key category to query
     * @param databasePath the path to the SQLite database file
     * @param topFrequency the number of top results to return (10, 25, or 50)
     */
    public SSHSearchDatabaseArguments(String treeType, String databasePath, int topFrequency) {
        this.treeType = treeType;
        this.databasePath = databasePath;
        this.topFrequency = topFrequency;
    }

    public String getTreeType() {
        return treeType;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public int getTopFrequency() {
        return topFrequency;
    }

    /**
     * Parses raw command-line arguments into an SSHSearchDatabaseArguments object.
     *
     * @param args command-line flags passed to main
     * @return parsed and validated arguments
     * @throws ParseArgumentException if any required flag is missing or invalid
     */
    public static SSHSearchDatabaseArguments parseArguments(String[] args) throws ParseArgumentException {
        String type = null;
        String database = null;
        Integer topFreq = null;

        for (String arg : args) {
            if (arg.startsWith("--type=")) {
                type = arg.substring("--type=".length());
            } else if (arg.startsWith("--database=")) {
                database = arg.substring("--database=".length());
            } else if (arg.startsWith("--top-frequency=")) {
                int val = ParseArgumentUtils.convertStringToInt(arg.substring("--top-frequency=".length()));
                if (val != 10 && val != 25 && val != 50) {
                    throw new ParseArgumentException("--top-frequency must be 10, 25, or 50.");
                }
                topFreq = val;
            } else {
                throw new ParseArgumentException("Unknown argument: " + arg);
            }
        }

        if (type == null || !VALID_TYPES.contains(type)) {
            throw new ParseArgumentException("Missing or invalid --type argument.");
        }
        if (database == null || database.isEmpty()) {
            throw new ParseArgumentException("Missing --database argument.");
        }
        if (topFreq == null) {
            throw new ParseArgumentException("Missing --top-frequency argument.");
        }

        return new SSHSearchDatabaseArguments(type, database, topFreq);
    }

}
