package main.elevator;

public class ElevatorLamp {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private boolean _isLit          = false;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */

    /** Sets the state of the lamp to ON */
    public void turnON() {
        this._isLit = true;
    }

    /** Sets the state of the lamp to OFF */
    public void turnOFF() {
        this._isLit = false;
    }
    
    /* ============================= */
    /* ========== GETTERS ========== */

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

    /** ElevatorLamp constructor. Initially lamp will be turned off. */
    public ElevatorLamp() {
        _isLit = false;
    }

    /* ============================= */
    /* ========== METHODS ========== */
}
