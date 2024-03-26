package ch.epfl.chacun;

/**
 * Helper class to calculate the points for different elements of the game.
 *
 * @author Maxence Espagnet (sciper: 372808)
 * @author Balthazar Baillat (sciper: 373420)
 */
public final class Points {

    // Forest points
    private final static int CLOSED_FOREST_POINTS_BY_MAJORITY_OCCUPANTS = 2;
    private final static int CLOSED_FOREST_POINTS_BY_MUSHROOM = 3;

    // River points
    private final static int CLOSED_RIVER_POINTS_BY_MAJORITY_OCCUPANTS = 1;
    private final static int CLOSED_RIVER_POINTS_BY_FISH = 1;

    // Meadow points
    private final static int MEADOW_POINTS_BY_MAMMOTH = 3;
    private final static int MEADOW_POINTS_BY_AUROCHS = 2;
    private final static int MEADOW_POINTS_BY_DEER = 1;

    // Logboat points
    private final static int LOGBOAT_POINTS_BY_LAKE = 2;

    // Raft points
    private final static int RAFT_POINTS_BY_LAKE = 1;

    /**
     * Private constructor to prevent instantiation.
     */
    private Points() {}

    /**
     * Returns the number of points obtained by the majority pickers in a closed
     * forest made up of {@code tileCount} tiles and {@code mushroomGroupCount} mushroom groups.
     *
     * @param tileCount          the number of tiles in the closed forest
     * @param mushroomGroupCount the number of mushroom groups in the closed forest
     * @return the points given to a player for a closed forest
     * @throws IllegalArgumentException if the tile count is not greater than 1 or the mushroom group count is negative
     */
    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(mushroomGroupCount >= 0);
        return CLOSED_FOREST_POINTS_BY_MAJORITY_OCCUPANTS * tileCount + CLOSED_FOREST_POINTS_BY_MUSHROOM * mushroomGroupCount;
    }

    /**
     * Returns the number of points obtained by the majority anglers in
     * a closed river made up of {@code tileCount} tiles and in which {@code fishCount} fish swim.
     *
     * @param tileCount the number of tiles in the closed river
     * @param fishCount the number of fish in the closed river
     * @return the points given to a player for a closed river
     * @throws IllegalArgumentException if the tile count is not greater than 1
     *                                  or if the fish count is negative
     */
    public static int forClosedRiver(int tileCount, int fishCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(fishCount >= 0);
        return CLOSED_RIVER_POINTS_BY_MAJORITY_OCCUPANTS * tileCount + CLOSED_RIVER_POINTS_BY_FISH * fishCount;
    }

    /**
     * Returns the number of points obtained by the majority hunters in a
     * meadow containing {@code mammothCount} mammoths, {@code aurochsCount} aurochs and {@code deerCount} deer.
     * <p>
     * Deer eaten by smilodons are not included in deerCount.
     *
     * @param mammothCount the number of mammoths in the meadow
     * @param aurochsCount the number of aurochs in the meadow
     * @param deerCount    the number of deers in the meadow
     * @return the points given to a player for a meadow
     * @throws IllegalArgumentException if the mammoth count is not positive
     *                                  or if the aurochs count is not positive
     *                                  or if the deer count is not positive
     */
    public static int forMeadow(int mammothCount, int aurochsCount, int deerCount) {
        Preconditions.checkArgument(mammothCount >= 0);
        Preconditions.checkArgument(aurochsCount >= 0);
        Preconditions.checkArgument(deerCount >= 0);
        return MEADOW_POINTS_BY_MAMMOTH * mammothCount + MEADOW_POINTS_BY_AUROCHS * aurochsCount + MEADOW_POINTS_BY_DEER * deerCount;
    }

    /**
     * Returns the number of points obtained by the majority of anglers in a
     * river system in which {@code fishCount} fish swim.
     *
     * @param fishCount the number of fish in the lake
     * @return the points given to a player for a river system
     * @throws IllegalArgumentException if the fish count is not positive
     */
    public static int forRiverSystem(int fishCount) {
        Preconditions.checkArgument(fishCount >= 0);
        return CLOSED_RIVER_POINTS_BY_FISH * fishCount;
    }

    /**
     * Returns the number of points obtained by the player depositing the
     * logboat in a river system containing {@code lakeCount} lakes.
     *
     * @param lakeCount the number of lakes in the river system
     * @return the points given to a player for a logboat
     * @throws IllegalArgumentException if the lake count is not strictly positive
     */
    public static int forLogboat(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return LOGBOAT_POINTS_BY_LAKE * lakeCount;
    }

    /**
     * Returns the number of additional points obtained by the majority anglers
     * on the river network containing the raft and including {@code lakeCount} lakes.
     *
     * @param lakeCount the number of lakes in the river network
     * @return the points given to a player for a raft
     * @throws IllegalArgumentException if the lake count is not strictly positive
     */
    public static int forRaft(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return RAFT_POINTS_BY_LAKE * lakeCount;
    }
}
