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
        this.players = new HashMap<>(players);
    }

    private String pluralize(String word, boolean isPlural) {
        boolean isInclusive = word.contains("·");
        return isPlural ? STR."\{word}\{isInclusive ? "·" : ""}s" : word;
    }

    private <E> String pluralize(String word, Collection<E> values) {
        return pluralize(word, values.size() > 1);
    }

    private String accord(String word, int value) {
        return STR."\{value} \{pluralize(word, value > 1)}";
    }

    private String conjugateEarn(Set<PlayerColor> players) {
        return players.size() > 1 ? "ont remporté" : "a remporté";
    }

    private String humanizedJoin(List<String> strings) {
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

    private String joinPlayerNames(Set<PlayerColor> playerColors) {
        return humanizedJoin(playerColors.stream().sorted().map(this::playerName).toList());
    }

    private String joinAnimalsWithQuantities(Map<Animal.Kind, Integer> animals) {
        return humanizedJoin(animals.entrySet().stream()
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
        return STR."\{joinPlayerNames(scorers)} \{conjugateEarn(scorers)} \{points(points)} en tant " +
                STR."qu'\{pluralize("occupant·e", scorers)} " +
                STR."\{pluralize("majoritaire", scorers)} d'une forêt " +
                STR."composée de \{accord("tuile", tileCount)}\{mushroomGroupCount > 0 ? mushrooms : ""}.";
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        return STR."\{joinPlayerNames(scorers)} \{conjugateEarn(scorers)} \{points(points)} en tant qu'" +
                STR."\{pluralize("occupant·e", scorers)} " +
                STR."\{pluralize("majoritaire", scorers)} d'une rivière " +
                STR."composée de \{accord("tuile", tileCount)}" +
                STR."\{fishCount > 0 ? STR." et contenant \{accord("poisson", fishCount)}" : ""}.";
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{playerName(scorer)} a remporté \{points(points)} en plaçant la fosse à pieux dans un pré " +
                STR."dans lequel elle est entourée de \{joinAnimalsWithQuantities(animals)}.";
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return STR."\{playerName(scorer)} a remporté \{points(points)} en plaçant la pirogue dans un réseau " +
                STR."hydrographique contenant \{accord("lac", lakeCount)}.";
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{joinPlayerNames(scorers)} \{conjugateEarn(scorers)} \{points(points)} en tant qu'" +
                STR."\{pluralize("occupant·e", scorers)} " +
                STR."\{pluralize("majoritaire", scorers)} d'un pré contenant " +
                STR."\{joinAnimalsWithQuantities(animals)}.";
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return STR."\{joinPlayerNames(scorers)} \{conjugateEarn(scorers)} \{points(points)} en tant qu'" +
                STR."\{pluralize("occupant·e", scorers)} " +
                STR."\{pluralize("majoritaire", scorers)} d'un réseau " +
                STR."hydrographique contenant \{accord("poisson", fishCount)}.";
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return STR."\{joinPlayerNames(scorers)} \{conjugateEarn(scorers)} \{points(points)} " +
                STR."en tant qu'\{pluralize("occupant·e", scorers)} " +
                STR."\{pluralize("majoritaire", scorers)} d'un pré contenant " +
                STR."la grande fosse à pieux entourée de \{joinAnimalsWithQuantities(animals)}.";
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return STR."\{joinPlayerNames(scorers)} \{conjugateEarn(scorers)} \{points(points)} en tant " +
                STR."qu'\{pluralize("occupant·e", scorers)} " +
                STR."\{pluralize("majoritaire", scorers)} " +
                STR."d'un réseau hydrographique contenant le radeau et \{accord("lac", lakeCount)}.";
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return STR."\{joinPlayerNames(winners)} \{conjugateEarn(winners)} la partie avec \{points(points)} !";
    }

    @Override
    public String clickToOccupy() {
        return "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    }

    @Override
    public String clickToUnoccupy() {
        return "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";
    }
}
