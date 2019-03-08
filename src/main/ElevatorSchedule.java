package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import main.elevator.ElevatorMotor.MotorState;
import utils.message.FloorInputEntry;

public class ElevatorSchedule {

    @SuppressWarnings("serial")
    private static final Map<MotorState, Comparator<FloorStop>> COMPARATOR = new EnumMap<MotorState, Comparator<FloorStop>>(MotorState.class) {
        {
            put(MotorState.UP, new Comparator<FloorStop>() {
                @Override
                public int compare(FloorStop fs1, FloorStop fs2) { return fs1.target() - fs2.target(); }
            });
            put(MotorState.DOWN, new Comparator<FloorStop>() {
                @Override
                public int compare(FloorStop fs1, FloorStop fs2) { return fs2.target() - fs1.target(); }
            });
        }
    };

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int        _currentFloor;
    private FloorStop  _currentTarget;
    private MotorState _currentDirection;

    private List<FloorStop> _nextTargets;
    private MotorState      _nextDirection;
    private boolean         _canStopCurrentFloor;
    private boolean         _isWaitingForJob;
    private boolean         _doorStuck;
    private boolean         _motorStuck;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */


    /* ============================= */
    /* ========== SETTERS ========== */

    public void setCurrentFloor(int floor) { _currentFloor = floor; }

    public void setCanStopCurrentFloor(boolean canStop) { _canStopCurrentFloor = canStop; }

    public void disengage() { _isWaitingForJob = true; }
    
    public void setDoorStuck(boolean stuck) { _doorStuck = stuck; }
    
    public void setMotorStuck(boolean stuck) { _motorStuck = stuck; }

    /* ============================= */
    /* ========== GETTERS ========== */

    public boolean isWaitingForJob() { return this._isWaitingForJob; }

    public int currentFloor() { return this._currentFloor; }

    public FloorStop currentTarget() { return this._currentTarget; }

    public MotorState currentDirection() { return this._currentDirection; }

    public MotorState nextDirection() { return this._nextDirection; }
    
