package gui;

import java.awt.Color;

import simple.gui.Draw;
import simple.gui.Image;
import simple.run.SimpleGUIApp;
import utils.ResLoader;
import utils.message.ElevatorScheduleUpdate;

@SuppressWarnings("serial")
public class ElevatorGUI extends SimpleGUIApp {

    ////////////////////////////////////////////////////////////////
    // ------------------------- STATICS ------------------------ //
    ////////////////////////////////////////////////////////////////

    private static final Color FLOOR_COLOR          = Color.WHITE;
    private static final Color CURRENT_FLOOR_COLOR  = new Color(210, 255, 200);
    private static final Color CURRENT_TARGET_COLOR = new Color(170, 200, 255);
    private static final Color NEXT_TARGET_COLOR    = new Color(170, 170, 255);

    private static Image BROKEN_IMAGE   = new Image(ResLoader.load("broken.png"));
    private static Image STUCK_IMAGE    = new Image(ResLoader.load("warning.png"));

    ////////////////////////////////////////////////////////////////
    // ------------------------- FIELDS ------------------------- //
    ////////////////////////////////////////////////////////////////

    private ElevatorScheduleUpdate[] _schedules;

    private int     _numFloors;
    private int     _numElevators;
    private boolean _flaggedForRender;

    private Image   _buffer;

    private int _elevatorLeftBound;
    private int _elevatorTopBound;
    private int _elevatorRightBound;
    private int _elevatorBottomBound;
    private int _elevatorSpacing;
    private int _elevatorWidth;
    private int _elevatorHeight;
    private int _floorHeight;

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
        super(1200, 800, 15);
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

        _elevatorLeftBound   = 50;
        _elevatorTopBound    = 20;
        _elevatorRightBound  = windowWidth() - 50;
        _elevatorBottomBound = windowHeight() - 150;

        _elevatorSpacing     = 50;
        _elevatorWidth       = (_elevatorRightBound - _elevatorLeftBound - (_elevatorSpacing*(_numElevators-1))) / _numElevators;
        _elevatorHeight      = (_elevatorBottomBound - _elevatorTopBound);

        _floorHeight         = _elevatorHeight/_numFloors;

        if ((_floorHeight-2)<BROKEN_IMAGE.h()) {
            BROKEN_IMAGE = BROKEN_IMAGE.resizeScaledHeight(_floorHeight-2);
        }

        if ((_floorHeight-2)<STUCK_IMAGE.h()) {
            STUCK_IMAGE = STUCK_IMAGE.resizeScaledHeight(_floorHeight-2);
        }
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

    public void updateSchedule(ElevatorScheduleUpdate update) {
        synchronized(_schedules) {
            _schedules[update.elevatorID()] = update;
            _flaggedForRender = true;
        }
    }

    public void render() {
        synchronized(_schedules) {
            Draw.setStroke(null);
            Draw.setFill(new Color(180, 180, 180));
            Draw.rect(_buffer, 0, 0, windowWidth(), windowHeight());

            // First render all the floors
            for (int elv=0; elv<_numElevators; elv++) {
                int currentLeft = _elevatorLeftBound+((elv)*(_elevatorWidth+_elevatorSpacing));

                for (int floor=0; floor<_numFloors; floor++) {
                    int currentTop  = _elevatorTopBound+((_numFloors - 1 - floor)*_floorHeight);

                    Color floorColor = FLOOR_COLOR;

                    // Determine the colour for the current floor
                    if (_schedules[elv].currentFloor() == floor) {
                        // Draw the current floor green and draw a triangle to the left
                        floorColor = CURRENT_FLOOR_COLOR;

                        int centerX = currentLeft-20;
                        int centerY = currentTop + (_floorHeight/2);
                        int[] x = {centerX-5, centerX-5, centerX+5};
                        int[] y = {centerY-5, centerY+5, centerY};
                        Draw.setStroke(null);
                        Draw.setFill(Color.BLACK);
                        Draw.polygon(_buffer, x, y, 3);

                    } else if (_schedules[elv].currentTarget() == floor) {
                        // Draw floor light cyan
                        floorColor = CURRENT_TARGET_COLOR;

                    } else {
                        for (int stop: _schedules[elv].elevatorStops()) {
                            if (floor == stop) {
                                // Draw floor light blue
                                floorColor = NEXT_TARGET_COLOR;
                                break;
                            }
                        }
                    }

                    // Draw the floor
                    Draw.setStroke(Color.BLACK);
                    Draw.setFill(floorColor);
                    Draw.rect(_buffer,
                            currentLeft,
                            currentTop,
                            _elevatorWidth,
                            _floorHeight);

                    // Draw the floor number to the left
                    Draw.setStroke(Color.BLACK);
                    Draw.textCentered(_buffer, ""+(floor+1), currentLeft+10, currentTop + (_floorHeight/2));

                    // If the elevator is stuck or broken, render the image on the floor
                    if (_schedules[elv].currentFloor() == floor) {
                        if (_schedules[elv].doorStuck()) {
                            Draw.imageCentered(_buffer, STUCK_IMAGE, currentLeft+(_elevatorWidth/2), currentTop+(_floorHeight/2));
                        } else if (_schedules[elv].motorStuck()) {
                            Draw.imageCentered(_buffer, BROKEN_IMAGE, currentLeft+(_elevatorWidth/2), currentTop+(_floorHeight/2));
                        }
                    }
                }

                // Now draw all stuff relevant to just the silo
                int textOffset = _elevatorBottomBound+20;

                Draw.setStroke(Color.black);
                Draw.text(_buffer, "Motor state: " + _schedules[elv].motorState(), currentLeft+2, textOffset);
                textOffset += 14;

                if (_schedules[elv].doorStuck()) {
                    Draw.text(_buffer, "Door is stuck. Resolving...", currentLeft+2, textOffset);
                    textOffset += 14;
                }

                if (_schedules[elv].currentTarget() != -1) {
                    Draw.text(_buffer, "Currently moving to floor " + (_schedules[elv].currentTarget()+1), currentLeft+2, textOffset);
                    textOffset += 14;
                }

                if (!_schedules[elv].elevatorStops().isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    for (Integer i: _schedules[elv].elevatorStops()) {
                        result.append((i+1) + ", ");
                    }
                    result.delete(result.length()-2, result.length());
                    Draw.text(_buffer, "Next stops in queue: " + result.toString(), currentLeft+2, textOffset);
                }
            }
        }
    }
}
