package cs321.common;

/**
 * Utility methods for parsing and validating command-line arguments.
 */
public class ParseArgumentUtils {

    /**
     * Verifies that lowRangeInclusive <= argument <= highRangeInclusive.
     */
    public static void verifyRanges(int argument, int lowRangeInclusive, int highRangeInclusive)
            throws ParseArgumentException {
        if (argument < lowRangeInclusive || argument > highRangeInclusive) {
            throw new ParseArgumentException(
                "Argument " + argument + " is out of range [" + lowRangeInclusive + ", " + highRangeInclusive + "]"
            );
        }
    }

    /**
     * Converts a String to an int, throwing a ParseArgumentException if invalid.
     */
    public static int convertStringToInt(String argument) throws ParseArgumentException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new ParseArgumentException("Invalid integer value: " + argument);
        }
    }
}