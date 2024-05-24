package ch.epfl.chacun;

/**
 * Helper class to encode and decode values in base 32.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public class Base32 {

    /**
     * The alphabet representing the base 32.
     */
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    /**
     * The number of bits to represent a symbol in base 32.
     */
    private static final int BASE_32_SYMBOL_BITS = 5;

    /**
     * Checks if the given string is valid in base 32.
     *
     * @param string the string to be checked
     * @return true if and only if the string is valid and false otherwise
     */
    public static boolean isValid(String string) {
        return string.chars().allMatch(c -> ALPHABET.indexOf(c) != -1);
    }

    /**
     * Encodes in base 32 a 5-bit value.
     *
     * @param value the 5-bit integer value to be encoded
     * @return the string containing the encoded value
     */
    public static String encodeBits5(int value) {
        // Keep only the 5 less significand bits
        int fiveLSB = value & ((1 << BASE_32_SYMBOL_BITS) - 1);
        return String.valueOf(ALPHABET.charAt(fiveLSB));
    }

    /**
     * Encodes in base 32 a 10-bit value.
     *
     * @param value the 10-bit integer value to be encoded
     * @return a string containing the encoded value
     */
    public static String encodeBits10(int value) {
        // Encoded independently two 5-bit parts and merge them
        return encodeBits5(value >> BASE_32_SYMBOL_BITS) + encodeBits5(value);
    }

    /**
     * Decodes the given string value which is in base 32.
     * <p>
     * Decode each character of the value independently.
     *
     * @param value the string value to be decoded
     * @return the integer corresponding to the decoded value
     */
    public static int decode(String value) {
        Preconditions.checkArgument(isValid(value));
        int decodedValue = 0;
        // For each value, add its index in the base 32 alphabet times
        for (int i = 0; i < value.length(); ++i) {
            decodedValue = decodedValue << BASE_32_SYMBOL_BITS | ALPHABET.indexOf(value.charAt(i));
        }
        return decodedValue;
    }
}
