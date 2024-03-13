package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_4 {
    private SignatureChecks_4() {}

    void checkZonePartitions() throws Exception {
        v01 = new ch.epfl.chacun.ZonePartitions(v02, v03, v04, v05);
        v01 = ch.epfl.chacun.ZonePartitions.EMPTY;
        v07 = v01.equals(v06);
        v02 = v01.forests();
        v08 = v01.hashCode();
        v03 = v01.meadows();
        v05 = v01.riverSystems();
        v04 = v01.rivers();
        v09 = v01.toString();
    }

    void checkZonePartitions_Builder() throws Exception {
        v10 = new ch.epfl.chacun.ZonePartitions.Builder(v01);
        v10.addInitialOccupant(v11, v12, v13);
        v10.addTile(v14);
        v01 = v10.build();
        v10.clearFishers(v15);
        v10.clearGatherers(v16);
        v10.connectSides(v17, v17);
        v10.removePawn(v11, v13);
    }

    void checkMessageBoard() throws Exception {
        v18 = new ch.epfl.chacun.MessageBoard(v19, v20);
        v07 = v18.equals(v06);
        v08 = v18.hashCode();
        v21 = v18.messages();
        v22 = v18.points();
        v19 = v18.textMaker();
        v09 = v18.toString();
        v18 = v18.withClosedForestWithMenhir(v11, v16);
        v18 = v18.withScoredForest(v16);
        v18 = v18.withScoredHuntingTrap(v11, v23);
        v18 = v18.withScoredLogboat(v11, v24);
        v18 = v18.withScoredMeadow(v23, v25);
        v18 = v18.withScoredPitTrap(v23, v25);
        v18 = v18.withScoredRaft(v24);
        v18 = v18.withScoredRiver(v15);
        v18 = v18.withScoredRiverSystem(v24);
        v18 = v18.withWinners(v26, v08);
    }

    void checkMessageBoard_Message() throws Exception {
        v27 = new ch.epfl.chacun.MessageBoard.Message(v09, v08, v28, v29);
        v07 = v27.equals(v06);
        v08 = v27.hashCode();
        v08 = v27.points();
        v26 = v27.scorers();
        v09 = v27.text();
        v29 = v27.tileIds();
        v09 = v27.toString();
    }

    ch.epfl.chacun.ZonePartitions v01;
    ch.epfl.chacun.ZonePartition<ch.epfl.chacun.Zone.Forest> v02;
    ch.epfl.chacun.ZonePartition<ch.epfl.chacun.Zone.Meadow> v03;
    ch.epfl.chacun.ZonePartition<ch.epfl.chacun.Zone.River> v04;
    ch.epfl.chacun.ZonePartition<ch.epfl.chacun.Zone.Water> v05;
    Object v06;
    boolean v07;
    int v08;
    String v09;
    ch.epfl.chacun.ZonePartitions.Builder v10;
    ch.epfl.chacun.PlayerColor v11;
    ch.epfl.chacun.Occupant.Kind v12;
    ch.epfl.chacun.Zone v13;
    ch.epfl.chacun.Tile v14;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.River> v15;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Forest> v16;
    ch.epfl.chacun.TileSide v17;
    ch.epfl.chacun.MessageBoard v18;
    ch.epfl.chacun.TextMaker v19;
    java.util.List<ch.epfl.chacun.MessageBoard.Message> v20;
    java.util.List<ch.epfl.chacun.MessageBoard.Message> v21;
    java.util.Map<ch.epfl.chacun.PlayerColor, Integer> v22;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Meadow> v23;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Water> v24;
    java.util.Set<ch.epfl.chacun.Animal> v25;
    java.util.Set<ch.epfl.chacun.PlayerColor> v26;
    ch.epfl.chacun.MessageBoard.Message v27;
    java.util.Set<ch.epfl.chacun.PlayerColor> v28;
    java.util.Set<Integer> v29;
}
