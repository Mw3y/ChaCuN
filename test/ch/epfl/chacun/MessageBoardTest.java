package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageBoardTest {

    @Test
    void testWithScoredForestAndPoints() {
        TextMaker textMaker = new TextMakerTestImplementation();
        MessageBoard messageBoard = new MessageBoard(textMaker, List.of());
        Zone.Forest forest1 = new Zone.Forest(230, Zone.Forest.Kind.WITH_MUSHROOMS);
        Zone.Forest forest2 = new Zone.Forest(331, Zone.Forest.Kind.WITH_MENHIR);

        Area<Zone.Forest> area = new Area<>(Set.of(forest1, forest2), List.of(PlayerColor.YELLOW, PlayerColor.YELLOW, PlayerColor.GREEN), 10);
        Area<Zone.Forest> area2 = new Area<>(Set.of(forest1, forest2), List.of(), 10);
        Area<Zone.Forest> area3 = new Area<>(Set.of(forest1, forest2), List.of(PlayerColor.RED, PlayerColor.GREEN), 10);

        int points = Points.forClosedForest(2, 1);
        MessageBoard newMessageBoard = messageBoard.withScoredForest(area);
        MessageBoard newMessageBoard2 = newMessageBoard.withScoredForest(area2);
        MessageBoard newMessageBoard3 = newMessageBoard2.withClosedForestWithMenhir(PlayerColor.YELLOW, area3);

        assertEquals(newMessageBoard, newMessageBoard2);
        assertEquals("forest.scored([YELLOW]," + points + ",1," + area.tileIds().size() + ")", newMessageBoard2.messages().getFirst().text());
        assertEquals("forest.closed.withMenhir(YELLOW)", newMessageBoard3.messages().get(1).text());
    }

}