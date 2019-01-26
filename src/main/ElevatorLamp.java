package main;

public class ElevatorLamp {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int     _floorNumber    = 0;
    private boolean _isLit          = false;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */

    /**
     * Sets the state of the lamp to ON
     */
    public void turnON() {
        this._isLit = true;
    }

    /**
     * Sets the state of the lamp to OFF
     */
    public void turnOFF() {
        this._isLit = false;
    }

    /* ============================= */
    /* ========== GETTERS ========== */

    /**
     * Retrieves the floor number of the lamp
     *
     * @return the floor number of the lamp
     */
    public int floorNumber() {
        return this._floorNumber;
    }

    /**
     * Retrieves whether the lamp is ON or OFF
     *
     * @return the status of the lamp (ON = true, OFF = false)
     */
    public boolean isLit() {
        return this._isLit;
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * ElevatorLamp constructor
     *
     * @param floorNumber
     */
    public ElevatorLamp(int floorNumber) {
        this._floorNumber = floorNumber;
    }

    /* ============================= */
    /* ========== METHODS ========== */
}
