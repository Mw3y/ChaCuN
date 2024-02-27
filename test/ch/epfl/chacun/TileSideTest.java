package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TileSideTest {

    @Test
    void isSameKindDoesntWorkForNull() {
        Zone.Forest forest = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        TileSide.Forest forestSide = new TileSide.Forest(forest);
        assertFalse(forestSide.isSameKindAs(null));
    }

    @Test
    void isSameKindDoesntWorkForDifferentKinds() {
        Zone.Forest forest = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow = new Zone.Meadow(0, new ArrayList<>(), null);

        TileSide.Forest forestSide = new TileSide.Forest(forest);
        TileSide.Meadow meadowSide = new TileSide.Meadow(meadow);

        assertFalse(forestSide.isSameKindAs(meadowSide));
    }

    @Test
    void isSameKindWorksForForest() {
        Zone.Forest forest1 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        Zone.Forest forest2 = new Zone.Forest(1, Zone.Forest.Kind.WITH_MENHIR);

        TileSide.Forest forestSide1 = new TileSide.Forest(forest1);
        TileSide.Forest forestSide2 = new TileSide.Forest(forest2);

        assertTrue(forestSide1.isSameKindAs(forestSide2));
    }

    @Test
    void isSameKindWorksForMeadow() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, new ArrayList<>(), null);
        Zone.Meadow meadow2 = new Zone.Meadow(1, new ArrayList<>(), null);

        TileSide.Meadow meadowSide1 = new TileSide.Meadow(meadow1);
        TileSide.Meadow meadowSide2 = new TileSide.Meadow(meadow2);

        assertTrue(meadowSide1.isSameKindAs(meadowSide2));
    }

    @Test
    void isSameKindWorksForRiver() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, new ArrayList<>(), null);
        Zone.River river1 = new Zone.River(0, 9, null);
        Zone.River river2 = new Zone.River(0, 6, null);
        Zone.Meadow meadow2 = new Zone.Meadow(0, new ArrayList<>(), null);

        TileSide.River riverSide1 = new TileSide.River(meadow1, river1, meadow2);
        TileSide.River riverSide2 = new TileSide.River(meadow2, river2, meadow1);

        assertTrue(riverSide1.isSameKindAs(riverSide2));
    }

    @Test
    void zonesWorksForForest() {
        Zone.Forest forest = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        TileSide.Forest forestSide = new TileSide.Forest(forest);
        List<Zone> expectedZones = List.of(forest);

        assertEquals(expectedZones, forestSide.zones());
    }

    @Test
    void zonesWorksForMeadows() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, new ArrayList<>(), null);
        TileSide.Meadow meadowSide = new TileSide.Meadow(meadow1);
        List<Zone> expectedZones = List.of(meadow1);

        assertEquals(expectedZones, meadowSide.zones());
    }
    @Test
    void zonesWorksForRiver() {
        Zone.Meadow meadow1 = new Zone.Meadow(0, new ArrayList<>(), null);
        Zone.River river = new Zone.River(1, 9, null);
        Zone.Meadow meadow2 = new Zone.Meadow(2, new ArrayList<>(), null);

        List<Zone> expectedZones = List.of(meadow1, river, meadow2);
        TileSide.River riverSide = new TileSide.River(meadow1, river, meadow2);

        assertEquals(expectedZones, riverSide.zones());
    }
}
