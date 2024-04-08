package ch.epfl.chacun;

import java.util.Map;
import java.util.Set;

public final class TextMakerFr implements TextMaker {

    private final Map<String, PlayerColor> players;

    public TextMakerFr(Map<String, PlayerColor> players) {
        this.players = players;
    }

    @Override
    public String playerName(PlayerColor playerColor) {
        for (Map.Entry<String, PlayerColor> entry : players.entrySet()) {
            if (entry.getValue() == playerColor)
                return entry.getKey();
        }
        return null;
    }

    @Override
    public String points(int points) {
        return null;
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return null;
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        return null;
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
