package ch.epfl.chacun;

/**
 * Represents an animal.
 *
 * @param id   the id of the animal
 * @param kind the kind of the animal
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record Animal(int id, Kind kind) {

    /**
     * Returns the id of the tile on which the animal is located.
     *
     * @return the id of the tile on which the animal is located
     */
    public int tileId() {
        // id = 10 * zoneId + animalNumber
        return Zone.tileId(id / 10);
    }

    /**
     * Represents the different kinds of animals.
     */
    public enum Kind {
        MAMMOTH,
        AUROCHS,
        DEER,
        TIGER
    }

}
