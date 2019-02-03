package main.elevator;

public class ElevatorButton {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ElevatorLamp    _targetLamp;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */

    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    /* ============================= */
    /* ========== SETTERS ========== */
    
    /* ============================= */
    /* ========== GETTERS ========== */
    
    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /** 
     * Elevator button constructor. When pressed, it turns {targetLamp} on.
     * @param targetLamp    Target lamp to turn on when pressed 
     */
    public ElevatorButton(ElevatorLamp targetLamp) {
        _targetLamp = targetLamp;
    }

    /* ============================= */
    /* ========== METHODS ========== */
    
    public void pressButton() {
        _targetLamp.turnON();
    }
}
