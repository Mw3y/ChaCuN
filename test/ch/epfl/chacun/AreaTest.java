package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.epfl.chacun.Zone.*;
import static org.junit.jupiter.api.Assertions.*;

public class AreaTest {
    @Test
    void areaThrowsOnNegativeNumberOfOccupants() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.add(0, PlayerColor.RED);

        assertThrows(IllegalArgumentException.class,
                () -> new Area<>(forestZones, occupants, -2));
    }

    @Test
    void occupantsSortingWorks() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.add(PlayerColor.RED);
        occupants.add(PlayerColor.PURPLE);
        occupants.add(PlayerColor.GREEN);

        List<PlayerColor> expectedSortedOccupants = new ArrayList<>();
        expectedSortedOccupants.add(PlayerColor.RED);
        expectedSortedOccupants.add(PlayerColor.GREEN);
        expectedSortedOccupants.add(PlayerColor.PURPLE);

        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertEquals(expectedSortedOccupants, forestArea.occupants());
    }

    @Test
    void hasMenhirWorksWithMenhir() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.WITH_MENHIR));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        assertTrue(Area.hasMenhir(forestArea));
    }

    @Test
    void hasMenhirWorksWithOutMenhir() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        assertFalse(Area.hasMenhir(forestArea));
    }

    @Test
    void mushroomGroupCountWorksWhenMushrooms() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.WITH_MUSHROOMS));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertEquals(1, Area.mushroomGroupCount(forestArea));
    }

    @Test
    void mushroomGroupCountWorksWhenNoMushrooms() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertEquals(0, Area.mushroomGroupCount(forestArea));
    }

    @Test
    void animalsWorksWithNoCancelledAnimals() {
        Set<Meadow> meadowZones = new HashSet<>();
        List<Animal> animals = new ArrayList<>(List.of(new Animal(0, Animal.Kind.AUROCHS)));
        meadowZones.add(new Meadow(0, animals, null));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Meadow> medowArea = new Area<>(meadowZones, occupants, 3);
        Set<Animal> expectedAnimals = new HashSet<>(List.of(new Animal(0, Animal.Kind.AUROCHS)));
        Set<Animal> cancelledAnimals = new HashSet<>();
        assertEquals(expectedAnimals, Area.animals(medowArea, cancelledAnimals));
    }

    @Test
    void animalsWorksWithCancelledAnimals() {
        Set<Meadow> meadowZones = new HashSet<>();
        List<Animal> animals = new ArrayList<>(List.of(new Animal(0, Animal.Kind.AUROCHS)));
        meadowZones.add(new Meadow(0, animals, null));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Meadow> medowArea = new Area<>(meadowZones, occupants, 3);
        Set<Animal> expectedAnimals = new HashSet<>(List.of(new Animal(0, Animal.Kind.AUROCHS)));
        Set<Animal> cancelledAnimals = new HashSet<>(List.of(new Animal(1, Animal.Kind.DEER)));
        assertEquals(expectedAnimals, Area.animals(medowArea, cancelledAnimals));
    }

    @Test
    void riverFishCountWorksWithTwoDistinctLakes() {
        Set<River> riverZones = new HashSet<>();
        Lake lake1 = new Lake(0, 2, null);
        Lake lake2 = new Lake(1, 2, null);
        riverZones.add(new River(2, 1, lake1));
        riverZones.add(new River(3, 1, lake2));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<River> riverArea = new Area<>(riverZones, occupants, 0);

        assertEquals(6, Area.riverFishCount(riverArea));
    }

    @Test
    void riverFishCountWorksWithSingleLake() {
        Set<River> riverZones = new HashSet<>();
        Lake lake1 = new Lake(0, 2, null);
        River river1 = new River(1, 1, lake1);
        River river2 = new River(2, 1, null);
        River river3 = new River(3, 1, lake1);
        riverZones.add(river1);
        riverZones.add(river2);
        riverZones.add(river3);
        List<PlayerColor> occupants = new ArrayList<>();
        Area<River> riverArea = new Area<>(riverZones, occupants, 0);

        assertEquals(5, Area.riverFishCount(riverArea));
    }

    @Test
    void riverSystemFishCountWorks() {
        Set<Water> riverSystemZones = new HashSet<>();
        Lake lake1 = new Lake(0, 2, null);
        River river1 = new River(1, 1, lake1);
        River river2 = new River(2, 1, null);
        River river3 = new River(3, 1, lake1);
        riverSystemZones.add(lake1);
        riverSystemZones.add(river1);
        riverSystemZones.add(river2);
        riverSystemZones.add(river3);
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Water> riverSystem = new Area<>(riverSystemZones, occupants, 0);

        assertEquals(5, Area.riverSystemFishCount(riverSystem));
    }

    @Test
    void lakeCountWorks() {
        Set<Water> riverSystemZones = new HashSet<>();
        Lake lake1 = new Lake(0, 2, null);
        Lake lake2 = new Lake(1, 2, null);
        River river1 = new River(2, 1, lake1);
        River river2 = new River(3, 1, null);
        riverSystemZones.add(lake1);
        riverSystemZones.add(lake2);
        riverSystemZones.add(river1);
        riverSystemZones.add(river2);
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Water> riverSystem = new Area<>(riverSystemZones, occupants, 0);

        assertEquals(2, Area.lakeCount(riverSystem));
    }

    @Test
    void isClosedWorksOnClosedArea() {
        Set<River> riverZones = new HashSet<>();
        Lake lake1 = new Lake(0, 2, null);
        Lake lake2 = new Lake(1, 2, null);
        riverZones.add(new River(2, 1, lake1));
        riverZones.add(new River(3, 1, lake2));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<River> riverArea = new Area<>(riverZones, occupants, 0);

        assertTrue(riverArea.isClosed());
    }

    @Test
    void isClosedWorksOnOpenArea() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertFalse(forestArea.isClosed());
    }

    @Test
    void isOccupiedWorksOnOccupiedArea() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.RED));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertTrue(forestArea.isOccupied());
    }

    @Test
    void isOccupiedWorksOnUnoccupiedArea() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertFalse(forestArea.isOccupied());
    }

    @Test
    void majorityOccupantsWorks() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.add(PlayerColor.BLUE);
        occupants.add(PlayerColor.BLUE);
        occupants.add(PlayerColor.YELLOW);
        occupants.add(PlayerColor.YELLOW);
        occupants.add(PlayerColor.PURPLE);
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertEquals(Set.of(PlayerColor.BLUE, PlayerColor.YELLOW), forestArea.majorityOccupants());
    }

    @Test
    void majorityOccupantsWorksWithoutOccupants() {
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea = new Area<>(new HashSet<>(), occupants, 3);

        assertEquals(Set.of(), forestArea.majorityOccupants());
    }

    @Test
    void connectToWorksWithOtherArea() {
        Set<Forest> forestZones1 = new HashSet<>();
        Set<Forest> forestZones2 = new HashSet<>();
        Set<Forest> forestZones3 = new HashSet<>();
        forestZones1.add(new Forest(0, Forest.Kind.PLAIN));
        forestZones2.add(new Forest(1, Forest.Kind.PLAIN));
        forestZones3.add(new Forest(0, Forest.Kind.PLAIN));
        forestZones3.add(new Forest(1, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea1 = new Area<>(forestZones1, occupants, 4);
        Area<Forest> forestArea2 = new Area<>(forestZones2, occupants, 4);
        Area<Forest> forestArea3 = new Area<>(forestZones3, occupants, 6);

        assertEquals(forestArea3, forestArea1.connectTo(forestArea2));

    }

    @Test
    void connectToWorksWithItself() {
        Set<Forest> forestZones1 = new HashSet<>();
        Set<Forest> forestZones2 = new HashSet<>();
        forestZones1.add(new Forest(0, Forest.Kind.PLAIN));
        forestZones1.add(new Forest(1, Forest.Kind.PLAIN));
        forestZones1.add(new Forest(2, Forest.Kind.PLAIN));
        forestZones1.add(new Forest(3, Forest.Kind.PLAIN));
        forestZones2.add(new Forest(0, Forest.Kind.PLAIN));
        forestZones2.add(new Forest(1, Forest.Kind.PLAIN));
        forestZones2.add(new Forest(2, Forest.Kind.PLAIN));
        forestZones2.add(new Forest(3, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>();
        Area<Forest> forestArea1 = new Area<>(forestZones1, occupants, 10);
        Area<Forest> forestArea2 = new Area<>(forestZones2, occupants, 8);


        assertEquals(forestArea2, forestArea1.connectTo(forestArea1));

    }

    @Test
    void withInitialOccupantThrowsOnOccupiedArea() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.RED));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);

        assertThrows(IllegalArgumentException.class, () -> forestArea.withInitialOccupant(PlayerColor.BLUE));

    }

    @Test
    void withInitialOccupantWorks() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants1 = new ArrayList<>();
        List<PlayerColor> occupants2 = new ArrayList<>(List.of(PlayerColor.YELLOW));
        Area<Forest> forestArea1 = new Area<>(forestZones, occupants1, 3);
        Area<Forest> forestArea2 = new Area<>(forestZones, occupants2, 3);

        assertEquals(forestArea2, forestArea1.withInitialOccupant(PlayerColor.YELLOW));
    }

    @Test
    void withoutOccupantThrowsWhenNoOccupantOfGivenColor() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants = new ArrayList<>(List.of(PlayerColor.RED));
        Area<Forest> forestArea = new Area<>(forestZones, occupants, 3);
        assertThrows(IllegalArgumentException.class, () -> forestArea.withoutOccupant(PlayerColor.YELLOW));
    }

    @Test
    void withoutOccupantWorks() {
        Set<Forest> forestZones = new HashSet<>();
        forestZones.add(new Forest(0, Forest.Kind.PLAIN));
        List<PlayerColor> occupants1 = new ArrayList<>(List.of(PlayerColor.YELLOW));
        List<PlayerColor> occupants2 = new ArrayList<>();
        Area<Forest> forestArea1 = new Area<>(forestZones, occupants1, 3);
        Area<Forest> forestArea2 = new Area<>(forestZones, occupants2, 3);

        assertEquals(forestArea2, forestArea1.withoutOccupant(PlayerColor.YELLOW));
    }

}
