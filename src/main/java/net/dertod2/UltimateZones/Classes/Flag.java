package net.dertod2.UltimateZones.Classes;

public enum Flag {
    // Block Actions
    Place(0, true, false, "Place"), // Setzen von Blöcken -fin-
    Break(1, false, false, "Break"), // Abbauen von Blöcken -fin-
    BreakContainer(2, "BreakContainer"), // Abbauen von Containers -> Kisten, Öfen, Braustände, ... -fin-

    // Container Actions
    ContainerShow(3, "ContainerShow"), // Öffnen von Containern
    ContainerGet(4, false, false, "ContainerGet"), // Items aus Containern entnehmen TODO
    ContainerPut(5, true, false, "ContainerPut"), // Items in Container legen TODO
    ContainerUse(6, "ContainerUse"), // Benutzen von Ambossen

    // Entity Actions
    SlapAnimals(7, "SlapAnimals"), // Tiere angreifen und schädigen
    InteractAnimals(8, "InteractAnimals"), // Schären von Schafen oder sonstige Aktionen an Tieren
    TameAnimals(9, "TameAnimals"), // Zähmen von Katzen, Wölfen oder Pferden -fin-
    FeedAnimals(10, "FeedAnimals"), // Füttern von Tieren zum vermehren

    // Vehicle Actions
    InteractVehicles(11, "InteractVehicles"), // Benutzen von Booten/Loren -fin-
    PlaceVehicles(12, "PlaceVehicles"), // Setzen von Booten/Loren TODO
    BreakVehicles(13, "BreakVehicles"), // Abbauen von Booten/Loren (eigene gesetzte, wenn erlaubt, können trozdem
                                        // abgebaut werden -Idee-) -fin-

    // Redstone Actions
    TriggerWood(14, "TriggerWood"), // Holzknöpfe oder Holzdruckplatten TODO
    TriggerStone(15, "TriggerStone"), // Steinknöpfe und Strindruckplatten TODO
    InteractRedstone(16, "InteractRedstone"), // Schalter und andere Druckplatten (Gold und Eisen) TODO

    // Special
    PickupItems(17, false, false, "PickupItems"), // Aufnehmen von Items die auf dem Boden liegen (eigene weggeworfene
                                                  // können weiter aufgehoben werden) -fin-
    RotateFrames(18, "RotateFrames"), // Drehen von Item Frames
    InteractBed(19, "InteractBed"), // In Bett legen -fin-
    InteractTrapdoor(20, "InteractTrapdoor"), // Falltüren öffnen/schließen TODO
    DenySetHome(21, null, true, "DenySetHome"), // sethome Befehl auf fremden Zonen nutzen
    InteractFenceGate(26, "InteractFenceGate"),

    // Flag Modificators
    PlaceAsWhiteList(22, "PlaceAsWhiteList"), // Selbsterklärend ~ -fin-
    BreakAsWhiteList(23, "BreakAsWhiteList"), // Selbsterklärend ~ -fin-

    // Right Actions
    AdministrateZones(24, "AdministrateZones"), // Hinzufügen von Unterzonen bzw. löschen dieser TODO
    AdministrateRights(25, "AdministrateRights"); // Vergeben von Rechten in Zone + Unterzonen TODO

    public int typeId;
    public Boolean usePlaceList;
    public boolean negatedFlag;

    public String typeName;

    private Flag(int typeId, String typeName) {
        this.typeId = typeId;
        this.usePlaceList = null;
        this.negatedFlag = false;

        this.typeName = typeName;
    }

    private Flag(int typeId, Boolean usePlaceList, boolean negatedFlag, String typeName) {
        this.typeId = typeId;
        this.usePlaceList = usePlaceList;
        this.negatedFlag = negatedFlag;

        this.typeName = typeName;
    }

    public static Flag fromString(String name) {
        for (Flag flag : Flag.values()) {
            if (flag.name().equalsIgnoreCase(name)) {
                return flag;
            }
        }
        return null;
    }

    public static Flag fromInt(int id) {
        for (Flag flag : Flag.values()) {
            if (flag.typeId == id) {
                return flag;
            }
        }
        return null;
    }
}