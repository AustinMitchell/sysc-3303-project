package utils.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import main.ElevatorSchedule;
import main.elevator.ElevatorMotor.MotorState;

public class ElevatorScheduleUpdate {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int             _elevatorID;
    private int             _currentTarget;
    private int             _currentFloor;

    private int             _count;

    private MotorState      _motorState;

    private List<Integer>   _elevatorStops;

    private boolean         _motorStuck;
    private boolean         _doorStuck;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_SCHEDULE_UPDATE;

    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    public int              elevatorID()    { return this._elevatorID; }

    public int              currentFloor()  { return this._currentFloor; }

    public int              currentTarget()  { return this._currentTarget; }

    public MotorState       motorState()    { return this._motorState; }

    public List<Integer>    elevatorStops() { return this._elevatorStops; }

    public boolean          motorStuck()    { return this._motorStuck; }

    public boolean          doorStuck()     { return this._doorStuck; }

    public int              count()         { return this._count; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    public ElevatorScheduleUpdate() {
        this._elevatorID    = -1;
        this._currentFloor  = 0;
        this._currentTarget = -1;
        this._count         = -1;
        this._motorState    = MotorState.STATIONARY;
        this._motorStuck    = false;
        this._doorStuck     = false;
        this._elevatorStops = new ArrayList<>();
    }

    public ElevatorScheduleUpdate(int elevatorID, ElevatorSchedule elevatorSchedule) {
        this._elevatorID    = elevatorID;
        this._currentFloor  = elevatorSchedule.currentFloor();
        this._currentTarget = (elevatorSchedule.currentTarget() == null ? -1 : elevatorSchedule.currentTarget().target());
        this._count         = Counter.next();
        this._motorState    = elevatorSchedule.currentDirection();
        this._motorStuck    = elevatorSchedule.motorStuck();
        this._doorStuck     = elevatorSchedule.doorStuck();

        this._elevatorStops = new ArrayList<>();

        for (int i = 0; i < elevatorSchedule.nextTargets().size(); i++) {
            this._elevatorStops.add(elevatorSchedule.nextTargets().get(i).target());
        }
    }

    public ElevatorScheduleUpdate(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);

        ByteBuffer buffer = (ByteBuffer) ByteBuffer.wrap(inputData).position(1);

        int numberOfStops = 0;
        this._elevatorStops = new ArrayList<>();

        this._count         = buffer.getInt();
        this._currentTarget = buffer.get();
        this._elevatorID    = buffer.get();
        this._currentFloor  = buffer.get();
        this._motorState    = MotorState.fromOrdinal(buffer.get());
        this._motorStuck    = (buffer.get() == 1 ? true : false );
        this._doorStuck     = (buffer.get() == 1 ? true : false );

        numberOfStops = buffer.get();

        for (int i = 0; i < numberOfStops; i++) {
            this._elevatorStops.add((int) buffer.get());
        }
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Converts the object into a byte array with following format:
     *      1 byte: message type
     *      4 bytes: message ID
     *      1 byte: current target
     *      1 byte: elevator ID
     *      1 byte: current floor
     *      1 byte: current motor state
     *      1 byte: motor stuck flag
     *      1 byte: door stuck flag
     *      1 byte: number of floor stops
     *      n bytes: array of floor stops
     *
     * @return object as byte array
     */
    public byte[] toBytes() {
        return ByteBuffer.allocate(12 + this._elevatorStops.size())
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(this._count)
                .put((byte)this._currentTarget)
                .put((byte)this._elevatorID)
                .put((byte)this._currentFloor)
                .put((byte)this._motorState.ordinal())
                .put((byte) (this._motorStuck ? 1 : 0))
                .put((byte) (this._doorStuck ? 1 : 0))
                .put((byte) this._elevatorStops.size())
                .put(this.elevatorStopsToByteArray())
                .array();
    }

    /**
     * Helper function to convert the list of elevator stops to a byte array
     *
     * @return elevator stops list as byte array
     */
    private byte[] elevatorStopsToByteArray() {
        byte[] byteArray = new byte[this._elevatorStops.size()];

        for (int i = 0; i < this._elevatorStops.size(); i++) {
            byteArray[i] = this._elevatorStops.get(i).byteValue();
        }

        return byteArray;
    }
}
