package net.dertod2.UltimateZones.CreationWizard;

public enum CreationState {
    /**
     * This state is the default State directly after starting the Creation of an
     * Zone
     */
    NoPositionSet,
    /**
     * When the creator of an Zone started to mark Positions this State is the
     * current State
     */
    PositionsSet,
    /**
     * When Enough Positions exist to create the Zone this State is active<br />
     * Warning: The Type Polygon has the extra State 'CheckPossible'
     */
    WaitForCheck,
    /**
     * Extra State for Polygon Zones. When the Creator of the Zone <br />
     * marked at least 3 Positions the Check of the Zone is possible
     */
    CheckPossible,
    /**
     * When the check was done without errors
     */
    CheckSuccesfully;
}