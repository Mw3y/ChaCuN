package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_1 {
    private SignatureChecks_1() {}

    void checkPreconditions() throws Exception {
        ch.epfl.chacun.Preconditions.checkArgument(v01);
    }

    void checkAnimal() throws Exception {
        v02 = new ch.epfl.chacun.Animal(v03, v04);
        v01 = v02.equals(v05);
        v03 = v02.hashCode();
        v03 = v02.id();
        v04 = v02.kind();
        v03 = v02.tileId();
        v06 = v02.toString();
    }

    void checkAnimal_Kind() throws Exception {
        v04 = ch.epfl.chacun.Animal.Kind.AUROCHS;
        v04 = ch.epfl.chacun.Animal.Kind.DEER;
        v04 = ch.epfl.chacun.Animal.Kind.MAMMOTH;
        v04 = ch.epfl.chacun.Animal.Kind.TIGER;
        v04 = ch.epfl.chacun.Animal.Kind.valueOf(v06);
        v07 = ch.epfl.chacun.Animal.Kind.values();
    }

    void checkDirection() throws Exception {
        v08 = ch.epfl.chacun.Direction.ALL;
        v03 = ch.epfl.chacun.Direction.COUNT;
        v09 = ch.epfl.chacun.Direction.E;
        v09 = ch.epfl.chacun.Direction.N;
        v09 = ch.epfl.chacun.Direction.S;
        v09 = ch.epfl.chacun.Direction.W;
        v09 = ch.epfl.chacun.Direction.valueOf(v06);
        v10 = ch.epfl.chacun.Direction.values();
        v09 = v09.opposite();
        v09 = v09.rotated(v11);
    }

    void checkOccupant() throws Exception {
        v12 = new ch.epfl.chacun.Occupant(v13, v03);
        v03 = ch.epfl.chacun.Occupant.occupantsCount(v13);
        v01 = v12.equals(v05);
        v03 = v12.hashCode();
        v13 = v12.kind();
        v06 = v12.toString();
        v03 = v12.zoneId();
    }

    void checkOccupant_Kind() throws Exception {
        v13 = ch.epfl.chacun.Occupant.Kind.HUT;
        v13 = ch.epfl.chacun.Occupant.Kind.PAWN;
        v13 = ch.epfl.chacun.Occupant.Kind.valueOf(v06);
        v14 = ch.epfl.chacun.Occupant.Kind.values();
    }

    void checkPlayerColor() throws Exception {
        v15 = ch.epfl.chacun.PlayerColor.ALL;
        v16 = ch.epfl.chacun.PlayerColor.BLUE;
        v16 = ch.epfl.chacun.PlayerColor.GREEN;
        v16 = ch.epfl.chacun.PlayerColor.PURPLE;
        v16 = ch.epfl.chacun.PlayerColor.RED;
        v16 = ch.epfl.chacun.PlayerColor.YELLOW;
        v16 = ch.epfl.chacun.PlayerColor.valueOf(v06);
        v17 = ch.epfl.chacun.PlayerColor.values();
    }

    void checkRotation() throws Exception {
        v18 = ch.epfl.chacun.Rotation.ALL;
        v03 = ch.epfl.chacun.Rotation.COUNT;
        v11 = ch.epfl.chacun.Rotation.HALF_TURN;
        v11 = ch.epfl.chacun.Rotation.LEFT;
        v11 = ch.epfl.chacun.Rotation.NONE;
        v11 = ch.epfl.chacun.Rotation.RIGHT;
        v11 = ch.epfl.chacun.Rotation.valueOf(v06);
        v19 = ch.epfl.chacun.Rotation.values();
        v11 = v11.add(v11);
        v03 = v11.degreesCW();
        v11 = v11.negated();
        v03 = v11.quarterTurnsCW();
    }

    void checkZone() throws Exception {
        v03 = ch.epfl.chacun.Zone.localId(v03);
        v03 = ch.epfl.chacun.Zone.tileId(v03);
        v03 = v20.id();
        v03 = v20.localId();
        v21 = v20.specialPower();
        v03 = v20.tileId();
    }

    void checkZone_Lake() throws Exception {
        v22 = new ch.epfl.chacun.Zone.Lake(v03, v03, v21);
        v01 = v22.equals(v05);
        v03 = v22.fishCount();
        v03 = v22.hashCode();
        v03 = v22.id();
        v21 = v22.specialPower();
        v06 = v22.toString();
    }

    void checkZone_River() throws Exception {
        v23 = new ch.epfl.chacun.Zone.River(v03, v03, v22);
        v01 = v23.equals(v05);
        v03 = v23.fishCount();
        v01 = v23.hasLake();
        v03 = v23.hashCode();
        v03 = v23.id();
        v22 = v23.lake();
        v06 = v23.toString();
    }

    void checkZone_Water() throws Exception {
        v03 = v24.fishCount();
    }

    void checkZone_Meadow() throws Exception {
        v25 = new ch.epfl.chacun.Zone.Meadow(v03, v26, v21);
        v27 = v25.animals();
        v01 = v25.equals(v05);
        v03 = v25.hashCode();
        v03 = v25.id();
        v21 = v25.specialPower();
        v06 = v25.toString();
    }

    void checkZone_Forest() throws Exception {
        v28 = new ch.epfl.chacun.Zone.Forest(v03, v29);
        v01 = v28.equals(v05);
        v03 = v28.hashCode();
        v03 = v28.id();
        v29 = v28.kind();
        v06 = v28.toString();
    }

    void checkZone_SpecialPower() throws Exception {
        v21 = ch.epfl.chacun.Zone.SpecialPower.HUNTING_TRAP;
        v21 = ch.epfl.chacun.Zone.SpecialPower.LOGBOAT;
        v21 = ch.epfl.chacun.Zone.SpecialPower.PIT_TRAP;
        v21 = ch.epfl.chacun.Zone.SpecialPower.RAFT;
        v21 = ch.epfl.chacun.Zone.SpecialPower.SHAMAN;
        v21 = ch.epfl.chacun.Zone.SpecialPower.WILD_FIRE;
        v21 = ch.epfl.chacun.Zone.SpecialPower.valueOf(v06);
        v30 = ch.epfl.chacun.Zone.SpecialPower.values();
    }

    void checkPos() throws Exception {
        v31 = new ch.epfl.chacun.Pos(v03, v03);
        v31 = ch.epfl.chacun.Pos.ORIGIN;
        v01 = v31.equals(v05);
        v03 = v31.hashCode();
        v31 = v31.neighbor(v09);
        v06 = v31.toString();
        v31 = v31.translated(v03, v03);
        v03 = v31.x();
        v03 = v31.y();
    }

    void checkPoints() throws Exception {
        v03 = ch.epfl.chacun.Points.forClosedForest(v03, v03);
        v03 = ch.epfl.chacun.Points.forClosedRiver(v03, v03);
        v03 = ch.epfl.chacun.Points.forLogboat(v03);
        v03 = ch.epfl.chacun.Points.forMeadow(v03, v03, v03);
        v03 = ch.epfl.chacun.Points.forRaft(v03);
        v03 = ch.epfl.chacun.Points.forRiverSystem(v03);
    }

    boolean v01;
    ch.epfl.chacun.Animal v02;
    int v03;
    ch.epfl.chacun.Animal.Kind v04;
    java.lang.Object v05;
    java.lang.String v06;
    ch.epfl.chacun.Animal.Kind[] v07;
    java.util.List<ch.epfl.chacun.Direction> v08;
    ch.epfl.chacun.Direction v09;
    ch.epfl.chacun.Direction[] v10;
    ch.epfl.chacun.Rotation v11;
    ch.epfl.chacun.Occupant v12;
    ch.epfl.chacun.Occupant.Kind v13;
    ch.epfl.chacun.Occupant.Kind[] v14;
    java.util.List<ch.epfl.chacun.PlayerColor> v15;
    ch.epfl.chacun.PlayerColor v16;
    ch.epfl.chacun.PlayerColor[] v17;
    java.util.List<ch.epfl.chacun.Rotation> v18;
    ch.epfl.chacun.Rotation[] v19;
    ch.epfl.chacun.Zone v20;
    ch.epfl.chacun.Zone.SpecialPower v21;
    ch.epfl.chacun.Zone.Lake v22;
    ch.epfl.chacun.Zone.River v23;
    ch.epfl.chacun.Zone.Water v24;
    ch.epfl.chacun.Zone.Meadow v25;
    java.util.List v26;
    java.util.List<ch.epfl.chacun.Animal> v27;
    ch.epfl.chacun.Zone.Forest v28;
    ch.epfl.chacun.Zone.Forest.Kind v29;
    ch.epfl.chacun.Zone.SpecialPower[] v30;
    ch.epfl.chacun.Pos v31;
}
