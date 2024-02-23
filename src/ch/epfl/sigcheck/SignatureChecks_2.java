package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_2 {
    private SignatureChecks_2() {}

    void checkTileSide() throws Exception {
        v02 = v01.isSameKindAs(v01);
        v03 = v01.zones();
    }

    void checkTileSide_River() throws Exception {
        v04 = new ch.epfl.chacun.TileSide.River(v05, v06, v05);
        v02 = v04.equals(v07);
        v08 = v04.hashCode();
        v02 = v04.isSameKindAs(v01);
        v05 = v04.meadow1();
        v05 = v04.meadow2();
        v06 = v04.river();
        v09 = v04.toString();
        v03 = v04.zones();
    }

    void checkTileSide_Meadow() throws Exception {
        v10 = new ch.epfl.chacun.TileSide.Meadow(v05);
        v02 = v10.equals(v07);
        v08 = v10.hashCode();
        v02 = v10.isSameKindAs(v01);
        v05 = v10.meadow();
        v09 = v10.toString();
        v03 = v10.zones();
    }

    void checkTileSide_Forest() throws Exception {
        v11 = new ch.epfl.chacun.TileSide.Forest(v12);
        v02 = v11.equals(v07);
        v12 = v11.forest();
        v08 = v11.hashCode();
        v02 = v11.isSameKindAs(v01);
        v09 = v11.toString();
        v03 = v11.zones();
    }

    void checkTile() throws Exception {
        v13 = new ch.epfl.chacun.Tile(v08, v14, v01, v01, v01, v01);
        v01 = v13.e();
        v02 = v13.equals(v07);
        v08 = v13.hashCode();
        v08 = v13.id();
        v14 = v13.kind();
        v01 = v13.n();
        v01 = v13.s();
        v15 = v13.sideZones();
        v16 = v13.sides();
        v09 = v13.toString();
        v01 = v13.w();
        v15 = v13.zones();
    }

    void checkTile_Kind() throws Exception {
        v14 = ch.epfl.chacun.Tile.Kind.MENHIR;
        v14 = ch.epfl.chacun.Tile.Kind.NORMAL;
        v14 = ch.epfl.chacun.Tile.Kind.START;
        v14 = ch.epfl.chacun.Tile.Kind.valueOf(v09);
        v17 = ch.epfl.chacun.Tile.Kind.values();
    }

    void checkPlacedTile() throws Exception {
        v18 = new ch.epfl.chacun.PlacedTile(v13, v19, v20, v21);
        v18 = new ch.epfl.chacun.PlacedTile(v13, v19, v20, v21, v22);
        v02 = v18.equals(v07);
        v23 = v18.forestZones();
        v08 = v18.hashCode();
        v08 = v18.id();
        v08 = v18.idOfZoneOccupiedBy(v24);
        v14 = v18.kind();
        v25 = v18.meadowZones();
        v22 = v18.occupant();
        v19 = v18.placer();
        v21 = v18.pos();
        v26 = v18.potentialOccupants();
        v27 = v18.riverZones();
        v20 = v18.rotation();
        v01 = v18.side(v28);
        v29 = v18.specialPowerZone();
        v13 = v18.tile();
        v09 = v18.toString();
        v18 = v18.withNoOccupant();
        v18 = v18.withOccupant(v22);
        v29 = v18.zoneWithId(v08);
    }

    void checkTileDecks() throws Exception {
        v30 = new ch.epfl.chacun.TileDecks(v31, v31, v31);
        v08 = v30.deckSize(v14);
        v02 = v30.equals(v07);
        v08 = v30.hashCode();
        v32 = v30.menhirTiles();
        v32 = v30.normalTiles();
        v32 = v30.startTiles();
        v09 = v30.toString();
        v13 = v30.topTile(v14);
        v30 = v30.withTopTileDrawn(v14);
        v30 = v30.withTopTileDrawnUntil(v14, v33);
    }

    ch.epfl.chacun.TileSide v01;
    boolean v02;
    java.util.List<ch.epfl.chacun.Zone> v03;
    ch.epfl.chacun.TileSide.River v04;
    ch.epfl.chacun.Zone.Meadow v05;
    ch.epfl.chacun.Zone.River v06;
    Object v07;
    int v08;
    String v09;
    ch.epfl.chacun.TileSide.Meadow v10;
    ch.epfl.chacun.TileSide.Forest v11;
    ch.epfl.chacun.Zone.Forest v12;
    ch.epfl.chacun.Tile v13;
    ch.epfl.chacun.Tile.Kind v14;
    java.util.Set<ch.epfl.chacun.Zone> v15;
    java.util.List<ch.epfl.chacun.TileSide> v16;
    ch.epfl.chacun.Tile.Kind[] v17;
    ch.epfl.chacun.PlacedTile v18;
    ch.epfl.chacun.PlayerColor v19;
    ch.epfl.chacun.Rotation v20;
    ch.epfl.chacun.Pos v21;
    ch.epfl.chacun.Occupant v22;
    java.util.Set<ch.epfl.chacun.Zone.Forest> v23;
    ch.epfl.chacun.Occupant.Kind v24;
    java.util.Set<ch.epfl.chacun.Zone.Meadow> v25;
    java.util.Set<ch.epfl.chacun.Occupant> v26;
    java.util.Set<ch.epfl.chacun.Zone.River> v27;
    ch.epfl.chacun.Direction v28;
    ch.epfl.chacun.Zone v29;
    ch.epfl.chacun.TileDecks v30;
    java.util.List v31;
    java.util.List<ch.epfl.chacun.Tile> v32;
    java.util.function.Predicate<ch.epfl.chacun.Tile> v33;
}
