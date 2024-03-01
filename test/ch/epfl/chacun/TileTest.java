package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TileTest {

    @Test
    void sidesWorksWithAllSidesProvided() {
        Zone.Forest forest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow1 = new Zone.Meadow(2, new ArrayList<>(), null);
        Zone.River river1 = new Zone.River(3, 9, null);
        Zone.Meadow meadow2 = new Zone.Meadow(4, new ArrayList<>(), null);

        TileSide.Forest forestSide = new TileSide.Forest(forest);
        TileSide.Meadow meadowSide1 = new TileSide.Meadow(meadow1);
        TileSide.River riverSide = new TileSide.River(meadow1, river1, meadow2);
        TileSide.Meadow meadowSide2 = new TileSide.Meadow(meadow2);
        Tile tile = new Tile(0, null, forestSide, meadowSide1, riverSide, meadowSide2);

        List<TileSide> expectedSides = List.of(forestSide, meadowSide1, riverSide, meadowSide2);
        assertEquals(expectedSides, tile.sides());
    }

    @Test
    void sideZonesWorksWithRiverAndOtherZones() {
        Zone.Forest forest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow1 = new Zone.Meadow(2, new ArrayList<>(), null);
        Zone.River river1 = new Zone.River(3, 9, null);
        Zone.Meadow meadow2 = new Zone.Meadow(4, new ArrayList<>(), null);

        TileSide.Forest forestSide = new TileSide.Forest(forest);
        TileSide.Meadow meadowSide1 = new TileSide.Meadow(meadow1);
        TileSide.River riverSide = new TileSide.River(meadow1, river1, meadow2);
        TileSide.Meadow meadowSide2 = new TileSide.Meadow(meadow2);
        Tile tile = new Tile(0, null, forestSide, meadowSide1, riverSide, meadowSide2);

        Set<Zone> expectedSideZones = Set.of(forest, meadow1, river1, meadow2);
        assertEquals(expectedSideZones, tile.sideZones());
    }

    @Test
    void zonesWorksWithLake() {
        Zone.Forest forest = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        Zone.Meadow meadow1 = new Zone.Meadow(2, new ArrayList<>(), null);
        Zone.Lake lake = new Zone.Lake(3, 9, null);
        Zone.River river1 = new Zone.River(3, 9, lake);
        Zone.Meadow meadow2 = new Zone.Meadow(4, new ArrayList<>(), null);

        TileSide.Forest forestSide = new TileSide.Forest(forest);
        TileSide.Meadow meadowSide1 = new TileSide.Meadow(meadow1);
        TileSide.River riverSideWithLake = new TileSide.River(meadow1, river1, meadow2);
        TileSide.Meadow meadowSide2 = new TileSide.Meadow(meadow2);
        Tile tile = new Tile(0, null, forestSide, meadowSide1, riverSideWithLake, meadowSide2);

        Set<Zone> expectedZones = Set.of(forest, meadow1, river1, lake, meadow2);
        assertEquals(expectedZones, tile.zones());
    }

}
