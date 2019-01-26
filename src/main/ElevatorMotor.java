package main;

public class ElevatorMotor {
    /* =========================== */
    /* ========== ENUMS ========== */

    public enum MotorMovement {
        UP,
        DOWN,
        STATIONARY
    }

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    MotorMovement _moving = MotorMovement.STATIONARY;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */

    /**
     * Changes the state of the movement of the motor
     *
     * @param movement
     */
    public void setMotorState(MotorMovement movement) {
        this._moving = movement;
    }

    /* ============================= */
    /* ========== GETTERS ========== */

    /**
     * Retrieves the direction the motor is moving in (if any)
     *
     * @return the direction that the motor is moving in
     */
    public MotorMovement getMovementState() {
        return this._moving;
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * ElevatorMotor constructor that takes no params
     * The movement is set to stationary
     */
    public ElevatorMotor() {
        // Empty stub
    }

    /**
     * ElevatorMotor constructor that takes an initial movement state
     *
     * @param movement
     */
    public ElevatorMotor(MotorMovement movement) {
        this._moving = movement;
    }

    /* ============================= */
    /* ========== METHODS ========== */
}
