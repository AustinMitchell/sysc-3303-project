package main.elevator;

public class ElevatorMotor {
    /* =========================== */
    /* ========== ENUMS ========== */

    public enum MotorState {
        UP,
        DOWN,
        STATIONARY,
        BROKEN;

        private static final MotorState[] VALUES = MotorState.values();

        public static MotorState fromOrdinal(int i) {
            return VALUES[i];
        }

    }

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private MotorState _motorState;

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
    public void setMotorState(MotorState movement) {
        this._motorState = movement;
    }

    /* ============================= */
    /* ========== GETTERS ========== */

    /**
     * Retrieves the direction the motor is moving in (if any)
     *
     * @return the direction that the motor is moving in
     */
    public MotorState motorState() {
        return this._motorState;
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * ElevatorMotor constructor that takes no params
     * The movement is set to stationary
     */
    public ElevatorMotor() {
        this._motorState = MotorState.STATIONARY;
    }

    /**
     * ElevatorMotor constructor that takes an initial movement state
     *
     * @param movement
     */
    public ElevatorMotor(MotorState motorState) {
        this._motorState = motorState;
    }

    /* ============================= */
    /* ========== METHODS ========== */
}
