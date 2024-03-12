package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the message board of the game.
 *
 * @param textMaker the text maker used to create the messages
 * @param messages  the list of messages on the message board
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
            for (PlayerColor scorer : message.scorers) {
                scorers.put(scorer, scorers.getOrDefault(scorer, 0) + message.points);
            }
        }
        return scorers;
    }

    /**
     * Counts the number of animals of each kind in the given set of animals.
     *
     * @param animals the set of animals
     * @return the number of animals of each kind in the given set of animals
     */
    private Map<Animal.Kind, Integer> countAnimals(Set<Animal> animals) {
        Map<Animal.Kind, Integer> animalCount = new HashMap<>();
        for (Animal.Kind kind : Animal.Kind.values()) {
            animalCount.put(kind, (int) animals.stream().filter(a -> a.kind() == kind).count());
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
            ArrayList<Message> messages = new ArrayList<>(this.messages);
            // Calculate the data needed
            int mushroomGroupCount = Area.mushroomGroupCount(forest);
            int tileCount = forest.tileIds().size();
            int points = Points.forClosedForest(tileCount, mushroomGroupCount);
            String messageContent = textMaker
                    .playersScoredForest(forest.majorityOccupants(), points, mushroomGroupCount, tileCount);
            // Create the message
            messages.add(new Message(messageContent, points, forest.majorityOccupants(), forest.tileIds()));
            return new MessageBoard(textMaker, messages);
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
        ArrayList<Message> messages = new ArrayList<>(this.messages);
        String messageContent = textMaker.playerClosedForestWithMenhir(player);
        // Create the message
        messages.add(new Message(messageContent, 0, Set.of(player), forest.tileIds()));
        return new MessageBoard(textMaker, messages);
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
            ArrayList<Message> messages = new ArrayList<>(messages());
            // Calculate the data needed
            int tileCount = river.tileIds().size();
            int fishCount = Area.riverFishCount(river);
            int points = Points.forClosedRiver(tileCount, fishCount);
            String messageContent = textMaker
                    .playersScoredRiver(river.majorityOccupants(), points, fishCount, tileCount);
            // Create the message
            messages.add(new Message(messageContent, points, river.majorityOccupants(), river.tileIds()));
            return new MessageBoard(textMaker, messages);
        }
        return this;
    }

    /**
     * Returns the same message board, unless the laying of the hunting trap has
     * resulted in points for the given player who laid it, in which case the scoreboard contains
     * a new message pointing this out.
     *
     * @param scorer         the player who laid the hunting trap
     * @param adjacentMeadow the meadow adjacent to the hunting trap
     * @return
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow) {
        Set<Animal> animals = Area.animals(adjacentMeadow, new HashSet<>());
        if (!animals.isEmpty()) {
            ArrayList<Message> messages = new ArrayList<>(this.messages);
            // Calculate the data needed
            Map<Animal.Kind, Integer> animalCount = countAnimals(animals);
            int points = Points.forMeadow(
                    animalCount.get(Animal.Kind.MAMMOTH),
                    animalCount.get(Animal.Kind.AUROCHS),
                    animalCount.get(Animal.Kind.DEER));
            // Create the message
            String messageContent = textMaker.playerScoredHuntingTrap(scorer, points, animalCount);
            messages.add(new Message(messageContent, points, Set.of(scorer), adjacentMeadow.tileIds()));
            return new MessageBoard(textMaker, messages);
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
        ArrayList<Message> messages = new ArrayList<>(this.messages);
        // Calculate the data needed
        int lakeCount = Area.lakeCount(riverSystem);
        int points = Points.forLogboat(lakeCount);
        String messageContent = textMaker.playerScoredLogboat(scorer, lakeCount, points);
        // Create the message
        messages.add(new Message(messageContent, points, Set.of(scorer), riverSystem.tileIds()));
        return new MessageBoard(textMaker, messages);
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
                String messageContent = textMaker.playersScoredRiverSystem(riverSystem.majorityOccupants(),
                        points, fishCount);
                // Create the message
                ArrayList<Message> messages = new ArrayList<>(this.messages);
                messages.add(new Message(messageContent, points, scorers, riverSystem.tileIds()));
                return new MessageBoard(textMaker, messages);
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
            int points = Points.forMeadow(
                    animalCount.get(Animal.Kind.MAMMOTH),
                    animalCount.get(Animal.Kind.AUROCHS),
                    animalCount.get(Animal.Kind.DEER));

            Set<PlayerColor> scorers = meadow.majorityOccupants();
            // Don't create a message if no points are scored
            if (points > 0) {
                String messageContent = textMaker.playersScoredMeadow(scorers, points, animalCount);
                // Create the message
                ArrayList<Message> messages = new ArrayList<>(this.messages);
                messages.add(new Message(messageContent, points, scorers, meadow.tileIds()));
                return new MessageBoard(textMaker, messages);
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
            Set<Animal> animals = Area.animals(adjacentMeadow, new HashSet<>());
            Map<Animal.Kind, Integer> animalCount = countAnimals(animals);
            int points = Points.forMeadow(
                    animalCount.get(Animal.Kind.MAMMOTH),
                    animalCount.get(Animal.Kind.AUROCHS),
                    animalCount.get(Animal.Kind.DEER));

            Set<PlayerColor> scorers = adjacentMeadow.majorityOccupants();
            // Don't create a message if no points are scored
            if (points > 0) {
                String messageContent = textMaker.playersScoredPitTrap(scorers, points, animalCount);
                ArrayList<Message> messages = new ArrayList<>(this.messages);
                messages.add(new Message(messageContent, points, scorers, adjacentMeadow.tileIds()));
                return new MessageBoard(textMaker, messages);
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
            ArrayList<Message> messages = new ArrayList<>(this.messages);
            // Calculate the data needed
            Set<PlayerColor> scorers = riverSystem.majorityOccupants();
            int lakeCount = Area.lakeCount(riverSystem);
            int points = Points.forRaft(lakeCount);
            String messageContent = textMaker.playersScoredRaft(scorers, lakeCount, points);
            // Create the message
            messages.add(new Message(messageContent, points, scorers, riverSystem.tileIds()));
            return new MessageBoard(textMaker, messages);
        }
        return this;
    }

    /**
     * Returns the same message board with a new message indicating that the given players have won the game.
     *
     * @param winners the set of players who have won the game
     * @param points  the points of the winners
     * @return the same message board with a game won message
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        ArrayList<Message> messages = new ArrayList<>(this.messages);
        String messageContent = textMaker.playersWon(winners, points);
        messages.add(new Message(messageContent, points, winners, Set.of()));
        return new MessageBoard(textMaker, messages);
    }

    /**
     * Represents a message on the message board.
     *
     * @param text    the text of the message
     * @param points  the points associated with the message
     * @param scorers the players who have scored the points
     * @param tileIds the ids of the tiles involved in the message
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {

        /**
         * Validates the given text, points, scorers and tileIds.
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