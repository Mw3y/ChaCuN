package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextMakerFrTest {

    public static Map<PlayerColor, String> players = Map.of(
            PlayerColor.RED, "Dalia",
            PlayerColor.BLUE, "Claude",
            PlayerColor.GREEN, "Bachir",
            PlayerColor.YELLOW, "Alice"
    );
    TextMaker textMaker = new TextMakerFr(players);

    @Test
    void pointsWorks() {
        assertEquals(textMaker.points(0), "0 point");
        assertEquals(textMaker.points(1), "1 point");
        assertEquals(textMaker.points(16), "16 points");
    }

    @Test
    void playerClosedForestWithMenhirWorks() {
        String expected = "Dalia a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
        assertEquals(expected, textMaker.playerClosedForestWithMenhir(PlayerColor.RED));
    }

    @Test
    void playersScoredForestWorks() {
        String singularExpected = "Claude a remporté 6 points en tant qu'occupant·e majoritaire d'une forêt composée de 3 tuiles.";
        assertEquals(singularExpected, textMaker.playersScoredForest(Set.of(PlayerColor.BLUE), 6, 0, 3));

        String pluralExpected = "Dalia et Alice ont remporté 9 points en tant qu'occupant·e·s majoritaires d'une forêt composée de 3 tuiles et de 1 groupe de champignons.";
        assertEquals(pluralExpected, textMaker.playersScoredForest(Set.of(PlayerColor.YELLOW, PlayerColor.RED), 9, 1, 3));
    }

    @Test
    void playersScoredRiverWorks() {
        String expectedSingular = "Alice a remporté 8 points en tant qu'occupant·e majoritaire d'une rivière composée de 3 tuiles et contenant 5 poissons.";
        assertEquals(expectedSingular, textMaker.playersScoredRiver(Set.of(PlayerColor.YELLOW), 8, 5, 3));

        String expectedPlural = "Claude et Bachir ont remporté 3 points en tant qu'occupant·e·s majoritaires d'une rivière composée de 3 tuiles.";
        assertEquals(expectedPlural, textMaker.playersScoredRiver(Set.of(PlayerColor.BLUE, PlayerColor.GREEN), 3, 0, 3));
    }

    @Test
    void playerScoredHuntingTrapWorks() {
        String expected = "Bachir a remporté 10 points en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de 1 mammouth, 2 aurochs et 3 cerfs.";
        assertEquals(expected, textMaker.playerScoredHuntingTrap(PlayerColor.GREEN, 10, Map.of(Animal.Kind.DEER, 3, Animal.Kind.MAMMOTH, 1, Animal.Kind.AUROCHS, 2, Animal.Kind.TIGER, 0)));
    }

    @Test
    void playerScoredLogboatWorks() {
        String expected = "Alice a remporté 8 points en plaçant la pirogue dans un réseau hydrographique contenant 4 lacs.";
        assertEquals(expected, textMaker.playerScoredLogboat(PlayerColor.YELLOW, 8, 4));
    }

    @Test
    void playersScoredMeadowWorks() {
        String expectedSingular = "Dalia a remporté 1 point en tant qu'occupant·e majoritaire d'un pré contenant 1 cerf.";
        assertEquals(expectedSingular, textMaker.playersScoredMeadow(Set.of(PlayerColor.RED), 1, Map.of(Animal.Kind.DEER, 1)));

        String expectedPlural = "Claude et Bachir ont remporté 5 points en tant qu'occupant·e·s majoritaires d'un pré contenant 1 mammouth et 2 cerfs.";
        assertEquals(expectedPlural, textMaker.playersScoredMeadow(Set.of(PlayerColor.GREEN, PlayerColor.BLUE), 5, Map.of(Animal.Kind.DEER, 2, Animal.Kind.MAMMOTH, 1)));
    }

    @Test
    void playersScoredRiverSystem() {
        String singularExpected = "Alice a remporté 9 points en tant qu'occupant·e majoritaire d'un réseau hydrographique contenant 9 poissons.";
        assertEquals(singularExpected, textMaker.playersScoredRiverSystem(Set.of(PlayerColor.YELLOW), 9, 9));

        String pluralExpected = "Dalia, Claude et Bachir ont remporté 1 point en tant qu'occupant·e·s majoritaires d'un réseau hydrographique contenant 1 poisson.";
        assertEquals(pluralExpected, textMaker.playersScoredRiverSystem(Set.of(PlayerColor.BLUE, PlayerColor.RED, PlayerColor.GREEN), 1, 1));
    }

    @Test
    void playersScoredPitTrapWorks() {
        String expectedSingular = "Dalia a remporté 2 points en tant qu'occupant·e majoritaire d'un pré contenant la grande fosse à pieux entourée de 1 auroch.";
        assertEquals(expectedSingular, textMaker.playersScoredPitTrap(Set.of(PlayerColor.RED), 2, Map.of(Animal.Kind.AUROCHS, 1)));

        String expectedPlural = "Bachir et Alice ont remporté 12 points en tant qu'occupant·e·s majoritaires d'un pré contenant la grande fosse à pieux entourée de 2 mammouths, 2 aurochs et 2 cerfs.";
        assertEquals(expectedPlural, textMaker.playersScoredPitTrap(Set.of(PlayerColor.YELLOW, PlayerColor.GREEN), 12, Map.of(Animal.Kind.DEER, 2, Animal.Kind.MAMMOTH, 2, Animal.Kind.AUROCHS, 2)));
    }

    @Test
    void playersScoredRaftWorks() {
        String expectedSingular = "Alice a remporté 1 point en tant qu'occupant·e majoritaire d'un réseau hydrographique contenant le radeau et 1 lac.";
        assertEquals(expectedSingular, textMaker.playersScoredRaft(Set.of(PlayerColor.YELLOW), 1, 1));

        String expectedPlural = "Dalia et Claude ont remporté 10 points en tant qu'occupant·e·s majoritaires d'un réseau hydrographique contenant le radeau et 10 lacs.";
        assertEquals(expectedPlural, textMaker.playersScoredRaft(Set.of(PlayerColor.BLUE, PlayerColor.RED), 10, 10));
    }

    @Test
    void playersWonWorks() {
        String expectedSingular = "Bachir a remporté la partie avec 111 points !";
        assertEquals(expectedSingular, textMaker.playersWon(Set.of(PlayerColor.GREEN), 111));

        String expectedPlural = "Dalia et Alice ont remporté la partie avec 123 points !";
        assertEquals(expectedPlural, textMaker.playersWon(Set.of(PlayerColor.RED, PlayerColor.YELLOW), 123));
    }
}
