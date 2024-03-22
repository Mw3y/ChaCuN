package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AreaTest {
    @Test
    void areaIsImmutable() {
        var immutableZones = Set.of(new Zone.Meadow(0, List.of(), null));
        var zones = new HashSet<Zone>(immutableZones);

        var immutableOccupants = List.of(PlayerColor.RED);
        var occupants = new ArrayList<>(immutableOccupants);

        var area = new Area<>(zones, occupants, 0);
        zones.clear();
        assertEquals(immutableZones, area.zones());

        occupants.clear();
        assertEquals(immutableOccupants, area.occupants());

        try {
            area.zones().clear();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        assertEquals(immutableZones, area.zones());

        try {
            area.occupants().clear();
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        assertEquals(immutableOccupants, area.occupants());
    }

    @Test
    void areaConstructorSortsOccupants() {
        var sortedOccupants = List.of(
                PlayerColor.RED,
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.YELLOW,
                PlayerColor.YELLOW,
                PlayerColor.YELLOW,
                PlayerColor.YELLOW,
                PlayerColor.PURPLE);
        var rng = new Random(2024);
        for (int i = 0; i < 10; i += 1) {
            var shuffledOccupants = new ArrayList<>(sortedOccupants);
            Collections.shuffle(shuffledOccupants, rng);
            var area = new Area<>(Set.of(), shuffledOccupants, 0);
            assertEquals(sortedOccupants, area.occupants());
        }
    }

    @Test
    void areaConstructorThrowsOnNegativeOpenConnections() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Area<>(Set.of(), List.of(), -1);
        });
    }

    @Test
    void areaHasMenhirWorks() {
        var nonMenhirForests = List.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(5, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Forest(6, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Forest(7, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Forest(8, Zone.Forest.Kind.WITH_MUSHROOMS));
        var menhirForests = List.of(
                new Zone.Forest(9, Zone.Forest.Kind.WITH_MENHIR),
                new Zone.Forest(10, Zone.Forest.Kind.WITH_MENHIR));

        for (int i = 0; i < nonMenhirForests.size(); i += 1) {
            var forests = Set.copyOf(nonMenhirForests.subList(0, i));
            var area = new Area<>(forests, List.of(), 0);
            assertFalse(Area.hasMenhir(area));
        }

        for (int i = 0; i < nonMenhirForests.size(); i += 1) {
            var forests = new HashSet<>(nonMenhirForests.subList(0, i));
            for (int j = 1; j < menhirForests.size(); j += 1) {
                forests.addAll(menhirForests.subList(0, j));
                var area = new Area<>(forests, List.of(), 0);
                assertTrue(Area.hasMenhir(area));
            }
        }
    }

    @Test
    void areaMushroomGroupCountWorks() {
        var nonMushroomForests = List.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(9, Zone.Forest.Kind.WITH_MENHIR),
                new Zone.Forest(10, Zone.Forest.Kind.WITH_MENHIR));
        var mushroomForests = List.of(
                new Zone.Forest(5, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Forest(6, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Forest(7, Zone.Forest.Kind.WITH_MUSHROOMS),
                new Zone.Forest(8, Zone.Forest.Kind.WITH_MUSHROOMS));

        for (int i = 0; i < nonMushroomForests.size(); i += 1) {
            var forests = Set.copyOf(nonMushroomForests.subList(0, i));
            var area = new Area<>(forests, List.of(), 0);
            assertEquals(0, Area.mushroomGroupCount(area));
        }

        for (int i = 0; i < nonMushroomForests.size(); i += 1) {
            var forests = new HashSet<>(nonMushroomForests.subList(0, i));
            for (int j = 0; j < mushroomForests.size(); j += 1) {
                forests.addAll(mushroomForests.subList(0, j));
                var area = new Area<>(forests, List.of(), 0);
                assertEquals(j, Area.mushroomGroupCount(area));
            }
        }

    }

    @Test
    void areaAnimalsWorks() {
        var rng = new Random(2024);
        for (int i = 0; i < 50; i += 1) {
            var animals = new ArrayList<Animal>();
            for (int j = 0; j < i; j += 1) {
                var animalKind = Animal.Kind.values()[rng.nextInt(Animal.Kind.values().length)];
                animals.add(new Animal(j, animalKind));
            }
            var expectedAnimals = Set.copyOf(animals);

            var cancelledAnimals = new HashSet<Animal>();
            for (int j = 0; j < rng.nextInt(20); j += 1) {
                var animalKind = Animal.Kind.values()[rng.nextInt(Animal.Kind.values().length)];
                cancelledAnimals.add(new Animal(100 + j, animalKind));
            }
            var immutableCancelledAnimals = Set.copyOf(cancelledAnimals);
            animals.addAll(cancelledAnimals);

            var meadows = new HashSet<Zone.Meadow>();
            for (int j = 0; j < rng.nextInt(animals.size()); j += 1) {
                var animalCount = rng.nextInt(animals.size());
                var subAnimals = animals.subList(0, animalCount);
                meadows.add(new Zone.Meadow(j, List.copyOf(subAnimals), null));
                subAnimals.clear();
            }
            meadows.add(new Zone.Meadow(i, List.copyOf(animals), null));
            var area = new Area<>(meadows, List.of(), 0);

            assertEquals(expectedAnimals, Area.animals(area, immutableCancelledAnimals));
        }
    }

    @Test
    void areaRiverFishCountWorksWithoutLake() {
        var rivers = Set.of(
                new Zone.River(0, 0, null),
                new Zone.River(1, 1, null),
                new Zone.River(2, 2, null),
                new Zone.River(3, 3, null),
                new Zone.River(4, 2, null),
                new Zone.River(5, 1, null),
                new Zone.River(6, 0, null));
        var area = new Area<>(rivers, List.of(), 0);
        assertEquals(9, Area.riverFishCount(area));
    }

    @Test
    void areaRiverFishCountWorksWithTwoDifferentLakes() {
        var rivers = Set.of(
                new Zone.River(0, 0, new Zone.Lake(8, 1, null)),
                new Zone.River(1, 1, null),
                new Zone.River(2, 2, null),
                new Zone.River(3, 3, null),
                new Zone.River(4, 2, null),
                new Zone.River(5, 1, null),
                new Zone.River(6, 0, new Zone.Lake(9, 2, null)));
        var area = new Area<>(rivers, List.of(), 0);
        assertEquals(12, Area.riverFishCount(area));
    }

    @Test
    void areaRiverFishCountWorksWithOneSharedLake() {
        var lake = new Zone.Lake(8, 1, null);
        var rivers = Set.of(
                new Zone.River(0, 0, lake),
                new Zone.River(1, 1, null),
                new Zone.River(2, 2, null),
                new Zone.River(3, 3, null),
                new Zone.River(4, 2, null),
                new Zone.River(5, 1, null),
                new Zone.River(6, 0, lake));
        var area = new Area<>(rivers, List.of(), 0);
        assertEquals(10, Area.riverFishCount(area));
    }

    @Test
    void areaRiverSystemFishCountWorks() {
        var lake1 = new Zone.Lake(8, 2, null);
        var lake2 = new Zone.Lake(9, 3, null);
        var waterZones = Set.<Zone.Water>of(
                lake1,
                new Zone.River(0, 0, lake1),
                new Zone.River(1, 1, lake1),
                new Zone.River(2, 2, lake1),
                new Zone.River(3, 3, lake1),
                new Zone.River(4, 2, lake2),
                new Zone.River(5, 1, lake2),
                new Zone.River(6, 0, null),
                lake2);
        var area = new Area<>(waterZones, List.of(), 0);
        assertEquals(14, Area.riverSystemFishCount(area));
    }

    @Test
    void areaLakeCountWorks() {
        var lake1 = new Zone.Lake(8, 2, null);
        var lake2 = new Zone.Lake(9, 3, null);
        var lake3 = new Zone.Lake(18, 2, null);
        var waterZones = Set.<Zone.Water>of(
                lake1,
                new Zone.River(0, 0, lake1),
                new Zone.River(1, 1, lake1),
                new Zone.River(2, 2, lake1),
                new Zone.River(3, 3, lake3),
                lake3,
                new Zone.River(4, 2, lake2),
                new Zone.River(5, 1, lake2),
                new Zone.River(6, 0, null),
                lake2);
        var area = new Area<>(waterZones, List.of(), 0);
        assertEquals(3, Area.lakeCount(area));
    }

    @Test
    void areaIsClosedWorks() {
        assertTrue(new Area<>(Set.of(), List.of(), 0).isClosed());
        for (int i = 1; i < 10; i += 1) assertFalse(new Area<>(Set.of(), List.of(), i).isClosed());
    }

    @Test
    void areaIsOccupiedWorks() {
        assertFalse(new Area<>(Set.of(), List.of(), 0).isOccupied());
        for (int i = 1; i < PlayerColor.ALL.size(); i += 1)
            assertTrue(new Area<>(Set.of(), PlayerColor.ALL.subList(0, i), 0).isOccupied());
    }

    @Test
    void areaMajorityOccupantsReturnsEmptySetWhenOccupantsIsEmpty() {
        var area = new Area<>(Set.of(), List.of(), 0);
        assertEquals(Set.of(), area.majorityOccupants());
    }

    @Test
    void areaMajorityOccupantsWorksWithSingleOccupant() {
        var area = new Area<>(Set.of(), List.of(PlayerColor.RED), 0);
        assertEquals(Set.of(PlayerColor.RED), area.majorityOccupants());
    }

    @Test
    void areaMajorityOccupantWorksWithThreeEqualOccupants() {
        var occupants = List.of(
                PlayerColor.RED,
                PlayerColor.RED,
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.BLUE,
                PlayerColor.BLUE,
                PlayerColor.GREEN,
                PlayerColor.GREEN,
                PlayerColor.GREEN);
        var area = new Area<>(Set.of(), occupants, 0);
        assertEquals(
                Set.of(PlayerColor.RED, PlayerColor.BLUE, PlayerColor.GREEN),
                area.majorityOccupants());
    }

    @Test
    void areaMajorityOccupantsWorksInGeneralCase() {
        var occupants = List.of(
                PlayerColor.RED,
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.GREEN,
                PlayerColor.GREEN,
                PlayerColor.GREEN,
                PlayerColor.YELLOW,
                PlayerColor.YELLOW,
                PlayerColor.YELLOW);
        var area = new Area<>(Set.of(), occupants, 0);
        assertEquals(
                Set.of(PlayerColor.GREEN, PlayerColor.YELLOW),
                area.majorityOccupants());
    }

    @Test
    void areaConnectToWorksWhenConnectingAreaWithItself() {
        var zones = List.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN));
        var occupants = List.of(
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.GREEN);
        for (int i = 2; i < zones.size(); i += 1) {
            var areaZones = Set.copyOf(zones.subList(0, i));
            for (int j = 0; j < occupants.size(); j += 1) {
                var areaOccupants = (List<PlayerColor>) new ArrayList<>(occupants.subList(0, j + 1));
                Collections.sort(areaOccupants);
                areaOccupants = Collections.unmodifiableList(areaOccupants);

                for (int openConnections = 2; openConnections < 5; openConnections += 1) {
                    var area = new Area<>(areaZones, areaOccupants, openConnections);
                    var area1 = area.connectTo(area);
                    assertEquals(areaZones, area1.zones());
                    assertEquals(areaOccupants, area1.occupants());
                    assertEquals(openConnections - 2, area1.openConnections());
                }
            }
        }
    }

    @Test
    void areaConnectToWorksWhenConnectingTwoDifferentAreas() {
        var zones = List.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(4, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(5, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(6, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(7, Zone.Forest.Kind.PLAIN));
        var occupants = List.of(
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.RED,
                PlayerColor.BLUE,
                PlayerColor.GREEN);

        var expectedZones = Set.copyOf(zones);
        var expectedOccupants = (List<PlayerColor>) new ArrayList<>(occupants);
        Collections.sort(expectedOccupants);
        expectedOccupants = Collections.unmodifiableList(expectedOccupants);

        for (int i = 1; i < zones.size() - 1; i += 1) {
            var areaZones1 = Set.copyOf(zones.subList(0, i));
            var areaZones2 = Set.copyOf(zones.subList(i, zones.size()));
            for (int j = 0; j < occupants.size(); j += 1) {
                var areaOccupants1 = occupants.subList(0, j);
                var areaOccupants2 = occupants.subList(j, occupants.size());

                for (int openConnections = 1; openConnections < 5; openConnections += 1) {
                    var area1 = new Area<>(areaZones1, areaOccupants1, openConnections);
                    var area2 = new Area<>(areaZones2, areaOccupants2, openConnections);
                    var area12 = area1.connectTo(area2);

                    assertEquals(expectedZones, area12.zones());
                    assertEquals(expectedOccupants, area12.occupants());
                    assertEquals(2 * openConnections - 2, area12.openConnections());
                }
            }
        }
    }

    @Test
    void areaWithInitialOccupantThrowsIfAreaAlreadyOccupied() {
        var area = new Area<>(
                Set.of(new Zone.Forest(0, Zone.Forest.Kind.PLAIN)),
                List.of(PlayerColor.RED),
                1);
        assertThrows(IllegalArgumentException.class, () -> {
            area.withInitialOccupant(PlayerColor.RED);
        });
    }

    @Test
    void areaWithInitialOccupantWorksWithUnoccupiedArea() {
        var unoccupiedArea = new Area<>(
                Set.of(new Zone.Forest(0, Zone.Forest.Kind.PLAIN)),
                List.of(),
                1);
        for (var occupant : PlayerColor.values()) {
            var occupiedArea = unoccupiedArea.withInitialOccupant(occupant);
            assertEquals(List.of(occupant), occupiedArea.occupants());
        }
    }

    @Test
    void areaWithoutOccupantThrowsIfOccupantDoesNotExist() {
        var possibleOccupants = List.of(PlayerColor.values());
        for (var occupant : possibleOccupants) {
            var unoccupiedArea = new Area<>(
                    Set.of(new Zone.Forest(0, Zone.Forest.Kind.PLAIN)),
                    List.of(),
                    1);
            assertThrows(IllegalArgumentException.class, () -> {
                unoccupiedArea.withoutOccupant(occupant);
            });

            var occupiedArea = new Area<>(
                    Set.of(new Zone.Forest(0, Zone.Forest.Kind.PLAIN)),
                    List.of(occupant),
                    1);
            var wrongOccupant = possibleOccupants.get((occupant.ordinal() + 1) % possibleOccupants.size());
            assertThrows(IllegalArgumentException.class, () -> {
                occupiedArea.withoutOccupant(wrongOccupant);
            });
        }
    }

    @Test
    void areaWithoutOccupantWorks() {
        var possibleOccupants = List.of(PlayerColor.values());
        var area = new Area<>(
                Set.of(
                        new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                        new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                        new Zone.Forest(2, Zone.Forest.Kind.PLAIN)),
                possibleOccupants,
                5);
        for (int i = 0; i < possibleOccupants.size(); i += 1) {
            var occupantToRemove = possibleOccupants.get(i);
            var area1 = area.withoutOccupant(occupantToRemove);

            var expectedOccupants = new ArrayList<>(possibleOccupants);
            expectedOccupants.remove(i);

            assertEquals(area.zones(), area1.zones());
            assertEquals(area.openConnections(), area1.openConnections());
            assertEquals(expectedOccupants, area1.occupants());
        }
    }

    @Test
    void areaWithoutOccupantsWorks() {
        var possibleOccupants = List.of(PlayerColor.values());
        var area = new Area<>(
                Set.of(
                        new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                        new Zone.Forest(1, Zone.Forest.Kind.PLAIN),
                        new Zone.Forest(2, Zone.Forest.Kind.PLAIN)),
                possibleOccupants,
                5);
        var area1 = area.withoutOccupants();

        assertEquals(area.zones(), area1.zones());
        assertEquals(area.openConnections(), area1.openConnections());
        assertEquals(List.of(), area1.occupants());
    }

    @Test
    void areaTileIdsWorks() {
        var zones = Set.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(10, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(13, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(20, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(21, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(22, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(23, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(30, Zone.Forest.Kind.PLAIN));
        var area = new Area<>(zones, List.of(), 0);
        assertEquals(Set.of(0, 1, 2, 3), area.tileIds());
    }

    @Test
    void areaZoneWithSpecialPowerWorksWithInexistantSpecialPower() {
        var zones = Set.of(
                new Zone.Forest(0, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(2, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(3, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(10, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(13, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(20, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(21, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(22, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(23, Zone.Forest.Kind.PLAIN),
                new Zone.Forest(30, Zone.Forest.Kind.PLAIN));
        var area = new Area<>(zones, List.of(), 0);
        for (var specialPower : Zone.SpecialPower.values())
            assertNull(area.zoneWithSpecialPower(specialPower));
    }

    @Test
    void areaZoneWithSpecialPowerFindsCorrectZone() {
        var zones = List.of(
                new Zone.Meadow(0, List.of(), null),
                new Zone.Meadow(1, List.of(), Zone.SpecialPower.SHAMAN),
                new Zone.Meadow(2, List.of(), null),
                new Zone.Meadow(3, List.of(), Zone.SpecialPower.PIT_TRAP),
                new Zone.Meadow(4, List.of(), null),
                new Zone.Meadow(5, List.of(), Zone.SpecialPower.WILD_FIRE));

        var area = new Area<>(Set.copyOf(zones), List.of(), 0);
        assertEquals(zones.get(1), area.zoneWithSpecialPower(Zone.SpecialPower.SHAMAN));
        assertEquals(zones.get(3), area.zoneWithSpecialPower(Zone.SpecialPower.PIT_TRAP));
        assertEquals(zones.get(5), area.zoneWithSpecialPower(Zone.SpecialPower.WILD_FIRE));
    }
}