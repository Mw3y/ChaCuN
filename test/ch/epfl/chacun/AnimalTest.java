package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    @Test
    void animalKindIsDefinedCorrectly() {
        assertEquals(0, Animal.Kind.MAMMOTH.ordinal());
        assertEquals(1, Animal.Kind.AUROCHS.ordinal());
        assertEquals(2, Animal.Kind.DEER.ordinal());
        assertEquals(3, Animal.Kind.TIGER.ordinal());
    }

    @Test
    void animalTileIdWorks() {
        var allKinds = List.of(Animal.Kind.values());
        var kindIndex = 0;
        for (int tileId = 0; tileId < 10; tileId += 1) {
            for (int zoneLocalId = 0; zoneLocalId < 10; zoneLocalId += 1) {
                var zoneId = tileId * 10 + zoneLocalId;
                for (int animalLocalId = 0; animalLocalId < 2; animalLocalId += 1) {
                    var animalId = zoneId * 10 + animalLocalId;
                    var animal = new Animal(animalId, allKinds.get(kindIndex));
                    kindIndex = (kindIndex + 1) % allKinds.size();
                    assertEquals(tileId, animal.tileId());
                }
            }
        }
    }
}