package ch.epfl.chacun;

import java.util.*;

/**
 * Implementation of the French text maker.
 * It generates the text of French messages that are displayed to the players.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class TextMakerFr implements TextMaker {

    private final Map<PlayerColor, String> players;

    public TextMakerFr(Map<PlayerColor, String> players) {
        this.players = players;
    }

    private String plural(String word, boolean isPlural) {
        boolean isInclusive = word.contains("·");
        return isPlural ? STR."\{word}\{isInclusive ? "·" : ""}s" : word;
    }

    private <E> String plural(String word, Collection<E> values) {
        return plural(word, values.size() > 1);
    }

    private String accord(String word, int value) {
        return STR."\{value} \{plural(word, value > 1)}";
    }

    private String conjugateEarn(Set<PlayerColor> players) {
        return players.size() > 1 ? "ont remporté" : "a remporté";
    }

    private String naturalJoin(List<String> strings) {
        Preconditions.checkArgument(!strings.isEmpty());
        if (strings.size() == 2) {
            return String.join(" et ", strings);
        }
        if (strings.size() > 2) {
            String firstHalf = String.join(", ", strings.subList(0, strings.size() - 1));
            return STR."\{firstHalf} et \{strings.getLast()}";
        }
        return strings.getFirst();
    }

    private String playerNames(Set<PlayerColor> playerColors) {
        return naturalJoin(playerColors.stream().sorted().map(this::playerName).toList());
    }

    private String animalsWithQuantities(Map<Animal.Kind, Integer> animals) {
        return naturalJoin(animals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(e -> e.getValue() > 0)
                .map(e -> accord(getAnimalName(e.getKey()), e.getValue()))
                .toList());
    }

    private String getAnimalName(Animal.Kind kind) {
        return switch (kind) {
            case MAMMOTH -> "mammouth";
            case AUROCHS -> "auroch";
            case DEER -> "cerf";
            case TIGER -> "smilodon";
        };
    }

    @Override
    public String playerName(PlayerColor playerColor) {
        return players.get(playerColor);
    }

    @Override
    public String points(int points) {
        return accord("point", points);
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return STR."\{playerName(player)} a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        String mushrooms = mushroomGroupCount > 0 ?
                STR." et de \{accord("groupe", mushroomGroupCount)} de champignons" : "";
        return STR."\{playerNames(scorers)} \{conjugateEarn(scorers)} \{points(points)} en tant " +
                STR."qu'\{plural("occupant·e", scorers)} \{plural("majoritaire", scorers)} d'une forêt " +
                STR."composée de \{accord("tuile", tileCount)}\{mushroomGroupCount > 0 ? mushrooms : ""}.";
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        return null;
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        return null;
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return null;
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return null;
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return null;
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return null;
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return null;
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return null;
    }

    @Override
    public String clickToOccupy() {
        return null;
    }

    @Override
    public String clickToUnoccupy() {
        return null;
    }
}
