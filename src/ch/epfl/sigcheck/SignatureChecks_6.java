package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_6 {
    private SignatureChecks_6() {}

    void checkGameState() throws Exception {
        v01 = new ch.epfl.chacun.GameState(v08, v03, v04, v05, v06, v07);
        v01 = ch.epfl.chacun.GameState.initial(v08, v03, v09);
        v05 = v01.board();
        v10 = v01.currentPlayer();
        v12 = v01.equals(v11);
        v14 = v01.freeOccupantsCount(v10, v13);
        v14 = v01.hashCode();
        v15 = v01.lastTilePotentialOccupants();
        v07 = v01.messageBoard();
        v06 = v01.nextAction();
        v08 = v01.players();
        v03 = v01.tileDecks();
        v04 = v01.tileToPlace();
        v16 = v01.toString();
        v01 = v01.withNewOccupant(v17);
        v01 = v01.withOccupantRemoved(v17);
        v01 = v01.withPlacedTile(v18);
        v01 = v01.withStartingTilePlaced();
    }

    void checkGameState_Action() throws Exception {
        v06 = ch.epfl.chacun.GameState.Action.END_GAME;
        v06 = ch.epfl.chacun.GameState.Action.OCCUPY_TILE;
        v06 = ch.epfl.chacun.GameState.Action.PLACE_TILE;
        v06 = ch.epfl.chacun.GameState.Action.RETAKE_PAWN;
        v06 = ch.epfl.chacun.GameState.Action.START_GAME;
        v06 = ch.epfl.chacun.GameState.Action.valueOf(v16);
        v19 = ch.epfl.chacun.GameState.Action.values();
    }

    ch.epfl.chacun.GameState v01;
    ch.epfl.chacun.TileDecks v03;
    ch.epfl.chacun.Tile v04;
    ch.epfl.chacun.Board v05;
    ch.epfl.chacun.GameState.Action v06;
    ch.epfl.chacun.MessageBoard v07;
    java.util.List<ch.epfl.chacun.PlayerColor> v08;
    ch.epfl.chacun.TextMaker v09;
    ch.epfl.chacun.PlayerColor v10;
    Object v11;
    boolean v12;
    ch.epfl.chacun.Occupant.Kind v13;
    int v14;
    java.util.Set<ch.epfl.chacun.Occupant> v15;
    String v16;
    ch.epfl.chacun.Occupant v17;
    ch.epfl.chacun.PlacedTile v18;
    ch.epfl.chacun.GameState.Action[] v19;
}
