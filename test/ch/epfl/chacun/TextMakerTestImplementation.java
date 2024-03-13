package ch.epfl.chacun;

import java.util.Map;
import java.util.Set;

public class TextMakerTestImplementation implements TextMaker {
    @Override
    public String playerName(PlayerColor playerColor) {
        return playerColor.toString();
    }

    @Override
    public String points(int points) {
        return new StringBuilder().append(points).append(" points").toString();
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return new StringBuilder().append("forest.closed.withMenhir(").append(player).append(")").toString();
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        return new StringBuilder().append("forest.scored(").append(scorers).append(",").append(points).append(",").append(mushroomGroupCount).append(",").append(tileCount).append(")").toString();
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        return new StringBuilder().append("river.scored(").append(scorers).append(",").append(points).append(",").append(fishCount).append(",").append(tileCount).append(")").toString();
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        return new StringBuilder().append("huntingTrap.scored(").append(scorer).append(",").append(points).append(",").append(animals).append(")").toString();
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return new StringBuilder().append("logboat.scored(").append(scorer).append(",").append(points).append(",").append(lakeCount).append(")").toString();
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return new StringBuilder().append("meadow.scored(").append(scorers).append(",").append(points).append(",").append(animals).append(")").toString();
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        return new StringBuilder().append("riverSystem.scored(").append(scorers).append(",").append(points).append(",").append(fishCount).append(")").toString();
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        return new StringBuilder().append("pitTrap.scored(").append(scorers).append(",").append(points).append(",").append(animals).append(")").toString();
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        return new StringBuilder().append("raft.scored(").append(scorers).append(",").append(points).append(",").append(lakeCount).append(")").toString();
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        return new StringBuilder().append("winners(").append(winners).append(",").append(points).append(")").toString();
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
