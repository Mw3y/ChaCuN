package ch.epfl.chacun;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an area.
 *
 * @param zones           the list of the zones constituting the area
 * @param occupants       the list of the colors of any player occupying the area, sorted by color
 * @param openConnections the number of open connections of the area
 * @param <Z>             the type of the zone constituting the area
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public record Area<Z extends Zone>(Set<Z> zones, List<PlayerColor> occupants, int openConnections) {

    /**
     * Defensive copy of zones and occupants. Validates the number of open connections.
     *
     * @throws IllegalArgumentException if the number of open connections is negative
     */
    public Area {
        Objects.requireNonNull(openConnections);

        zones = Set.copyOf(zones);
        occupants = List.copyOf(occupants);
        Collections.sort(occupants);

        if (openConnections < 0) {
            throw new IllegalArgumentException("Negative number of open connections");
        }
    }

    /**
     * Checks if a given forest area contains at least one menhir.
     *
     * @param forest the forest area to check
     * @return {@code true} if the forest contains a menhir, {@code false} otherwise
     */
    static boolean hasMenhir(Area<Zone.Forest> forest) {
        return forest.zones.stream()
                .anyMatch(zone -> zone.kind() == Zone.Forest.Kind.WITH_MENHIR);
    }

    /**
     * Counts the number of mushroom groups in a given forest area.
     *
     * @param forest the forest area to check
     * @return the number of mushroom groups in the forest
     */
    static int mushroomGroupCount(Area<Zone.Forest> forest) {
        return (int) forest.zones.stream()
                .filter(zone -> zone.kind() == Zone.Forest.Kind.WITH_MUSHROOMS).count();
    }

    /**
     * Counts the number of animals in a given meadow area, excluding cancelled animals.
     * <p>Cancelled animals can be, for example, deer eaten by smilodons.
     *
     * @param meadow           the meadow area to count animals in
     * @param cancelledAnimals the set of animals to exclude
     * @return the number of animals in the meadow, excluding the cancelled animals
     */
    static Set<Animal> animals(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        return meadow.zones.stream()
                .flatMap(zone -> zone.animals().stream())
                .filter(animal -> !cancelledAnimals.contains(animal))
                .collect(Collectors.toSet());
    }

    /**
     * Counts the number of fish swimming in the given river or in any of the lakes at its ends
     * <p>The fish in a given lake need only be counted once, even if a single lake ends the river at both ends
     *
     * @param river the river area to count fish in
     * @return the total number of fish in the river, lakes included
     */
    static int riverFishCount(Area<Zone.River> river) {
        // Make sure a lake appears only once
        Set<Zone.Lake> lakes = river.zones.stream()
                .filter(Zone.River::hasLake).map(Zone.River::lake).collect(Collectors.toSet());
        // Compute the number of fishes
        int fishCountInLakes = lakes.stream().mapToInt(Zone.Lake::fishCount).sum();
        int fishCountInRivers = river.zones.stream().mapToInt(Zone.River::fishCount).sum();
        // Return the total number of fishes
        return fishCountInLakes + fishCountInRivers;
    }

    /**
     * Counts the number of fish swimming in the given river system
     *
     * @param riverSystem the river system area to count fish in
     * @return the total number of fish in the river system
     */
    static int riverSystemFishCount(Area<Zone.Water> riverSystem) {
        return riverSystem.zones.stream().mapToInt(Zone.Water::fishCount).sum();
    }

    /**
     * Counts the number of lakes in the given river system.
     *
     * @param riverSystem the river system area to count lakes in
     * @return the total number of lakes in the river system
     */
    static int lakeCount(Area<Zone.Water> riverSystem) {
        return (int) riverSystem.zones.stream().filter(Zone.Lake.class::isInstance).count();
    }

    /**
     * Returns whether the area is closed or not.
     *
     * @return {@code true} if the area is closed or {@code else} if it isn't
     */
    public boolean isClosed() {
        return openConnections == 0;
    }

    /**
     * Returns whether the area is occupied or not.
     *
     * @return {@code true} if the area is occupied or {@code else} if it isn't
     */
    public boolean isOccupied() {
        return !occupants.isEmpty();
    }

    /**
     * Returns the players that have the majority of occupants in the area.
     *
     * @return the players that have the majority of occupants in the area
     */
    public Set<PlayerColor> majorityOccupants() {
        int max = 0;
        // Count occurrences of each player color
        int[] occupantCount = new int[PlayerColor.ALL.size()];
        for (PlayerColor occupant : occupants) {
            int newCount = occupantCount[occupant.ordinal()]++;
            if (max < newCount)
                max = newCount;
        }
        // Find the majority occupants
        Set<PlayerColor> majorityOccupants = new HashSet<>();
        for (int i = 0; i < occupantCount.length; i++) {
            if (occupantCount[i] == max)
                majorityOccupants.add(PlayerColor.ALL.get(i));
        }

        return majorityOccupants;
    }

    /**
     * Returns the area resulting from the connection of the receiver (this) to the given area (that).
     *
     * @param newArea the new area to connect to
     * @return the new area resulting from the connection
     */
    public Area<Z> connectTo(Area<Z> newArea) {
        // Merge the zones of both areas into one
        Set<Z> zones = new HashSet<>();
        zones.addAll(this.zones);
        zones.addAll(newArea.zones);
        // Add the new occupants to the current ones
        List<PlayerColor> occupants = new ArrayList<>();
        occupants.addAll(this.occupants);
        occupants.addAll(newArea.occupants);
        // Calculate the new number of open connections
        // The new area will have 2 less open connections, as each area had at least one open connection
        // In case both areas are not the same, we need to add the open connections of the new area
        int openConnections = this.equals(newArea)
                ? this.openConnections - 2
                : this.openConnections + newArea.openConnections - 2;
        // Create the new area
        return new Area<>(zones, occupants, openConnections);
    }

    /**
     * Returns an area identical to the receiver, except that it is occupied only by the given occupant.
     *
     * @param occupant the occupant to put into the new area
     * @return an area identical to the receiver, except that it is occupied only by the given occupant
     */
    public Area<Z> withInitialOccupant(PlayerColor occupant) {
        if (!occupants.isEmpty()) {
            throw new IllegalArgumentException("The area is all ready occupied.");
        }
        return new Area<>(zones, List.of(occupant), this.openConnections);
    }

    /**
     *
     * @param occupant
     * @return
     */
    public Area<Z> withoutOccupant(PlayerColor occupant) {
        if (!occupants.contains(occupant)) {
            throw new IllegalArgumentException("The are does not contain an occupant of the given color.");
        }
        List<PlayerColor> filteredOccupants = occupants.stream().filter(o -> occupant != o).toList();
        return new Area<>(zones, filteredOccupants, openConnections);
    }
}
