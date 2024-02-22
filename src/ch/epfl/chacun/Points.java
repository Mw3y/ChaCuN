package ch.epfl.chacun;

public final class Points {

    // Forest points
    private final static int CLOSED_FOREST_POINTS_BY_MAJORITY_OCCUPANTS = 2;
    private final static int CLOSED_RIVER_POINTS_BY_MUSHROOM = 3;

    // River points
    private final static int CLOSED_RIVER_POINTS_BY_MAJORITY_OCCUPANTS = 1;
    private final static int CLOSED_RIVER_POINTS_BY_FISH = 1;

    // Meadow points
    private final static int CLOSED_MEADOW_POINTS_BY_MAMMOTH = 3;
    private final static int CLOSED_MEADOW_POINTS_BY_AUROCHS = 2;
    private final static int CLOSED_MEADOW_POINTS_BY_DEER = 1;

    // Logboat points
    private final  static int LOGBOAT_POINTS_BY_LAKE = 2;

    // Raft points
    private final  static int RAFT_POINTS_BY_LAKE = 1;

    private Points() {
    }

    public static int forClosedForest(int tileCount, int mushroomGroupCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(mushroomGroupCount >= 0);
        return CLOSED_FOREST_POINTS_BY_MAJORITY_OCCUPANTS * tileCount + CLOSED_RIVER_POINTS_BY_MUSHROOM * mushroomGroupCount;
    }

    public static int forClosedRiver(int tileCount, int fishCount) {
        Preconditions.checkArgument(tileCount > 1);
        Preconditions.checkArgument(fishCount >= 0);
        return CLOSED_RIVER_POINTS_BY_MAJORITY_OCCUPANTS * tileCount + CLOSED_RIVER_POINTS_BY_FISH * fishCount;

    }

    public static int forMeadow(int mammothCount, int aurochsCount, int deerCount) {
        Preconditions.checkArgument(mammothCount >= 0);
        Preconditions.checkArgument(aurochsCount >= 0);
        Preconditions.checkArgument(deerCount >= 0);
        return CLOSED_MEADOW_POINTS_BY_MAMMOTH * mammothCount + CLOSED_MEADOW_POINTS_BY_AUROCHS * aurochsCount + CLOSED_MEADOW_POINTS_BY_DEER * deerCount;
    }

    public static int forRiverSystem(int fishCount) {
        Preconditions.checkArgument(fishCount >= 0);
        return CLOSED_RIVER_POINTS_BY_FISH * fishCount;
    }

    public static int forLogboat(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return LOGBOAT_POINTS_BY_LAKE * lakeCount;
    }

    public static int forRaft(int lakeCount) {
        Preconditions.checkArgument(lakeCount > 0);
        return RAFT_POINTS_BY_LAKE * lakeCount;
    }
}
