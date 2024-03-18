package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.chacun.PlayerColor.*;
import static ch.epfl.chacun.Zone.Forest.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageBoardTest {
    @Test
    void messageBoardMessageConstructorThrowsIfTextIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new MessageBoard.Message(null, 0, Set.of(), Set.of());
        });
    }

    @Test
    void messageBoardMessageConstructorThrowsIfPointsAreNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MessageBoard.Message("", -1, Set.of(), Set.of());
        });
    }

    @Test
    void messageBoardMessageIsImmutable() {
        var immutableScorers = Set.of(RED, BLUE);
        var immutableTileIds = Set.of(1, 2, 3);

        var mutableScorers = new HashSet<>(immutableScorers);
        var mutableTileIds = new HashSet<>(immutableTileIds);

        var m = new MessageBoard.Message("", 0, mutableScorers, mutableTileIds);
        mutableScorers.clear();
        mutableTileIds.clear();

        assertEquals(immutableScorers, m.scorers());
        assertEquals(immutableTileIds, m.tileIds());

        try {
            m.scorers().clear();
        } catch (UnsupportedOperationException e) {
            /* nothing to do if scorers are not modifiable */
        }
        try {
            m.tileIds().clear();
        } catch (UnsupportedOperationException e) {
            /* nothing to do if tileIds are not modifiable */
        }
        assertEquals(immutableScorers, m.scorers());
        assertEquals(immutableTileIds, m.tileIds());
    }

    @Test
    void messageBoardIsImmutable() {
        var m = new MessageBoard.Message("", 0, Set.of(), Set.of());
        var immutableMessages = List.of(m, m, m);
        var mutableMessages = new ArrayList<>(immutableMessages);
        var mb = new MessageBoard(new BasicTextMaker(), mutableMessages);
        mutableMessages.clear();
        assertEquals(immutableMessages, mb.messages());

        try {
            mb.messages().clear();
        } catch (UnsupportedOperationException e) {
            /* nothing to do if messages are not modifiable */
        }
        assertEquals(immutableMessages, mb.messages());
    }

    private static MessageBoard.Message emptyMessage(int points, PlayerColor... players) {
        return new MessageBoard.Message("", points, Set.of(players), Set.of());
    }

    @Test
    void messageBoardPointsWorks() {
        var messages = List.of(
                emptyMessage(1 << 0, RED),
                emptyMessage(1 << 1, RED, BLUE, PURPLE),
                emptyMessage(1 << 2, RED, GREEN),
                emptyMessage(1 << 3, RED, GREEN),
                emptyMessage(1 << 4, RED, PURPLE));
        var messageBoard = new MessageBoard(new BasicTextMaker(), messages);

        var points = messageBoard.points();
        assertEquals(31, points.getOrDefault(RED, 0));
        assertEquals(2, points.getOrDefault(BLUE, 0));
        assertEquals(12, points.getOrDefault(GREEN, 0));
        assertEquals(18, points.getOrDefault(PURPLE, 0));
        assertEquals(0, points.getOrDefault(YELLOW, 0));
    }

    @Test
    void messageBoardWithScoredForestWorksWithUnoccupiedForest() {
        var f1 = new Zone.Forest(10, Kind.PLAIN);
        var f2 = new Zone.Forest(20, Kind.PLAIN);
        var forestArea = new Area<>(Set.of(f1, f2), List.of(), 0);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredForest(forestArea);
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredForestWorksWithOccupiedForest() {
        var f1 = new Zone.Forest(10, Kind.PLAIN);
        var f2 = new Zone.Forest(20, Kind.WITH_MUSHROOMS);
        var forestArea = new Area<>(Set.of(f1, f2), List.of(RED), 0);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredForest(forestArea);
        var expectedMessage = new MessageBoard.Message("{RED}|7|1|2", 7, Set.of(RED), Set.of(1, 2));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithClosedForestWithMenhirWorks() {
        var f1 = new Zone.Forest(10, Kind.PLAIN);
        var f2 = new Zone.Forest(20, Kind.WITH_MENHIR);
        var forestArea = new Area<>(Set.of(f1, f2), List.of(), 0);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withClosedForestWithMenhir(YELLOW, forestArea);
        var expectedMessage = new MessageBoard.Message("YELLOW", 0, Set.of(), Set.of(1, 2));
        assertEquals(1, mb.messages().size());
        assertEquals(expectedMessage.text(), mb.messages().getFirst().text());
        assertEquals(expectedMessage.points(), mb.messages().getFirst().points());
        assertEquals(expectedMessage.scorers(), mb.messages().getFirst().scorers());
        // We accept both, as the stage description was not clear about that.
        assertTrue(mb.messages().getFirst().tileIds().isEmpty()
                   || mb.messages().getFirst().tileIds().equals(expectedMessage.tileIds()));
    }

    @Test
    void messageBoardWithScoredRiverWorksWithUnoccupiedRiver() {
        var r1 = new Zone.River(10, 1, null);
        var r2 = new Zone.River(20, 2, null);
        var riverArea = new Area<>(Set.of(r1, r2), List.of(), 0);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredRiver(riverArea);
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredRiverWorksWithOccupiedRiver() {
        var r1 = new Zone.River(10, 1, null);
        var r2 = new Zone.River(20, 4, null);
        var riverArea = new Area<>(Set.of(r1, r2), List.of(BLUE), 0);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredRiver(riverArea);
        var expectedMessage = new MessageBoard.Message("{BLUE}|7|5|2", 7, Set.of(BLUE), Set.of(1, 2));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithScoredHuntingTrapWorksWithUnworthyAnimals() {
        var m1 = new Zone.Meadow(10, List.of(new Animal(10_1, Animal.Kind.TIGER)), null);
        var m2 = new Zone.Meadow(20, List.of(new Animal(20_1, Animal.Kind.TIGER)), null);
        var m3 = new Zone.Meadow(30, List.of(), SpecialPower.HUNTING_TRAP);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED), 0);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredHuntingTrap(BLUE, meadowArea);
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredHuntingTrapWorksWithWorthyAnimals() {
        var m1 = new Zone.Meadow(10, List.of(new Animal(10_1, Animal.Kind.MAMMOTH)), null);
        var m2 = new Zone.Meadow(20, List.of(new Animal(20_1, Animal.Kind.AUROCHS)), null);
        var m3 = new Zone.Meadow(30, List.of(), SpecialPower.HUNTING_TRAP);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED, GREEN), 0);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredHuntingTrap(BLUE, meadowArea);
        var expectedMessage = new MessageBoard.Message("BLUE|5|1×MAMMOTH/1×AUROCHS/0×DEER/0×TIGER", 5, Set.of(BLUE), Set.of(1, 2, 3));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithScoredLogboatWorks() {
        var l1 = new Zone.Lake(18, 1, null);
        var r1 = new Zone.River(10, 4, l1);

        var l2 = new Zone.Lake(28, 2, null);
        var r2 = new Zone.River(20, 5, l2);
        var r3 = new Zone.River(21, 5, l2);

        var l3 = new Zone.Lake(38, 3, null);
        var r4 = new Zone.River(30, 5, l3);

        var riverSystem = new Area<Water>(Set.of(l1, r1, l2, r2, r3, l3, r4), List.of(RED), 0);

        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredLogboat(BLUE, riverSystem);
        var expectedMessage = new MessageBoard.Message("BLUE|6|3", 6, Set.of(BLUE), Set.of(1, 2, 3));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithScoredMeadowWorksWithUnoccupiedMeadow() {
        var m1 = new Zone.Meadow(10, List.of(new Animal(10_1, Animal.Kind.MAMMOTH)), null);
        var m2 = new Zone.Meadow(20, List.of(new Animal(20_1, Animal.Kind.AUROCHS)), null);
        var m3 = new Zone.Meadow(30, List.of(), SpecialPower.HUNTING_TRAP);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredMeadow(meadowArea, Set.of());
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredMeadowWorksWithUnworthyAnimals() {
        var m1 = new Zone.Meadow(10, List.of(new Animal(10_1, Animal.Kind.TIGER)), null);
        var m2 = new Zone.Meadow(20, List.of(new Animal(20_1, Animal.Kind.TIGER)), null);
        var m3 = new Zone.Meadow(30, List.of(), null);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED, BLUE), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredMeadow(meadowArea, Set.of());
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredMeadowWorksWithWorthyButCancelledAnimals() {
        var a1 = new Animal(10_1, Animal.Kind.MAMMOTH);
        var a2 = new Animal(20_1, Animal.Kind.AUROCHS);
        var a3 = new Animal(20_2, Animal.Kind.TIGER);
        var m1 = new Zone.Meadow(10, List.of(a1), null);
        var m2 = new Zone.Meadow(20, List.of(a2, a3), null);
        var m3 = new Zone.Meadow(30, List.of(), null);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED, BLUE), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredMeadow(meadowArea, Set.of(a1, a2));
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredMeadowWorksWithWorthyAnimals() {
        var a1 = new Animal(10_1, Animal.Kind.MAMMOTH);
        var a2 = new Animal(20_1, Animal.Kind.AUROCHS);
        var a3 = new Animal(20_2, Animal.Kind.TIGER);
        var m1 = new Zone.Meadow(10, List.of(a1), null);
        var m2 = new Zone.Meadow(20, List.of(a2, a3), null);
        var m3 = new Zone.Meadow(30, List.of(), null);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED, BLUE), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredMeadow(meadowArea, Set.of());
        var expectedMessage = new MessageBoard.Message("{RED,BLUE}|5|1×MAMMOTH/1×AUROCHS/0×DEER/1×TIGER", 5, Set.of(RED, BLUE), Set.of(1, 2, 3));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithScoredRiverSystemWorksWithUnoccupiedRiverSystem() {
        var l1 = new Zone.Lake(18, 1, null);
        var r1 = new Zone.River(10, 4, l1);

        var l2 = new Zone.Lake(28, 2, null);
        var r2 = new Zone.River(20, 5, l2);
        var r3 = new Zone.River(21, 5, l2);

        var l3 = new Zone.Lake(38, 3, null);
        var r4 = new Zone.River(30, 5, l3);

        var riverSystem = new Area<Water>(Set.of(l1, r1, l2, r2, r3, l3, r4), List.of(), 0);

        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredRiverSystem(riverSystem);
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredRiverSystemWorksWithFishlessRiverSystem() {
        var l1 = new Zone.Lake(18, 0, null);
        var r1 = new Zone.River(10, 0, l1);

        var l2 = new Zone.Lake(28, 0, null);
        var r2 = new Zone.River(20, 0, l2);
        var r3 = new Zone.River(21, 0, l2);

        var l3 = new Zone.Lake(38, 0, null);
        var r4 = new Zone.River(30, 0, l3);

        var riverSystem = new Area<Water>(Set.of(l1, r1, l2, r2, r3, l3, r4), List.of(RED), 0);

        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredRiverSystem(riverSystem);
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredRiverSystemWorks() {
        var l1 = new Zone.Lake(18, 3, null);
        var r1 = new Zone.River(10, 1, l1);

        var l2 = new Zone.Lake(28, 2, null);
        var r2 = new Zone.River(20, 1, l2);
        var r3 = new Zone.River(21, 1, l2);

        var l3 = new Zone.Lake(38, 3, null);
        var r4 = new Zone.River(30, 1, l3);

        var riverSystem = new Area<Water>(Set.of(l1, r1, l2, r2, r3, l3, r4), List.of(RED, BLUE), 0);

        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredRiverSystem(riverSystem);
        var expectedMessage = new MessageBoard.Message("{RED,BLUE}|12|12", 12, Set.of(RED, BLUE), Set.of(1, 2, 3));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithScoredPitTrapWorksWithUnoccupiedMeadow() {
        var m1 = new Zone.Meadow(10, List.of(new Animal(10_1, Animal.Kind.MAMMOTH)), null);
        var m2 = new Zone.Meadow(20, List.of(new Animal(20_1, Animal.Kind.AUROCHS)), null);
        var m3 = new Zone.Meadow(30, List.of(), SpecialPower.HUNTING_TRAP);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredPitTrap(meadowArea, Set.of());
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredPitTrapWorksWithUnworthyAnimals() {
        var m1 = new Zone.Meadow(10, List.of(new Animal(10_1, Animal.Kind.TIGER)), null);
        var m2 = new Zone.Meadow(20, List.of(new Animal(20_1, Animal.Kind.TIGER)), null);
        var m3 = new Zone.Meadow(30, List.of(), null);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED, BLUE), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredPitTrap(meadowArea, Set.of());
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredPitTrapWorksWithWorthyButCancelledAnimals() {
        var a1 = new Animal(10_1, Animal.Kind.MAMMOTH);
        var a2 = new Animal(20_1, Animal.Kind.AUROCHS);
        var a3 = new Animal(20_2, Animal.Kind.TIGER);
        var m1 = new Zone.Meadow(10, List.of(a1), null);
        var m2 = new Zone.Meadow(20, List.of(a2, a3), null);
        var m3 = new Zone.Meadow(30, List.of(), null);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED, BLUE), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredPitTrap(meadowArea, Set.of(a1, a2));
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredPitTrapWorksWithWorthyAnimals() {
        var a1 = new Animal(10_1, Animal.Kind.MAMMOTH);
        var a2 = new Animal(20_1, Animal.Kind.AUROCHS);
        var a3 = new Animal(20_2, Animal.Kind.TIGER);
        var m1 = new Zone.Meadow(10, List.of(a1), null);
        var m2 = new Zone.Meadow(20, List.of(a2, a3), null);
        var m3 = new Zone.Meadow(30, List.of(), null);
        var meadowArea = new Area<>(Set.of(m1, m2, m3), List.of(RED, BLUE), 3);
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredPitTrap(meadowArea, Set.of());
        var expectedMessage = new MessageBoard.Message("{RED,BLUE}|5|1×MAMMOTH/1×AUROCHS/0×DEER/1×TIGER", 5, Set.of(RED, BLUE), Set.of(1, 2, 3));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithScoredRaftWorksWithUnoccupiedRiverSystem() {
        var l1 = new Zone.Lake(18, 1, null);
        var r1 = new Zone.River(10, 4, l1);

        var l2 = new Zone.Lake(28, 2, null);
        var r2 = new Zone.River(20, 5, l2);
        var r3 = new Zone.River(21, 5, l2);

        var l3 = new Zone.Lake(38, 3, null);
        var r4 = new Zone.River(30, 5, l3);

        var riverSystem = new Area<Water>(Set.of(l1, r1, l2, r2, r3, l3, r4), List.of(), 0);

        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredRaft(riverSystem);
        assertEquals(List.of(), mb.messages());
    }

    @Test
    void messageBoardWithScoredRaftWorksWithOccupiedRiverSystem() {
        var l1 = new Zone.Lake(18, 1, null);
        var r1 = new Zone.River(10, 4, l1);

        var l2 = new Zone.Lake(28, 2, null);
        var r2 = new Zone.River(20, 5, l2);
        var r3 = new Zone.River(21, 5, l2);

        var l3 = new Zone.Lake(38, 3, null);
        var r4 = new Zone.River(30, 5, l3);

        var riverSystem = new Area<Water>(Set.of(l1, r1, l2, r2, r3, l3, r4), List.of(RED, YELLOW), 0);

        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withScoredRaft(riverSystem);
        var expectedMessage = new MessageBoard.Message("{RED,YELLOW}|3|3", 3, Set.of(RED, YELLOW), Set.of(1, 2, 3));
        assertEquals(List.of(expectedMessage), mb.messages());
    }

    @Test
    void messageBoardWithWinnersWorks() {
        var mb = new MessageBoard(new BasicTextMaker(), List.of());
        mb = mb.withWinners(Set.of(PURPLE, YELLOW), 27);
        var expectedMessage = new MessageBoard.Message("{YELLOW,PURPLE}|27", 0, Set.of(), Set.of());
        assertEquals(1, mb.messages().size());
        var actualMessage = mb.messages().getFirst();
        assertEquals(expectedMessage.text(), actualMessage.text());
        assertEquals(expectedMessage.points(), actualMessage.points());
        // Ignore scorers, as the specification was somewhat ambiguous, but it should be empty
        // assertEquals(expectedMessage.scorers(), actualMessage.scorers());
        assertEquals(expectedMessage.tileIds(), actualMessage.tileIds());
    }
}

class BasicTextMaker implements TextMaker {
    private static String scorers(Set<PlayerColor> scorers) {
        return scorers.stream()
                .sorted()
                .map(Object::toString)
                .collect(Collectors.joining(",", "{", "}"));
    }

    private static String animals(Map<Animal.Kind, Integer> animals) {
        return Arrays.stream(Animal.Kind.values())
                .map(k -> animals.getOrDefault(k, 0) + "×" + k)
                .collect(Collectors.joining("/"));
    }

    @Override
    public String playerName(PlayerColor playerColor) {
        return playerColor.name();
    }

    @Override
    public String points(int points) {
        return String.valueOf(points);
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return playerName(player);
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers,
                                      int points,
                                      int mushroomGroupCount,
                                      int tileCount) {
        return String.join("|",
                scorers(scorers),
                points(points),
                String.valueOf(mushroomGroupCount),
                String.valueOf(tileCount));
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers,
                                     int points,
                                     int fishCount,
                                     int tileCount) {
        return String.join("|",
                scorers(scorers),
                points(points),
                String.valueOf(fishCount),
                String.valueOf(tileCount));
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer,
                                          int points,
                                          Map<Animal.Kind, Integer> animals) {
        return String.join("|",
                playerName(scorer),
                String.valueOf(points),
                animals(animals));
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return String.join("|",
                playerName(scorer),
                points(points),
                String.valueOf(lakeCount));
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers,
                                      int points,
                                      Map<Animal.Kind, Integer> animals) {
        return String.join(
                "|",
                scorers(scorers),
                points(points),
                animals(animals));
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return String.join(
                "|",
                scorers(scorers),
                points(points),
                String.valueOf(fishCount));
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers,
                                       int points,
                                       Map<Animal.Kind, Integer> animals) {
        return String.join("|",
                scorers(scorers),
                String.valueOf(points),
                animals(animals));
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return String.join("|",
                scorers(scorers),
                String.valueOf(points),
                String.valueOf(lakeCount));
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return String.join("|",
                scorers(winners),
                points(points));
    }

    @Override
    public String clickToOccupy() {
        return "clickToOccupy";
    }

    @Override
    public String clickToUnoccupy() {
        return "clickToUnoccupy";
    }
}