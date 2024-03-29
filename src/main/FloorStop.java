package main;

import java.util.*;

import main.elevator.ElevatorMotor.MotorState;
import utils.message.FloorInputEntry;

public class FloorStop {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int             _target;
    private List<Integer>   _buttonPresses;
    private MotorState      _direction;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    /**
     * Retrieves the target of the FloorStop
     *
     * @return the target
     */
    public int target() { return this._target; }

    /**
     * Retrieves the button presses for the floor stop
     *
     * @return the button presses list
     */
    public List<Integer>  buttonPresses() {
        return this._buttonPresses;
    }
    
    public boolean isPickup() { return !_buttonPresses.isEmpty(); }

    /**
     * Retrieves the button presses for the floor stop
     *
     * @return the button presses as an array
     */
    public int[] buttonPressesAsArray() {
        int[] returnList = new int[this._buttonPresses.size()];

        for (int i = 0; i < this._buttonPresses.size(); i++) {
            returnList[i] = this._buttonPresses.get(i);
        }
        return returnList;
    }

    /**
     * Retrieves the direction of the floor stop
     *
     * @return the direction for the floor stop
     */
    public MotorState direction() { return this._direction; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    public FloorStop(int target, MotorState direction) {
        this._target            = target;
        this._buttonPresses     = new ArrayList<Integer>();
        this._direction         = direction;
    }

    public FloorStop(FloorInputEntry inputEntry) {
        this._target = inputEntry.floor();
        switch (inputEntry.direction()) {
        case DOWN:
            this._direction = MotorState.DOWN;
            break;
        case UP:
            this._direction = MotorState.UP;
            break;
        default:
            this._direction = MotorState.STATIONARY;
        }
        this._buttonPresses     = new ArrayList<Integer>();
        this._buttonPresses.add(inputEntry.destination());
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Adds button presses to the collection of button presses
     *
     * @param buttonPress
     */
    public void addButtonPress(int buttonPress) {
        this._buttonPresses.add(buttonPress);
    }
    
    @Override
    public String toString() {
        if (_buttonPresses.isEmpty()) {
            return _target + ": ()";
        }
        
        StringBuilder result = new StringBuilder();
        result.append(_target + ": (");
        for (Integer i: _buttonPresses) {
            result.append(i + ", ");
        }
        result.delete(result.length()-2, result.length());
        result.append(")");
        return result.toString();
    }
}
