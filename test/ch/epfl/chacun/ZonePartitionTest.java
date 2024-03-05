package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.Zone.*;
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
    void addInitialOccupant() {

    }
}
