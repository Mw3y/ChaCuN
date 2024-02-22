package ch.epfl.chacun;

public record Animal(int id, Kind kind) {

    public enum Kind {
        MAMMOTH,
        AUROCHS,
        DEER,
        TIGER
    }

    public int tileId() {

    }

}