    public List<FloorStop> allPickupRequests() {
        List<FloorStop> pickups = new ArrayList<>();
        
        if (_currentTarget.isPickup()) {
            pickups.add(_currentTarget);
        }
        
        for (FloorStop fs: _nextTargets) {
            if (fs.isPickup()) {
                pickups.add(fs);
            }
        }
        
        return pickups;
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    public ElevatorSchedule() {
        this._currentFloor        = 1;
        this._currentTarget       = null;
        this._currentDirection    = MotorState.STATIONARY;
        this._nextTargets         = new ArrayList<>();
        this._nextDirection       = MotorState.STATIONARY;
        this._canStopCurrentFloor = true;
        this._isWaitingForJob     = true;
    }

    /* ============================= */
    /* ========== METHODS ========== */
    
    public void disable() {
        disengage();
        setMotorStuck(true);
        _currentDirection = MotorState.STATIONARY;
        _currentTarget = null;
        _nextDirection = null;
        _nextTargets = null;
    }

    /** Adds a floor input entry to the elevator schedule
     * @param inputEntry */
    public void addNewTarget(FloorInputEntry inputEntry) {
    	addNewTarget(new FloorStop(inputEntry));
    }
    
    /** Adds a floor stop to the elevator schedule
     * @param floorStop */
    public void addNewTarget(FloorStop floorStop) {
        addTarget(floorStop);
    }

    /** Adds a button press floor input to the elevator schedule
     * @param target */
    public void addButtonPress(int target) {
        FloorStop newTarget = new FloorStop(target, this._currentDirection);
        addTarget(newTarget);
    }

    /** Updates the current target based on the current floor */
    public List<Integer> updateCurrentTarget() {
        if ((this._currentTarget != null) && (this._currentFloor == this._currentTarget.target())) {
            if (!this._nextTargets.isEmpty()) {
                this._currentTarget = this._nextTargets.get(0);
                this._nextTargets.remove(0);

                if (this._nextTargets.isEmpty() && this._currentTarget.buttonPresses().isEmpty()) {
                    this._nextDirection = MotorState.STATIONARY;
                } else {
                    this._currentDirection = this._nextDirection;
                }

                return this._currentTarget.buttonPresses();
            } else {

                if (this._currentTarget.buttonPresses().isEmpty()) {
                    this._currentDirection = MotorState.STATIONARY;
                } else {
                    this._currentDirection = this._currentTarget.direction();
                }

                List<Integer> buttonPressesIntegers = this._currentTarget.buttonPresses();
                this._currentTarget = null;
                return buttonPressesIntegers;
            }
        }
        return null;
    }

    /** Moves the elevator to the next floor */
    public void moveToNextFloor() {
        if (this._currentDirection == MotorState.UP) {
            this._currentFloor++;
        } else if (this._currentDirection == MotorState.DOWN) {
            this._currentFloor--;
        }
    }

    /** Checks if the current floor matches the target floor
     * @return */
    public boolean atTargetFloor() { return this._currentFloor == this._currentTarget.target(); }

    /** Calculates the cost of adding the floor entry to the elevator schedule
     * @param  entry
     * @return       number of floors needed to travel, or -1 if not possible */
    public int cost(FloorInputEntry entry) {
        return cost(new FloorStop(entry));
    }
    
    public int cost(FloorStop stop) {
    	int retValue;
    	
    	if (_motorStuck) {
    	    return -1;
    	}

        if (this._currentDirection == MotorState.STATIONARY) {
            // idle state
            retValue = Math.abs(this._currentFloor - stop.target());

        } else if (this._nextDirection == MotorState.STATIONARY) {
            // Heading to a final stop
            retValue = Math.abs(this._currentFloor - _currentTarget.target()) + Math.abs(_currentTarget.target() - stop.target());

        } else if (this._currentDirection == this._nextDirection) {
            // Going to a request who has the same direction
            if (((this._currentDirection == MotorState.UP) && (stop.target() < this._currentFloor)) 
            		|| ((this._currentDirection == MotorState.DOWN) && (stop.target() > this._currentFloor))
                    || (stop.direction() != this._currentDirection)) {
                // Reject if you're past the request, or if it's in the opposite direction
                return -1;
            }
            retValue = Math.abs(this._currentFloor - stop.target());

        } else {
            // Going to a request who has the opposite direction
            if (stop.direction() != this._nextDirection) {
                // Reject if it's in the opposite direction
                return -1;
            }
            retValue = Math.abs(this._currentFloor - _currentTarget.target()) + Math.abs(_currentTarget.target() - stop.target());
        }

        /*
         * If we're currently at a floor for a request in the same direction (i.e. zero cost) and we've indicated the
         * elevator has already passed, then say we've already passed this floor
         */
        if ((retValue == 0) && !_canStopCurrentFloor) {
            return -1;
        } else {
            return retValue;
        }
    }

    /** Adds a new target to the elevator schedule
     * @param newTarget */
    private void addTarget(FloorStop newTarget) {
        this._isWaitingForJob = false;
        if ((this._currentDirection == MotorState.STATIONARY) || (this._currentTarget == null)) {
            // No current target, make current target the new target and change direction accordingly
            this._currentTarget = newTarget;
            if (newTarget.target() < this._currentFloor) {
                this._currentDirection = MotorState.DOWN;
            } else {
                this._currentDirection = MotorState.UP;
            }

            this._nextDirection = newTarget.direction();
        } else {
            // There is already a current target, add to the list of next targets in correct position
            if (this._nextDirection == MotorState.STATIONARY) {
                this._nextDirection = newTarget.direction();
            }

            // Check if the current target matches the new target
            if (newTarget.target() == this._currentTarget.target()) {
                mergeButtonPresses(newTarget, this._currentTarget);
            } else {
                // Check if there is already a stop at specified floor
                int stopExists = checkStopExists(newTarget);
                if (stopExists != -1) {
                    mergeButtonPresses(newTarget, this._nextTargets.get(stopExists));
                } else {

                    // Determine if we need to swap the current target and new target
                    if ((((this._nextDirection == MotorState.UP) && (this._currentTarget.target() > newTarget.target()))
                            || ((this._nextDirection == MotorState.DOWN) && (this._currentTarget.target() < newTarget.target())))
                            && this._currentTarget.isPickup()) {

                        this._nextTargets.add(0, this._currentTarget);
                        this._currentTarget = newTarget;

                    } else {
                        this._nextTargets.add(newTarget);
                        if (this._nextTargets.size() > 1) {
                            if ((this._nextDirection == null) || (this._nextDirection == MotorState.STATIONARY)) {
                                throw new RuntimeException("Next direction was NOT set correctly previously");
                            }
                            Collections.sort(this._nextTargets, COMPARATOR.get(this._nextDirection));
                        }
                    }

                }
            }
        }
    }

    /** Helper function to check if a stop exists that matches a given stop
     * @param  target
     * @return        index of the matching stop, -1 otherwise */
    private int checkStopExists(FloorStop target) {
        for (int i = 0; i < this._nextTargets.size(); i++) {
            if ((target.target() == this._nextTargets.get(i).target()) && (target.direction() == this._nextTargets.get(i).direction())) {
                return i;
            }
        }
        return -1;
    }

    /** Helper function to merge the button presses of two floor stops
     * @param target
     * @param mergeTarget */
    private void mergeButtonPresses(FloorStop target, FloorStop existingTarget) {
        for (int i = 0; i < target.buttonPresses().size(); i++) {
            existingTarget.addButtonPress(target.buttonPresses().get(i));
        }
    }
    
    public String statusString() {
        if (_motorStuck) {
            return "Motor stuck";
        } else if (_doorStuck) {
            return "Door stuck";
        } else if (_isWaitingForJob) {
            return "Idle";
        } else {
            return "Busy";
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
        result.delete(result.length() - 2, result.length());
        result.append("]");
        return result.toString();
    }
}
