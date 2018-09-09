package net.dertod2.UltimateZones.CreationWizard;

import java.util.List;

import net.dertod2.ZonesLib.Classes.ZoneType;

import com.google.common.collect.ImmutableList;

public enum CreationMethod {
    /**
     * The normal Creation, the User must do the creation complete without an
     * automatic creation wizard
     */
    Normal(NormalCreation.class,
            ImmutableList.<ZoneType>of(ZoneType.CUBOID, ZoneType.CYLINDER, ZoneType.SPHERE, ZoneType.POLYGON));
    // Simple(SimpleCreation.class, ImmutableList.<ZoneType>of(ZoneType.CUBOID));

    private Class<? extends AbstractCreation> wizardClass;
    private List<ZoneType> possibleCreationTypes;

    private CreationMethod(Class<? extends AbstractCreation> wizardClass, List<ZoneType> possibleCreationTypes) {
        this.wizardClass = wizardClass;
        this.possibleCreationTypes = possibleCreationTypes;
    }

    public static CreationMethod get(Class<? extends AbstractCreation> wizardClass) {
        for (CreationMethod creationMethod : CreationMethod.values()) {
            if (wizardClass.isAssignableFrom(creationMethod.wizardClass))
                return creationMethod;
        }

        return null;
    }

    public static CreationMethod get(String creationMethodName) {
        for (CreationMethod creationMethod : CreationMethod.values()) {
            if (creationMethodName.equals(creationMethod.name()))
                return creationMethod;
        }

        return null;
    }

    public Class<? extends AbstractCreation> getWizard() {
        return this.wizardClass;
    }

    public boolean isPossible(ZoneType zoneType) {
        return this.possibleCreationTypes.contains(zoneType);
    }
}