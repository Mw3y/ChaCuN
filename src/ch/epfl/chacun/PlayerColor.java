package ch.epfl.chacun;

import java.util.List;

/**
 * Represents the different possible colors of a player.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public enum PlayerColor {
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE;

    /**
     * All possible colors of a player
     */
    public static final List<PlayerColor> ALL = List.of(PlayerColor.values());

}
