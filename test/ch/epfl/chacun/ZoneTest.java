package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoneTest {
    @Test
    void zoneStaticTileIdWorks() {
        for (int tileId = 0; tileId < 95; tileId += 1) {
            for (int localZoneId = 0; localZoneId < 9; localZoneId += 1) {
                var zoneId = tileId * 10 + localZoneId;
                assertEquals(tileId, Zone.tileId(zoneId));
            }
        }
    }

    @Test
    void zoneStaticLocalIdWorks() {
        for (int tileId = 0; tileId < 95; tileId += 1) {
            for (int localZoneId = 0; localZoneId < 9; localZoneId += 1) {
                var zoneId = tileId * 10 + localZoneId;
                assertEquals(localZoneId, Zone.localId(zoneId));
            }
        }
    }

    @Test
    void zoneTileIdWorks() {
        for (int tileId = 0; tileId < 95; tileId += 1) {
            for (int localZoneId = 0; localZoneId < 9; localZoneId += 1) {
                var zoneId = tileId * 10 + localZoneId;
                var zone = new Zone.Forest(zoneId, Zone.Forest.Kind.PLAIN);
                assertEquals(tileId, zone.tileId());
            }
        }
    }

    @Test
    void zoneLocalIdWorks() {
        for (int tileId = 0; tileId < 95; tileId += 1) {
            for (int localZoneId = 0; localZoneId < 9; localZoneId += 1) {
                var zoneId = tileId * 10 + localZoneId;
                var zone = new Zone.River(zoneId, 0, null);
                assertEquals(localZoneId, zone.localId());
            }
        }
    }

    @Test
    void zoneMeadowCopiesAnimalList() {
        var immutableAnimals = List.of(new Animal(0, Animal.Kind.DEER));
        var mutableAnimals = new ArrayList<>(immutableAnimals);
        var meadow = new Zone.Meadow(0, mutableAnimals, null);
        mutableAnimals.clear();
        assertEquals(immutableAnimals, meadow.animals());
    }
}