package gui;

import java.awt.Color;

import simple.gui.Draw;
import simple.gui.Image;
import simple.run.SimpleGUIApp;
import utils.message.ElevatorScheduleUpdate;

@SuppressWarnings("serial")
public class ElevatorGUI extends SimpleGUIApp {

    ////////////////////////////////////////////////////////////////
    // ------------------------- STATICS ------------------------ //
    ////////////////////////////////////////////////////////////////

    static final Color FLOOR_COLOR = new Color(200, 200, 200);

    ////////////////////////////////////////////////////////////////
    // ------------------------- FIELDS ------------------------- //
    ////////////////////////////////////////////////////////////////

    private ElevatorScheduleUpdate[] _schedules;

    private int     _numFloors;
    private int     _numElevators;
    private boolean _flaggedForRender;

    private Image   _buffer;

    ////////////////////////////////////////////////////////////////
    // ----------------------- PROPERTIES ----------------------- //
    ////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////
    // ------------------------- SETTERS ------------------------ //
    ////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////
    // ---------------------- CONSTRUCTORS ---------------------- //
    ////////////////////////////////////////////////////////////////

    public ElevatorGUI(int numFloors, int numElevators) {
        super(1000, 800, 15);
        _numFloors      = numFloors;
        _numElevators   = numElevators;
    }

    ////////////////////////////////////////////////////////////////
    // ------------------------ OVERRIDES ----------------------- //
    ////////////////////////////////////////////////////////////////

    @Override
    public void setup() {
        _schedules      = new ElevatorScheduleUpdate[_numElevators];
        for (int i=0; i<_numElevators; i++) {
            _schedules[i] = new ElevatorScheduleUpdate();
        }

        _buffer = new Image(windowWidth(), windowHeight());
        render();
    }

    @Override
    public void loop() {

        if (_flaggedForRender) {
            _flaggedForRender = false;
            render();
        }

        Draw.image(_buffer, 0, 0);

        updateView();
    }


    ////////////////////////////////////////////////////////////////
    // ------------------------- METHODS ------------------------ //
    ////////////////////////////////////////////////////////////////


    public void render() {
        int elevatorLeftBound   = 100;
        int elevatorTopBound    = 50;
        int elevatorRightBound  = windowWidth() - 100;
        int elevatorBottomBound = windowHeight() - 200;

        int elevatorSpacing     = 50;
        int elevatorWidth       = (elevatorRightBound - elevatorLeftBound - (elevatorSpacing*(_numElevators-1))) / _numElevators;
        int elevatorHeight      = (elevatorBottomBound - elevatorTopBound);

        int floorHeight         = elevatorHeight/_numFloors;

        for (int elv=0; elv<_numElevators; elv++) {

            for (int floor=0; floor<_numFloors; floor++) {

                Color floorColor = FLOOR_COLOR;

                Draw.setStroke(Color.BLACK);
                Draw.setFill(floorColor);
                Draw.rect(_buffer,
                        elevatorLeftBound+(elv*(elevatorWidth+elevatorSpacing)),
                        elevatorTopBound+(floor*floorHeight),
                        elevatorWidth,
                        floorHeight);
            }
        }
    }


    /* ========================================================== */
    /* ====================== SUBSECTION ====================== */
}
