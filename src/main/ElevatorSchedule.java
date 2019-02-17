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
     * Adds a floor input entry to the elevator schedule
     *
     * @param inputEntry
     */
    public void addFloorEntry(FloorInputEntry inputEntry) {
        FloorStop newTarget = new FloorStop(inputEntry);
        addTarget(newTarget);
    }

    /**
     * Adds a button press floor input to the elevator schedule
     *
     * @param target
     */
    public void addButtonPress(int target) {
        FloorStop newTarget = new FloorStop(target, this._currentDirection);
        addTarget(newTarget);
    }

    /**
     * Updates the current target based on the current floor
     */
    public List<Integer> updateCurrentTarget() {
        if (this._currentTarget != null) {
            // First see if we are at our current target before moving
            if (this._currentFloor == this._currentTarget.target()) {
                if (!this._nextTargets.isEmpty()) {
                    this._currentTarget = this._nextTargets.get(0);
                    this._nextTargets.remove(0);

                    if (this._nextTargets.isEmpty() && this._currentTarget.buttonPresses().isEmpty()) {
                        this._nextDirection = MotorState.STATIONARY;
                    }
                    else {
                        this._currentDirection = this._nextDirection;
                    }

                    return this._currentTarget.buttonPresses();
                }
                else {

                    if (this._currentTarget.buttonPresses().isEmpty()) {
                        this._currentDirection  = MotorState.STATIONARY;
                    } else {
                        this._currentDirection = this._currentTarget.direction();
                    }

                    List<Integer> buttonPressesIntegers = this._currentTarget.buttonPresses();
                    this._currentTarget = null;
                    return buttonPressesIntegers;
                }
            }
        }
        return null;
    }

    /**
     * Moves the elevator to the next floor
     */
    public void moveToNextFloor() {
        if (this._currentDirection == MotorState.UP) {
            this._currentFloor++;
        }
        else if (this._currentDirection == MotorState.DOWN) {
            this._currentFloor--;
        }
    }

    /**
     * Checks if the current floor matches the target floor
     *
     * @return
     */
    public boolean atTargetFloor() {
        if (this._currentFloor == this._currentTarget.target()) {
            return true;
        }
        return false;
    }

    /**
     * Calculates the cost of adding the floor entry to the elevator schedule
     *
     * @param entry
     * @return number of floors needed to travel, or -1 if not possible
     */
    public int cost(FloorInputEntry entry) {
        if (this._currentDirection == MotorState.STATIONARY) {
            // idle state
            return Math.abs(this._currentFloor - entry.floor());

        } else if (this._nextDirection == MotorState.STATIONARY) {
            // Heading to a final stop
            return Math.abs(this._currentFloor - _currentTarget.target()) + Math.abs(_currentTarget.target() - entry.floor());

        } else if (this._currentDirection == this._nextDirection) {
            // Going to a request who has the same direction
            if ((this._currentDirection == MotorState.UP && entry.floor() < this._currentFloor)
                    || (this._currentDirection == MotorState.DOWN && entry.floor() > this._currentFloor)
                    || (entry.direction().toMotorState() != this._currentDirection)) {
                // Reject if you're past the request, or if it's in the opposite direction
                return -1;
            }
            return Math.abs(this._currentFloor - entry.floor());

        } else {
            // Going to a request who has the opposite direction
            if (entry.direction().toMotorState() != this._currentDirection) {
                // Reject if it's in the opposite direction
                return -1;
            }
            return Math.abs(this._currentFloor - _currentTarget.target()) + Math.abs(_currentTarget.target() - entry.floor());
        }
    }

    /**
     * Adds a new target to the elevator schedule
     *
     * @param newTarget
     */
    private void addTarget(FloorStop newTarget) {
        if (this._currentDirection == MotorState.STATIONARY || this._currentTarget == null) {
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

            // Check if the current target matches the new target
            if (newTarget.target() == 
                    this._currentTarget.target()) {
                mergeButtonPresses(newTarget, this._currentTarget);
            }
            else {
                // Check if there is already a stop at specified floor
                int stopExists = checkStopExists(newTarget);
                if (stopExists != -1) {
                    mergeButtonPresses(newTarget, this._nextTargets.get(stopExists));
                }
                else {

                    // Determine if we need to swap the current target and new target
                    if (this._nextDirection == MotorState.UP &&
                            this._currentTarget.target() < newTarget.target()) {

                        this._nextTargets.add(0, this._currentTarget);
                        this._currentTarget = newTarget;

                    }
                    else if (this._nextDirection == MotorState.DOWN &&
                            this._currentTarget.target() > newTarget.target()) {
                        this._nextTargets.add(0, this._currentTarget);
                        this._currentTarget = newTarget;
                    }
                    else {
                        this._nextTargets.add(newTarget);
                        if (this._nextTargets.size() > 1) {
                            Collections.sort(this._nextTargets, COMPARATOR.get(this._nextDirection));
                        }
                    }

                }
            }
        }
    }

    /**
     * Helper function to check if a stop exists that matches a given stop
     *
     * @param target
     * @return index of the matching stop, -1 otherwise
     */
    private int checkStopExists(FloorStop target) {
        for (int i = 0; i < this._nextTargets.size(); i++) {
            if (target.target() == this._nextTargets.get(i).target() &&
                    target.direction() == this._nextTargets.get(i).direction()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper function to merge the button presses of two floor stops
     *
     * @param target
     * @param mergeTarget
     */
    private void mergeButtonPresses(FloorStop target, FloorStop existingTarget) {
        for (int i = 0; i < target.buttonPresses().size(); i++) {
            existingTarget.addButtonPress(target.buttonPresses().get(i));
        }
    }
    
    public String targetListAsString() {
        if (_nextTargets.isEmpty()) {
            return "[]";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (FloorStop stop: _nextTargets) {
            result.append(stop.toString() + ", ");
        }
        result.delete(result.length()-2, result.length());
        result.append("]");
        return result.toString();
    }
}
