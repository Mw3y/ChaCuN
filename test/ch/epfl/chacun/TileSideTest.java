package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TileSideTest {
    @Test
    void tileSideForestZonesWorks() {
        var forest = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var forestSide = new TileSide.Forest(forest);
        assertEquals(List.of(forest), forestSide.zones());
    }

    @Test
    void tileSideMeadowZonesWorks() {
        var meadow = new Zone.Meadow(0, List.of(), null);
        var meadowSide = new TileSide.Meadow(meadow);
        assertEquals(List.of(meadow), meadowSide.zones());
    }

    @Test
    void tileSideRiverZonesWorks() {
        var meadow1 = new Zone.Meadow(0, List.of(), null);
        var river = new Zone.River(1, 0, null);
        var meadow2 = new Zone.Meadow(2, List.of(), null);
        var riverSide = new TileSide.River(meadow1, river, meadow2);
        assertEquals(List.of(meadow1, river, meadow2), riverSide.zones());
    }

    @Test
    void tileSideIsSameKindWorksForAllCombinations() {
        var forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var forest2 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MUSHROOMS);
        var meadow1 = new Zone.Meadow(2, List.of(), null);
        var meadow2 = new Zone.Meadow(3, List.of(), null);
        var meadow3 = new Zone.Meadow(4, List.of(), null);
        var meadow4 = new Zone.Meadow(5, List.of(), null);
        var river1 = new Zone.River(6, 0, new Zone.Lake(9, 0, null));
        var river2 = new Zone.River(7, 0, null);

        var forestSide1 = new TileSide.Forest(forest1);
        var forestSide2 = new TileSide.Forest(forest2);
        var meadowSide1 = new TileSide.Meadow(meadow1);
        var meadowSide2 = new TileSide.Meadow(meadow2);
        var riverSide1 = new TileSide.River(meadow1, river1, meadow3);
        var riverSide2 = new TileSide.River(meadow2, river2, meadow4);

        assertTrue(forestSide1.isSameKindAs(forestSide2));
        assertTrue(forestSide2.isSameKindAs(forestSide1));
        assertTrue(meadowSide1.isSameKindAs(meadowSide2));
        assertTrue(meadowSide2.isSameKindAs(meadowSide1));
        assertTrue(riverSide1.isSameKindAs(riverSide2));
        assertTrue(riverSide2.isSameKindAs(riverSide1));

        assertFalse(forestSide1.isSameKindAs(meadowSide1));
        assertFalse(forestSide1.isSameKindAs(riverSide1));
        assertFalse(meadowSide1.isSameKindAs(forestSide1));
        assertFalse(meadowSide1.isSameKindAs(riverSide1));
        assertFalse(riverSide1.isSameKindAs(forestSide1));
        assertFalse(riverSide1.isSameKindAs(meadowSide1));
    }
}