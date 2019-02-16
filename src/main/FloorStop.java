package main;

import java.util.*;

public class FloorStop {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int             _target;
    private List<Integer>   _buttonPresses;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    public int target() { return this._target; }

    public int[] buttonPressesAsArray() {
        int[] returnList = new int[this._buttonPresses.size()];

        for (int i = 0; i < this._buttonPresses.size(); i++) {
            returnList[i] = this._buttonPresses.get(i);
        }
        return returnList;
    }

    public List<Integer>  buttonPresses() {
        return this._buttonPresses;
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    public FloorStop(int target) {
        this._target = target;
        this._buttonPresses = new ArrayList<Integer>();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    public void addButtonPress(int buttonPress) {
        this._buttonPresses.add(buttonPress);
    }
}
