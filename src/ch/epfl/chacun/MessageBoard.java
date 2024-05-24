package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the message board of the game.
 *
 * @param textMaker the text maker used to create the messages
 * @param messages  the list of messages on the message board
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    /**
     * Defensive copy of the list of messages.
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     * Returns the points scored by each player.
     *
     * @return the points scored by each player
     */
    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> scorers = new HashMap<>();
        for (Message message : messages) {
            for (PlayerColor scorer : message.scorers)
                scorers.merge(scorer, message.points, Integer::sum);
        }
        return scorers;
    }

    /**
     * Counts the number of animals of each kind in the given set of animals.
     * Only consider an animal if there's more than one.
     *
     * @param animals the set of animals
     * @return a map which gives the number of each kind in the given set of animals
     */
    private Map<Animal.Kind, Integer> countAnimals(Set<Animal> animals) {
        Map<Animal.Kind, Integer> animalCount = new HashMap<>();
        for (Animal.Kind kind : Animal.Kind.values()) {
            int count = (int) animals.stream().filter(a -> a.kind() == kind).count();
            if (count > 0)
                animalCount.put(kind, count);
        }
        return animalCount;
    }

    /**
     * Returns the same message board, unless the given forest is occupied,
     * in which case the message board contains a new message indicating that its majority occupants
     * have won the points associated with its closure.
     *
     * @param forest the forest that has been closed
     * @return the same message board, or a new one with a message added if the forest is occupied
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            // Calculate the data needed
            int mushroomGroupCount = Area.mushroomGroupCount(forest);
            int tileCount = forest.tileIds().size();
            int points = Points.forClosedForest(tileCount, mushroomGroupCount);
            String messageContent = textMaker
                    .playersScoredForest(forest.majorityOccupants(), points, mushroomGroupCount, tileCount);
            return addMessage(messageContent, points, forest.majorityOccupants(), forest.tileIds());
        }
        return this;
    }

    /**
     * Returns the same message board, but with a new message indicating that the player
     * has the right to play a second turn after closing the forest, because it contains at least a menhir.
     *
     * @param player the player who closed the forest
     * @param forest the closed forest
     * @return the same message board, or a new one with a message added if the forest contains a menhir
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        String messageContent = textMaker.playerClosedForestWithMenhir(player);
        return addMessage(messageContent, 0, Set.of(), forest.tileIds());
    }

    /**
     * Returns the same message board, unless the given river is occupied,
     * in which case the message board contains a new message indicating that its majority occupants
     * have won the points associated with its closure.
     *
     * @param river the river that has been closed
     * @return the same message board, or a new one with a message added if the river is occupied
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            // Calculate the data needed
            Set<Integer> tileIds = river.tileIds();
            int fishCount = Area.riverFishCount(river);
            int points = Points.forClosedRiver(tileIds.size(), fishCount);
            Set<PlayerColor> scorers = river.majorityOccupants();
            String messageContent = textMaker
                    .playersScoredRiver(scorers, points, fishCount, tileIds.size());
            return addMessage(messageContent, points, scorers, tileIds);
        }
        return this;
    }

    /**
     * Returns the same message board, unless the laying of the hunting trap has
     * resulted in points for the given player who laid it, in which case the scoreboard contains
     * a new message pointing this out.
     *
     * @param scorer           the player who laid the hunting trap
     * @param adjacentMeadow   the meadow adjacent to the hunting trap
     * @param cancelledAnimals the set of cancelled animals
     * @return a new message board if the hunting trap enabled the given player to score points or the same
     * message board
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow,
                                              Set<Animal> cancelledAnimals) {
        Set<Animal> animals = Area.animals(adjacentMeadow, cancelledAnimals);
        // Calculate the data needed
        Map<Animal.Kind, Integer> animalCount = countAnimals(animals);
        int points = pointsForMeadow(animalCount);
        // Check if the hunting trap enabled the player to score points
        if (points > 0) {
            // Add a new message
            String messageContent = textMaker.playerScoredHuntingTrap(scorer, points, animalCount);
            return addMessage(messageContent, points, Set.of(scorer), adjacentMeadow.tileIds());
        }
        return this;
    }

    /**
     * Returns the same message board, but with a new message indicating that the given player has obtained
     * the points corresponding to placing the logboat in the given river system.
     *
     * @param scorer      the player who placed the logboat
     * @param riverSystem the river system in which the logboat was placed
     * @return the same message board, or a new one with a message added if the river system has a log boat
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        // Calculate the data needed
        int lakeCount = Area.lakeCount(riverSystem);
        int points = Points.forLogboat(lakeCount);
        String messageContent = textMaker.playerScoredLogboat(scorer, points, lakeCount);
        return addMessage(messageContent, points, Set.of(scorer), riverSystem.tileIds());
    }

    /**
     * Returns the same message board, unless the given river system is occupied and the points it yields
     * to its majority occupants are greater than 0, in which case the message board contains a new message
     * indicating that these players have won the points in question.
     *
     * @param riverSystem the river system that has been scored
     * @return the same message board, or a new one with a message added if the river system is occupied
     * and points are scored
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            // Calculate the data needed
            int fishCount = Area.riverSystemFishCount(riverSystem);
            int points = Points.forRiverSystem(fishCount);
            Set<PlayerColor> scorers = riverSystem.majorityOccupants();
            // Don't create a message if no points are scored
            if (points > 0) {
                String messageContent = textMaker.playersScoredRiverSystem(scorers, points, fishCount);
                return addMessage(messageContent, points, scorers, riverSystem.tileIds());
            }
        }
        return this;
    }

    /**
     * Returns the same message board, unless the given meadow is occupied and the points it yields
     * to its majority occupants are greater than 0, in which case the message board contains a
     * new message indicating that these players have won the points in question.
     * <p>
     * The points are calculated as if the given canceled animals didn't exist.
     *
     * @param meadow           the meadow that has been scored
     * @param cancelledAnimals the animals that have been cancelled
     * @return the same message board, or a new one with a message added if the meadow is occupied
     * and points are scored
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        if (meadow.isOccupied()) {
            // Calculate the data needed
            Set<Animal> animals = Area.animals(meadow, cancelledAnimals);
            Map<Animal.Kind, Integer> animalCount = countAnimals(animals);
            int points = pointsForMeadow(animalCount);
            Set<PlayerColor> scorers = meadow.majorityOccupants();
            // Don't create a message if no points are scored
            if (points > 0) {
                String messageContent = textMaker.playersScoredMeadow(scorers, points, animalCount);
                return addMessage(messageContent, points, scorers, meadow.tileIds());
            }
        }
        return this;
    }

    /**
     * Returns the same message board, unless the given meadow, is occupied and the points it yields to
     * its majority occupants are greater than 0, in which case the message board contains a new message
     * indicating that these players have won the points in question;
     * <p>
     * As with the "small" stake pit, the given meadow has the same occupants as the meadow containing
     * the pit, but only the areas within its reach.
     * <p>
     * The points are calculated as if the given canceled animals didn't exist.
     *
     * @param adjacentMeadow   the meadow adjacent to the pit trap
     * @param cancelledAnimals the animals that have been cancelled
     * @return the same message board, or a new one with a message added if the meadow is occupied
     * and points are scored
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        if (adjacentMeadow.isOccupied()) {
            Set<Animal> animals = Area.animals(adjacentMeadow, cancelledAnimals);
            Map<Animal.Kind, Integer> animalCount = countAnimals(animals);
            int points = Points.forMeadow(
                    animalCount.getOrDefault(Animal.Kind.MAMMOTH, 0),
                    animalCount.getOrDefault(Animal.Kind.AUROCHS, 0),
                    animalCount.getOrDefault(Animal.Kind.DEER, 0));

            Set<PlayerColor> scorers = adjacentMeadow.majorityOccupants();
            // Don't create a message if no points are scored
            if (points > 0) {
                String messageContent = textMaker.playersScoredPitTrap(scorers, points, animalCount);
                return addMessage(messageContent, points, scorers, adjacentMeadow.tileIds());
            }
        }
        return this;
    }

    /**
     * Returns the same message board, unless the given river network, which contains the raft,
     * is occupied, in which case the message board contains a new message indicating that its
     * majority occupants have won the corresponding points.
     *
     * @param riverSystem the river system containing the raft
     * @return the same message board, or a new one with a message added if the river system is occupied
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        if (riverSystem.isOccupied()) {
            // Calculate the data needed
            Set<PlayerColor> scorers = riverSystem.majorityOccupants();
            int lakeCount = Area.lakeCount(riverSystem);
            int points = Points.forRaft(lakeCount);
            String messageContent = textMaker.playersScoredRaft(scorers, lakeCount, points);
            return addMessage(messageContent, points, scorers, riverSystem.tileIds());
        }
        return this;
    }

    /**
     * Returns the same message board with a new message indicating that the given players have won the game.
     *
     * @param winners the set of players who have won the game
     * @param points  the points of the winners added
     * @return the same message board with a game won message
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        String messageContent = textMaker.playersWon(winners, points);
        return addMessage(messageContent, 0, Set.of(), Set.of());
    }

    /**
     * Counts the number of points given by the animals.
     *
     * @param animalCount the map of the animals and the points each one gives
     * @return the total of points given by the animals
     */
    private int pointsForMeadow(Map<Animal.Kind, Integer> animalCount) {
        return Points.forMeadow(
                animalCount.getOrDefault(Animal.Kind.MAMMOTH, 0),
                animalCount.getOrDefault(Animal.Kind.AUROCHS, 0),
                animalCount.getOrDefault(Animal.Kind.DEER, 0));
    }

    /**
     * Adds a new message to the message board.
     * @param text the text of the message
     * @param points the points associated with the message
     * @param scorers the players who have scored the points
     * @param tileIds the ids of the tiles involved in the message
     * @return the list of messages with the new message added
     */
    private MessageBoard addMessage(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        List<Message> newMessages = new ArrayList<>(messages);
        newMessages.add(new Message(text, points, scorers, tileIds));
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Represents a message on the message board.
     *
     * @param text    the text of the message
     * @param points  the points associated with the message (no points if the action doesn't grant any)
     * @param scorers the players who have scored the points (no player if no points)
     * @param tileIds the ids of the tiles involved in the message
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {

        /**
         * Validates the given text, points, scorers and tileIds.
         *
         * @throws NullPointerException     if the text is null
         * @throws IllegalArgumentException if the points are negative
         */
        public Message {
            Objects.requireNonNull(text);
            Preconditions.checkArgument(points >= 0);
            // Defensive copy
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }

    }

}
