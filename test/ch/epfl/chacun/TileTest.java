package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TileTest {
    @Test
    void tileSidesReturnsSidesInCorrectOrder() {
        var l0 = new Zone.Lake(1_8, 3, null);
        var z0 = new Zone.Meadow(1_0, List.of(), null);
        var z1 = new Zone.River(1_1, 0, l0);
        var z2 = new Zone.Meadow(1_2, List.of(), null);
        var z3 = new Zone.Forest(1_3, Zone.Forest.Kind.PLAIN);
        var z4 = new Zone.Meadow(1_4, List.of(), null);
        var z5 = new Zone.River(1_5, 0, l0);
        var sN = new TileSide.Meadow(z0);
        var sE = new TileSide.River(z0, z1, z2);
        var sS = new TileSide.Forest(z3);
        var sW = new TileSide.River(z4, z5, z0);
        var tile = new Tile(1, Tile.Kind.NORMAL, sN, sE, sS, sW);

        assertEquals(List.of(sN, sE, sS, sW), tile.sides());
    }

    @Test
    void tileSideZonesReturnsOnlySideZones() {
        var l0 = new Zone.Lake(4_8, 1, null);
        var z0 = new Zone.Meadow(4_0, List.of(), null);
        var z1 = new Zone.River(4_1, 0, l0);
        var a2_0 = new Animal(4_2_0, Animal.Kind.DEER);
        var z2 = new Zone.Meadow(4_2, List.of(a2_0), null);
        var z3 = new Zone.Forest(4_3, Zone.Forest.Kind.PLAIN);
        var z4 = new Zone.Meadow(4_4, List.of(), null);
        var z5 = new Zone.River(4_5, 0, l0);
        var sN = new TileSide.River(z0, z1, z2);
        var sE = new TileSide.Forest(z3);
        var sS = new TileSide.River(z4, z5, z0);
        var sW = new TileSide.Meadow(z0);
        var tile = new Tile(4, Tile.Kind.NORMAL, sN, sE, sS, sW);

        assertEquals(Set.of(z0, z1, z2, z3, z4, z5), tile.sideZones());
    }

    @Test
    void tileZonesReturnsAllZones() {
        var l0 = new Zone.Lake(4_8, 1, null);
        var z0 = new Zone.Meadow(4_0, List.of(), null);
        var z1 = new Zone.River(4_1, 0, l0);
        var a2_0 = new Animal(4_2_0, Animal.Kind.DEER);
        var z2 = new Zone.Meadow(4_2, List.of(a2_0), null);
        var z3 = new Zone.Forest(4_3, Zone.Forest.Kind.PLAIN);
        var z4 = new Zone.Meadow(4_4, List.of(), null);
        var z5 = new Zone.River(4_5, 0, l0);
        var sN = new TileSide.River(z0, z1, z2);
        var sE = new TileSide.Forest(z3);
        var sS = new TileSide.River(z4, z5, z0);
        var sW = new TileSide.Meadow(z0);
        var tile = new Tile(4, Tile.Kind.NORMAL, sN, sE, sS, sW);

        assertEquals(Set.of(z0, z1, z2, z3, z4, z5, l0), tile.zones());
    }
}