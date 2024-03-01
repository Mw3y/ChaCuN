package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est pas
// destiné à être exécuté. Son seul but est de vérifier, autant que possible,
// que les noms et les types des différentes entités à définir pour cette
// étape du projet sont corrects.

final class SignatureChecks_3 {
    private SignatureChecks_3() {}

    void checkArea() throws Exception {
        v01 = new ch.epfl.chacun.Area<>(v02, v03, v04);
        v06 = ch.epfl.chacun.Area.animals(v05, v06);
        v08 = ch.epfl.chacun.Area.hasMenhir(v07);
        v04 = ch.epfl.chacun.Area.lakeCount(v09);
        v04 = ch.epfl.chacun.Area.mushroomGroupCount(v07);
        v04 = ch.epfl.chacun.Area.riverFishCount(v10);
        v04 = ch.epfl.chacun.Area.riverSystemFishCount(v09);
        v11 = v01.connectTo(v11);
        v08 = v01.equals(v12);
        v04 = v01.hashCode();
        v08 = v01.isClosed();
        v08 = v01.isOccupied();
        v13 = v01.majorityOccupants();
        v14 = v01.occupants();
        v04 = v01.openConnections();
        v15 = v01.tileIds();
        v16 = v01.toString();
        v11 = v01.withInitialOccupant(v17);
        v11 = v01.withoutOccupant(v17);
        v11 = v01.withoutOccupants();
        v19 = v01.zoneWithSpecialPower(v18);
        v20 = v01.zones();
    }

    void checkZonePartition() throws Exception {
        v21 = new ch.epfl.chacun.ZonePartition<>(v02a);
        v21 = new ch.epfl.chacun.ZonePartition<>();
        v23 = v21.areaContaining(v22);
        v24 = v21.areas();
        v08 = v21.equals(v12);
        v04 = v21.hashCode();
        v16 = v21.toString();
    }

    void checkZonePartition_Builder() throws Exception {
        v25 = new ch.epfl.chacun.ZonePartition.Builder<>(v26);
        v25.addInitialOccupant(v27, v17);
        v25.addSingleton(v27, v04);
        v26 = v25.build();
        v25.removeAllOccupantsOf(v28);
        v25.removeOccupant(v27, v17);
        v25.union(v27, v27);
    }

    ch.epfl.chacun.Area<ch.epfl.chacun.Zone> v01;
    java.util.Set<ch.epfl.chacun.Zone> v02;
    java.util.Set<ch.epfl.chacun.Area<ch.epfl.chacun.Zone>> v02a;
    java.util.List<ch.epfl.chacun.PlayerColor> v03;
    int v04;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Meadow> v05;
    java.util.Set<ch.epfl.chacun.Animal> v06;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Forest> v07;
    boolean v08;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.Water> v09;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone.River> v10;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone> v11;
    Object v12;
    java.util.Set<ch.epfl.chacun.PlayerColor> v13;
    java.util.List<ch.epfl.chacun.PlayerColor> v14;
    java.util.Set<Integer> v15;
    String v16;
    ch.epfl.chacun.PlayerColor v17;
    ch.epfl.chacun.Zone.SpecialPower v18;
    ch.epfl.chacun.Zone v19;
    java.util.Set<ch.epfl.chacun.Zone> v20;
    ch.epfl.chacun.ZonePartition<ch.epfl.chacun.Zone> v21;
    ch.epfl.chacun.Zone v22;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone> v23;
    java.util.Set<ch.epfl.chacun.Area<ch.epfl.chacun.Zone>> v24;
    ch.epfl.chacun.ZonePartition.Builder<ch.epfl.chacun.Zone> v25;
    ch.epfl.chacun.ZonePartition<ch.epfl.chacun.Zone> v26;
    ch.epfl.chacun.Zone v27;
    ch.epfl.chacun.Area<ch.epfl.chacun.Zone> v28;
}
