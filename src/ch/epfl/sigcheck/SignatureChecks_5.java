package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_5 {
    private SignatureChecks_5() {}

    void checkBoard() throws Exception {
        v01 = ch.epfl.chacun.Board.EMPTY;
        v02 = ch.epfl.chacun.Board.REACH;
        v05 = v01.adjacentMeadow(v03, v04);
        v07 = v01.canAddTile(v06);
        v08 = v01.cancelledAnimals();
        v07 = v01.couldPlaceTile(v09);
        v07 = v01.equals(v10);
        v12 = v01.forestArea(v11);
        v13 = v01.forestsClosedByLastTile();
        v02 = v01.hashCode();
        v14 = v01.insertionPositions();
        v06 = v01.lastPlacedTile();
        v05 = v01.meadowArea(v04);
        v15 = v01.meadowAreas();
        v02 = v01.occupantCount(v16, v17);
        v18 = v01.occupants();
        v20 = v01.riverArea(v19);
        v22 = v01.riverSystemArea(v21);
        v23 = v01.riverSystemAreas();
        v24 = v01.riversClosedByLastTile();
        v06 = v01.tileAt(v03);
        v06 = v01.tileWithId(v02);
        v01 = v01.withMoreCancelledAnimals(v08);
        v01 = v01.withNewTile(v06);
        v01 = v01.withOccupant(v25);
        v01 = v01.withoutGatherersOrFishersIn(v13, v24);
        v01 = v01.withoutOccupant(v25);
    }

    ch.epfl.chacun.Board v01;
    int v02;
    ch.epfl.chacun.Pos v03;
    ch.epfl.chacun.Zone.Meadow v04;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Meadow> v05;
    ch.epfl.chacun.PlacedTile v06;
    boolean v07;
    java.util.Set<ch.epfl.chacun.Animal> v08;
    ch.epfl.chacun.Tile v09;
    java.lang.Object v10;
    ch.epfl.chacun.Zone.Forest v11;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Forest> v12;
    java.util.Set<ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Forest>> v13;
    java.util.Set<ch.epfl.chacun.Pos> v14;
    java.util.Set<ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Meadow>> v15;
    ch.epfl.chacun.PlayerColor v16;
    ch.epfl.chacun.Occupant.Kind v17;
    java.util.Set<ch.epfl.chacun.Occupant> v18;
    ch.epfl.chacun.Zone.River v19;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.River> v20;
    ch.epfl.chacun.Zone.Water v21;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Water> v22;
    java.util.Set<ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Water>> v23;
    java.util.Set<ch.epfl.chacun.Area<ch.epfl.chacun.Zone.River>> v24;
    ch.epfl.chacun.Occupant v25;
}
