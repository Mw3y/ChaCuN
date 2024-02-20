package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerColorTest {
    @Test
    void playerColorAllIsCorrectlyDefined() {
        assertEquals(5, PlayerColor.ALL.size());
        assertEquals(PlayerColor.RED, PlayerColor.ALL.get(0));
        assertEquals(PlayerColor.BLUE, PlayerColor.ALL.get(1));
        assertEquals(PlayerColor.GREEN, PlayerColor.ALL.get(2));
        assertEquals(PlayerColor.YELLOW, PlayerColor.ALL.get(3));
        assertEquals(PlayerColor.PURPLE, PlayerColor.ALL.get(4));
    }
}