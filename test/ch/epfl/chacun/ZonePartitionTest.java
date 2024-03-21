package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ZonePartitionTest {
    @Test
    void zonePartitionIsImmutable() {
        var areas = new HashSet<Area<Zone.Forest>>();
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(), 0);
        var a1 = new Area<>(Set.of(f1), List.of(), 0);
        areas.add(a0);
        areas.add(a1);
        var partition = new ZonePartition<>(areas);
        areas.clear();
        assertEquals(Set.of(a0, a1), partition.areas());

        try {
            partition.areas().clear();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        assertEquals(Set.of(a0, a1), partition.areas());
    }

    @Test
    void zonePartitionSecondaryConstructorWorks() {
        assertEquals(Set.<Area<Zone.Forest>>of(), new ZonePartition<Zone.Forest>().areas());
    }

    @Test
    void zonePartitionAreaContainingWorks() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var f2 = new Zone.Forest(2, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(), 0);
        var a1 = new Area<>(Set.of(f1), List.of(), 0);
        var a2 = new Area<>(Set.of(f2), List.of(), 0);
        var partition = new ZonePartition<>(Set.of(a0, a1, a2));
        assertEquals(a0, partition.areaContaining(f0));
        assertEquals(a1, partition.areaContaining(f1));
        assertEquals(a2, partition.areaContaining(f2));
    }

    @Test
    void zonePartitionAreaContainingThrowsIfZoneIsNotInArea() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(), 0);
        var a1 = new Area<>(Set.of(f1), List.of(), 0);
        var partition = new ZonePartition<>(Set.of(a0, a1));
        assertThrows(IllegalArgumentException.class, () -> {
            partition.areaContaining(new Zone.Forest(2, Zone.Forest.Kind.PLAIN));
        });
    }

    @Test
    void zonePartitionBuilderConstructorCorrectlyCopiesInitialValue() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(), 0);
        var a1 = new Area<>(Set.of(f1), List.of(), 0);
        var partition = new ZonePartition<>(Set.of(a0, a1));

        var partitionBuilder = new ZonePartition.Builder<>(partition);
        assertEquals(partition, partitionBuilder.build());
    }

    @Test
    void zonePartitionBuilderAddSingletonCreatesCorrectArea() {
        var emptyPartition = new ZonePartition<Zone.Forest>();
        var partitionBuilder = new ZonePartition.Builder<>(emptyPartition);

        var zone = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var openConnections = 1;
        partitionBuilder.addSingleton(zone, openConnections);
        var expectedArea = new Area<>(Set.of(zone), List.of(), openConnections);
        var expectedPartition = new ZonePartition<>(Set.of(expectedArea));
        assertEquals(expectedPartition, partitionBuilder.build());
    }

    @Test
    void zonePartitionBuilderAddInitialOccupantThrowsIfZoneDoesNotBelongToPartition() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(), 0);
        var partition = new ZonePartition<>(Set.of(a0));
        var partitionBuilder = new ZonePartition.Builder<>(partition);
        assertThrows(IllegalArgumentException.class, () -> {
            partitionBuilder.addInitialOccupant(f1, PlayerColor.RED);
        });
    }

    @Test
    void zonePartitionBuilderAddInitialOccupantThrowsIfAreaIsAlreadyOccupied() {
        var zones1 = Set.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN));
        var zones2 = Set.of(
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(5, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(6, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        var area1 = new Area<>(zones1, List.of(PlayerColor.RED), 0);
        var area2 = new Area<>(zones2, List.of(PlayerColor.BLUE), 0);
        var partition = new ZonePartition<>(Set.of(area1, area2));
        var allZones = new HashSet<>(zones1);
        allZones.addAll(zones2);
        for (var z : allZones) {
            var partitionBuilder = new ZonePartition.Builder<>(partition);
            assertThrows(IllegalArgumentException.class, () -> {
                partitionBuilder.addInitialOccupant(z, PlayerColor.YELLOW);
            });
        }
    }

    @Test
    void zonePartitionBuilderAddInitialOccupantWorksForUnoccupiedAreas() {
        var zones1 = Set.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN));
        var zones2 = Set.of(
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(5, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(6, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        var area1 = new Area<>(zones1, List.of(), 0);
        var area2 = new Area<>(zones2, List.of(), 0);
        var partition = new ZonePartition<>(Set.of(area1, area2));
        for (var z : zones1) {
            for (var occupant : PlayerColor.values()) {
                var partitionBuilder = new ZonePartition.Builder<>(partition);
                partitionBuilder.addInitialOccupant(z, occupant);
                var partition1 = partitionBuilder.build();
                var occupiedArea1 = new Area<>(area1.zones(), List.of(occupant), area1.openConnections());
                assertEquals(Set.of(occupiedArea1, area2), partition1.areas());
            }
        }
        for (var z : zones2) {
            for (var occupant : PlayerColor.values()) {
                var partitionBuilder = new ZonePartition.Builder<>(partition);
                partitionBuilder.addInitialOccupant(z, occupant);
                var partition1 = partitionBuilder.build();
                var occupiedArea2 = new Area<>(area2.zones(), List.of(occupant), area2.openConnections());
                assertEquals(Set.of(area1, occupiedArea2), partition1.areas());
            }
        }
    }

    @Test
    void zonePartitionBuilderRemoveOccupantThrowsIfZoneDoesNotBelongToPartition() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(PlayerColor.RED), 0);
        var partition = new ZonePartition<>(Set.of(a0));
        var partitionBuilder = new ZonePartition.Builder<>(partition);
        assertThrows(IllegalArgumentException.class, () -> {
            partitionBuilder.removeOccupant(f1, PlayerColor.RED);
        });
    }

    @Test
    void zonePartitionBuilderRemoveOccupantThrowsIfAreaIsUnoccupied() {
        var zones1 = Set.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN));
        var zones2 = Set.of(
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(5, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(6, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        var area1 = new Area<>(zones1, List.of(), 0);
        var area2 = new Area<>(zones2, List.of(PlayerColor.RED), 0);
        var partition = new ZonePartition<>(Set.of(area1, area2));

        var allZones = new HashSet<>(zones1);
        allZones.addAll(zones2);
        for (var z : allZones) {
            for (var color : PlayerColor.values()) {
                if (color == PlayerColor.RED) continue;
                var partitionBuilder = new ZonePartition.Builder<>(partition);
                assertThrows(IllegalArgumentException.class, () -> {
                    partitionBuilder.removeOccupant(z, color);
                });
            }
        }
    }

    @Test
    void zonePartitionBuilderRemoveOccupantWorksForOccupiedAreas() {
        var zones1 = Set.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN));
        var zones2 = Set.of(
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(5, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(6, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        var area1 = new Area<>(zones1, List.of(PlayerColor.RED, PlayerColor.RED), 0);
        var area2 = new Area<>(zones2, List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.PURPLE), 0);
        var partition = new ZonePartition<>(Set.of(area1, area2));
        for (var z : zones1) {
            var partitionBuilder = new ZonePartition.Builder<>(partition);
            partitionBuilder.removeOccupant(z, PlayerColor.RED);
            var partition1 = partitionBuilder.build();
            var lessOccupiedArea1 = new Area<>(area1.zones(), List.of(PlayerColor.RED), area1.openConnections());
            assertEquals(Set.of(lessOccupiedArea1, area2), partition1.areas());
        }
        for (var z : zones2) {
            var partitionBuilder = new ZonePartition.Builder<>(partition);
            partitionBuilder.removeOccupant(z, PlayerColor.GREEN);
            var partition1 = partitionBuilder.build();
            var lessOccupiedArea2 = new Area<>(area2.zones(), List.of(PlayerColor.BLUE, PlayerColor.PURPLE), area2.openConnections());
            assertEquals(Set.of(area1, lessOccupiedArea2), partition1.areas());
        }
    }

    @Test
    void zonePartitionBuilderRemoveAllOccupantsOfThrowsIfZoneDoesNotBelongToPartition() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(), 0);
        var a1 = new Area<>(Set.of(f1), List.of(), 0);
        var partition = new ZonePartition<>(Set.of(a0));
        var partitionBuilder = new ZonePartition.Builder<>(partition);
        assertThrows(IllegalArgumentException.class, () -> {
            partitionBuilder.removeAllOccupantsOf(a1);
        });
    }

    @Test
    void zonePartitionBuilderRemoveAllOccupantOfWorksForOccupiedAreas() {
        var zones1 = Set.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN));
        var zones2 = Set.of(
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(5, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(6, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        var area1 = new Area<>(zones1, List.of(PlayerColor.RED, PlayerColor.RED), 0);
        var area2 = new Area<>(zones2, List.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.PURPLE), 0);
        var partition = new ZonePartition<>(Set.of(area1, area2));

        var partitionBuilder1 = new ZonePartition.Builder<>(partition);
        partitionBuilder1.removeAllOccupantsOf(area1);
        var unoccupiedArea1 = new Area<>(area1.zones(), List.of(), area1.openConnections());
        assertEquals(Set.of(unoccupiedArea1, area2), partitionBuilder1.build().areas());

        var partitionBuilder2 = new ZonePartition.Builder<>(partition);
        partitionBuilder2.removeAllOccupantsOf(area2);
        var unoccupiedArea2 = new Area<>(area2.zones(), List.of(), area2.openConnections());
        assertEquals(Set.of(area1, unoccupiedArea2), partitionBuilder2.build().areas());
    }

    @Test
    void zonePartitionBuilderUnionThrowsIfOneZoneDoesNotBelongToPartition() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var f2 = new Zone.Forest(2, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(), 0);
        var a1 = new Area<>(Set.of(f1), List.of(), 0);
        var partition = new ZonePartition<>(Set.of(a0, a1));
        var partitionBuilder = new ZonePartition.Builder<>(partition);
        assertThrows(IllegalArgumentException.class, () -> {
            partitionBuilder.union(f0, f2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            partitionBuilder.union(f2, f1);
        });
    }

    @Test
    void zonePartitionBuilderUnionWorksWithTwoDifferentAreas() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(PlayerColor.RED), 1);
        var a1 = new Area<>(Set.of(f1), List.of(PlayerColor.RED), 1);
        var partition = new ZonePartition<>(Set.of(a0, a1));
        var partitionBuilder = new ZonePartition.Builder<>(partition);
        partitionBuilder.union(f0, f1);
        var expectedArea = new Area<>(Set.of(f0, f1), List.of(PlayerColor.RED, PlayerColor.RED), 0);
        assertEquals(Set.of(expectedArea), partitionBuilder.build().areas());
    }

    @Test
    void zonePartitionBuilderUnionWorksWithOneArea() {
        var f0 = new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
        var f1 = new Zone.Forest(1, Zone.Forest.Kind.PLAIN);
        var f2 = new Zone.Forest(2, Zone.Forest.Kind.PLAIN);
        var a0 = new Area<>(Set.of(f0), List.of(PlayerColor.RED), 1);
        var a1 = new Area<>(Set.of(f1, f2), List.of(PlayerColor.RED), 2);
        var partition = new ZonePartition<>(Set.of(a0, a1));
        var partitionBuilder = new ZonePartition.Builder<>(partition);
        partitionBuilder.union(f1, f2);
        var expectedArea1 = new Area<>(a1.zones(), a1.occupants(), a1.openConnections() - 2);
        assertEquals(Set.of(a0, expectedArea1), partitionBuilder.build().areas());
    }
}