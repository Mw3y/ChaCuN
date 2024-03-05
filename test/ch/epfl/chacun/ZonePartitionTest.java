package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.Zone.Forest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZonePartitionTest {
    @Test
    void areaContainingThrowsOnUnassignedZone() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.RED));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));

        assertThrows(IllegalArgumentException.class
                , () -> zonePartition.areaContaining(new Forest(1, Forest.Kind.PLAIN)));
    }

    @Test
    void areaContainingWorks() {
        Set<Forest> forestZones = new HashSet<>();
        Forest forest = new Forest(0, Forest.Kind.PLAIN);
        forestZones.add(forest);
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.RED));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));

        assertEquals(forestArea, zonePartition.areaContaining(forest));
    }

    @Test
    void addSingletonWorks() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.RED));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition.Builder<Forest> builder = new ZonePartition.Builder<>(zonePartition);

        Set<Forest> newForestZones = new HashSet<>();
        Forest newForest = new Forest(1, Forest.Kind.WITH_MUSHROOMS);
        newForestZones.add(newForest);
        List<PlayerColor> newOccupants = new ArrayList<>();
        Area<Forest> newForestArea = new Area<>(newForestZones, newOccupants, 1);

        builder.addSingleton(newForest, 1);
        ZonePartition<Forest> buildedZonePartition = builder.build();

        Set<Area> expectedAreas = new HashSet<>();
        expectedAreas.add(forestArea);
        expectedAreas.add(newForestArea);

        assertEquals(expectedAreas, buildedZonePartition.areas());
    }

    @Test
    void addInitialOccupantThrowsOnUnAssignedZone() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

        assertThrows(IllegalArgumentException.class
                , () -> builder.addInitialOccupant(new Forest(1, Forest.Kind.WITH_MENHIR), PlayerColor.BLUE));
    }

    @Test
    void addInitialOccupantThrowsOnOccupiedArea() {
        Set<Forest> forestZones = new HashSet<>();
        Forest forest = new Forest(0, Forest.Kind.PLAIN);
        forestZones.add(forest);
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.YELLOW));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition.Builder builder = new ZonePartition.Builder(zonePartition);

        assertThrows(IllegalArgumentException.class
                , () -> builder.addInitialOccupant(forest, PlayerColor.BLUE));
    }

    @Test
    void addInitialOccupantWorks() {
        Set<Forest> forestZones = new HashSet<>();
        Forest forest = new Forest(0, Forest.Kind.PLAIN);
        forestZones.add(forest);
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition.Builder<Forest> builder = new ZonePartition.Builder<>(zonePartition);

        List<PlayerColor> newOccupants = new ArrayList<>(List.of(PlayerColor.YELLOW));
        Area<Forest> newForestArea = new Area<>(forestZones, newOccupants, 3);

        builder.addInitialOccupant(forest, PlayerColor.YELLOW);
        Set<Area> expectedAreas = new HashSet<>();
        expectedAreas.add(newForestArea);

        assertEquals(expectedAreas, builder.build().areas());
    }

    @Test
    void removeOccupantWorks() {
        Set<Forest> forestZones = new HashSet<>();
        Forest forest = new Forest(0, Forest.Kind.PLAIN);
        forestZones.add(forest);
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.YELLOW));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition.Builder<Forest> builder = new ZonePartition.Builder<>(zonePartition);

        List<PlayerColor> newOccupants = new ArrayList<>();
        Area<Forest> newForestArea = new Area<>(forestZones, newOccupants, 3);

        builder.removeOccupant(forest, PlayerColor.YELLOW);
        Set<Area> expectedAreas = new HashSet<>();
        expectedAreas.add(newForestArea);

        assertEquals(expectedAreas, builder.build().areas());
    }

    @Test
    void removeAllOccupantsOfThrowsOnUnknownArea() {
        Set<Forest> forestZones = new HashSet<>();
        Forest forest = new Forest(0, Forest.Kind.PLAIN);
        forestZones.add(forest);
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.YELLOW));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition.Builder<Forest> builder = new ZonePartition.Builder<>(zonePartition);

        Forest unknownForest = new Forest(1, Forest.Kind.WITH_MENHIR);
        Area<Forest> unknownArea = new Area<>(Set.of(unknownForest), occupants, 2);

        assertThrows(IllegalArgumentException.class, () -> builder.removeAllOccupantsOf(unknownArea));
    }

    @Test
    void removeAllOccupantsOfWorks() {
        Set<Forest> forestZones = new HashSet<>();
        Forest forest = new Forest(0, Forest.Kind.PLAIN);
        forestZones.add(forest);
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.YELLOW, PlayerColor.GREEN));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea));
        ZonePartition.Builder<Forest> builder = new ZonePartition.Builder<>(zonePartition);

        List<PlayerColor> newOccupants = new ArrayList<>();
        Area<Forest> newForestArea = new Area<>(forestZones, newOccupants, 3);

        builder.removeAllOccupantsOf(forestArea);
        Set<Area> expectedAreas = new HashSet<>();
        expectedAreas.add(newForestArea);

        assertEquals(expectedAreas, builder.build().areas());
    }
    @Test
    void unionWorksWithDifferentAreas() {
        Set<Forest> forestZones1 = new HashSet<>();
        Forest forest1 = new Forest(0, Forest.Kind.PLAIN);
        forestZones1.add(forest1);
        List<PlayerColor> occupants1 = new ArrayList<>(List.of(PlayerColor.YELLOW));
        Area<Forest> forestArea1 = new Area<>(forestZones1, occupants1, 3);

        Set<Forest> forestZones2 = new HashSet<>();
        Forest forest2 = new Forest(1, Forest.Kind.WITH_MENHIR);
        forestZones2.add(forest2);
        List<PlayerColor> occupants2 = new ArrayList<>(List.of(PlayerColor.RED));
        Area<Forest> forestArea2 = new Area<>(forestZones2, occupants2, 3);

        ZonePartition<Forest> zonePartition = new ZonePartition<>(Set.of(forestArea1, forestArea2));
        ZonePartition.Builder<Forest> builder = new ZonePartition.Builder<>(zonePartition);

        Set<Forest> expectedForestZones = new HashSet<>();
        expectedForestZones.add(forest1);
        expectedForestZones.add(forest2);
        List<PlayerColor> expectedOccupants = new ArrayList<>();
        expectedOccupants.add(PlayerColor.YELLOW);
        expectedOccupants.add(PlayerColor.RED);
        Area<Forest> expectedArea = new Area<>(expectedForestZones, expectedOccupants, 4);

        builder.union(forest1, forest2);

        assertEquals(Set.of(expectedArea), builder.build().areas());
    }

}
