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
     * The size of the base 32 alphabet.
     */
    public static final int ALPHABET_SIZE = ALPHABET.length();

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
        int fiveLSB = value & ((1 << 5) - 1);
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
        return encodeBits5(value) + encodeBits5(value >> 5);
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
        int decodedValue = 0;
        // For each value, add its index in the base 32 alphabet times the corresponding power of 32
        // to the decoded integer value
        for (int i = 0; i < value.length(); ++i) {
            decodedValue += (int) (ALPHABET.indexOf(value.charAt(i)) * Math.pow(ALPHABET_SIZE, i));
        }
        return decodedValue;
    }
}
