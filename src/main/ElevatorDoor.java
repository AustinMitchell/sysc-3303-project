package main;

public class ElevatorDoor {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private boolean _isOpen = false;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */

    /**
     * sets the state of the door to 'open'
     */
    public void openDoor() {
        this._isOpen = true;
    }

    /**
     * sets the state of the door to 'closed'
     */
    public void closeDoor() {
        this._isOpen = false;
    }

    /* ============================= */
    /* ========== GETTERS ========== */

    /**
     * indicates if the door is open or closed
     *
     * @return door opened or closed (open = true, close = false)
     */
    public boolean isOpen() {
        return this._isOpen;
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * ElevatorDoor Constructor
     */
    public ElevatorDoor() {
        // empty stub
    }

    /* ============================= */
    /* ========== METHODS ========== */
}
