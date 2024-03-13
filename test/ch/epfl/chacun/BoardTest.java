package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BoardTest {

    @Test
    void tileAt() {
        Board board = Board.EMPTY;
        assertNull(board.tileAt(Pos.ORIGIN));
        assertNull(board.tileAt(new Pos(28, 28)));
        assertNull(board.tileAt(new Pos(-28, -28)));
        assertNull(board.tileAt(new Pos(-1, 28)));
    }

}
