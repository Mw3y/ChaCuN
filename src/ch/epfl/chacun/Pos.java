package ch.epfl.chacun;

public record Pos(int x, int y) {
    public final static Pos ORIGIN = new Pos(0, 0);

    public Pos translated(int dX, int dY) {
        // The maximum board size is 25x25
        Preconditions.checkArgument(x + dX <= 12 && x + dX >= -12);
        Preconditions.checkArgument(y + dY <= 12 && y + dY >= -12);
        return new Pos(x + dX, y + dY);
    }

    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case N -> translated(0, -1);
            case E -> translated(1, 0);
            case S -> translated(0, 1);
            case W -> translated(-1, 0);
        };
    }
}
