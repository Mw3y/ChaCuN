package ch.epfl.chacun;

/**
 * Helper class to check preconditions.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class Preconditions {

    /**
     * Private constructor to prevent instantiation.
     */
    private Preconditions() {
    }

    /**
     * Check if the given precondition has been satisfied.
     *
     * @param precondition the precondition to check
     * @throws IllegalArgumentException if the precondition is false
     */
    public static void checkArgument(boolean precondition) {
        if (!precondition)
            throw new IllegalArgumentException();
    }
}
