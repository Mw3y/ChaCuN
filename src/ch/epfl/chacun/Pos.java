package ch.epfl.chacun;

/**
 * Represents a position on the board of size 25x25.
 *
 * @param x the x coordinate of the position
 * @param y the y coordinate of the position
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record Pos(int x, int y) {

    // The origin of the board
    public final static Pos ORIGIN = new Pos(0, 0);

    /**
     * Translates the current position by a given amount.
     *
     * @param dX the amount by which the x coordinate should be translated
     * @param dY the amount by which the x coordinate should be translated
     * @return the translated position
     */
    public Pos translated(int dX, int dY) {
        return new Pos(x + dX, y + dY);
    }

    /**
     * Returns the neighbor of the current position in a given direction.
     *
     * @param direction the direction in which to find the neighbor
     * @return the neighbor of the current position in the given direction
     */
    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case N -> translated(0, -1);
            case E -> translated(1, 0);
            case S -> translated(0, 1);
            case W -> translated(-1, 0);
        };
    }
}
