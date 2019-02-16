package main;

import java.util.*;

import main.elevator.ElevatorMotor.MotorState;
import utils.message.FloorInputEntry;

public class ElevatorSchedule {
    @SuppressWarnings("serial")
    private static final Map<MotorState, Comparator<FloorStop>> COMPARATOR = new EnumMap<MotorState, Comparator<FloorStop>>(MotorState.class) {{
        put(MotorState.UP, new Comparator<FloorStop>() {
            @Override
            public int compare(FloorStop fs1, FloorStop fs2) {
                return fs1.target() - fs2.target();
            }
        });
        put(MotorState.DOWN, new Comparator<FloorStop>() {
            @Override
            public int compare(FloorStop fs1, FloorStop fs2) {
                return fs2.target() - fs1.target();
            }
        });
    }};
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int             _currentFloor;
    private FloorStop       _currentTarget;
    private MotorState      _currentDirection;

    private List<FloorStop> _nextTargets;
    private MotorState      _nextDirection;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */


    /* ============================= */
    /* ========== SETTERS ========== */

    public void setCurrentFloor(int floor) {
        _currentFloor = floor;
    }
    
    /* ============================= */
    /* ========== GETTERS ========== */

    public int currentFloor() { return this._currentFloor; }

    public FloorStop currentTarget() { return this._currentTarget; }

    public MotorState currentDirection() { return this._currentDirection; }

    public MotorState nextDirection() { return this._nextDirection; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    public ElevatorSchedule() {
        this._currentFloor      = 1;
        this._currentTarget     = null;
        this._currentDirection  = MotorState.STATIONARY;
        this._nextTargets       = new ArrayList<FloorStop>();
        this._nextDirection     = MotorState.STATIONARY;
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Adds a new target to the elevator schedule
     *
     * @param newTarget
     */
    public void addTarget(FloorStop newTarget) {
        if (this._currentTarget == null) {
            // No current target, make current target the new target and change direction accordingly
            this._currentTarget = newTarget;
            if (newTarget.target() < this._currentFloor) {
                this._currentDirection = MotorState.DOWN;
            } else {
                this._currentDirection = MotorState.UP;
            }

            this._nextDirection = newTarget.direction();
        }
        else {
            // There is already a current target, add to the list of next targets in correct position
            this._nextTargets.add(newTarget);
            if (this._nextTargets.size() == 1) {
                Collections.sort(this._nextTargets, COMPARATOR.get(this._nextDirection));
            }
        }
    }
    
    public void addFloorEntry(FloorInputEntry newEntry) {
        
    }
    
    public int cost(FloorInputEntry newEntry) { return 0; }
}
