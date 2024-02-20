package ch.epfl.chacun;

public final class Preconditions {

    private Preconditions() {}

    public static void checkArgument(boolean isPreconditionFullfilled) {
        if (!isPreconditionFullfilled) {
            throw new IllegalArgumentException();
        }
    }
}
