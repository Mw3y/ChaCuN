package ch.epfl.chacun;

import java.util.Map;
import java.util.Set;

/**
 * A text maker. It is responsible for generating the text of messages that are displayed to the players.
 *
 * @author Michel Schinz (sciper: 103610)
 */
public interface TextMaker {
    /**
     * Returns the name of the player of the given color.
     *
     * @param playerColor the color of the player
     * @return the name of the player
     */
    String playerName(PlayerColor playerColor);

    /**
     * Returns the textual representation of the given number of points (e.g., "3 points").
     *
     * @param points the number of points
     * @return the textual representation of the number of points
     */
    String points(int points);

    /**
     * Returns the text of a message declaring that a player has closed a forest with a menhir.
     *
     * @param player the player who closed the forest
     * @return the text of the message
     */
    String playerClosedForestWithMenhir(PlayerColor player);

    /**
     * Returns the text of a message declaring that the majority occupants of a newly
     * closed forest, consisting of a certain number of tiles and containing a certain number of mushroom groups,
     * have won the corresponding points.
     *
     * @param scorers            the majority occupants of the forest
     * @param points             the points won
     * @param mushroomGroupCount the number of mushroom groups that the forest contains
     * @param tileCount          the number of tiles that make up the forest
     * @return the text of the message
     */
    String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount);

    /**
     * Returns the text of a message declaring that the majority occupants of a newly
     * closed river, consisting of a certain number of tiles and containing a certain number of fish,
     * have won the corresponding points.
     *
     * @param scorers   the majority occupants of the river
     * @param points    the points won
     * @param fishCount the number of fish swimming in the river or adjacent lakes
     * @param tileCount the number of tiles that make up the river
     * @return the text of the message
     */
    String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount);

    /**
     * Returns the text of a message declaring that a player has placed the pit trap in a meadow containing,
     * on the 8 neighboring tiles of the pit, certain animals, and won the corresponding points.
     *
     * @param scorer  the player who placed the pit trap
     * @param points  the points won
     * @param animals the animals present in the same meadow as the pit and on the 8 neighboring tiles
     * @return the text of the message
     */
    String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals);

    /**
     * Returns the text of a message declaring that a player has placed the logboat in a river system
     * containing a certain number of lakes, and won the corresponding points.
     *
     * @param scorer    the player who placed the logboat
     * @param points    the points won
     * @param lakeCount the number of lakes accessible to the logboat
     * @return the text of the message
     */
    String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount);

    /**
     * Returns the text of a message declaring that the majority occupants of a meadow containing certain
     * animals have won the corresponding points.
     *
     * @param scorers the majority occupants of the meadow
     * @param points  the points won
     * @param animals the animals present in the meadow (excluding those previously cancelled)
     * @return the text of the message
     */
    String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals);

    /**
     * Returns the text of a message declaring that the majority occupants of a river system
     * containing a certain number of fish have won the corresponding points.
     *
     * @param scorers   the majority occupants of the river system
     * @param points    the points won
     * @param fishCount the number of fish swimming in the river system
     * @return the text of the message
     */
    String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount);

    /**
     * Returns the text of a message declaring that the majority occupants of a meadow containing the
     * large pit trap and, on the 8 neighboring tiles of it, certain animals, have won the
     * corresponding points.
     *
     * @param scorers the majority occupants of the meadow containing the pit trap
     * @param points  the points won
     * @param animals the animals present on the tiles neighboring the pit (excluding those previously cancelled)
     * @return the text of the message
     */
    String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals);

    /**
     * Returns the text of a message declaring that the majority occupants of a river system
     * containing the raft have won the corresponding points.
     *
     * @param scorers   the majority occupants of the river system containing the raft
     * @param points    the points won
     * @param lakeCount the number of lakes contained in the river system
     * @return the text of the message
     */
    String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount);

    /**
     * Returns the text of a message declaring that one or more players have won the game, with a
     * certain number of points.
     *
     * @param winners the set of players who have won the game
     * @param points  the points of the winners
     * @return the text of the message
     */
    String playersWon(Set<PlayerColor> winners, int points);

    /**
     * Returns a text asking the current player to click on the occupant they wish to place, or on the text
     * of the message if they do not wish to place any occupant.
     *
     * @return the text in question
     */
    String clickToOccupy();

    /**
     * Returns a text asking the current player to click on the pawn they wish to take back, or on the text
     * of the message if they do not wish to take back any pawn.
     *
     * @return the text in question
     */
    String clickToUnoccupy();
}
