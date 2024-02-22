package ch.epfl.chacun;

public final class Preconditions {

    private Preconditions() {}

    public static void checkArgument(boolean precondition) {
        if (!precondition) {
            throw new IllegalArgumentException();
        }
    }
}
