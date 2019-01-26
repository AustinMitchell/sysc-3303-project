package main;

public class ElevatorButton {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int _floorNumber = 0;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    /**
     * Retrieves the floor number of the button
     *
     * @return floor number
     */
    public int floorNumber() {
        return this._floorNumber;
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * Elevator button constructor
     *
     * @param floorNumber
     */
    public ElevatorButton(int floorNumber) {
        this._floorNumber = floorNumber;
    }

    /* ============================= */
    /* ========== METHODS ========== */
}
