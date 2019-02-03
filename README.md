=========================================
    
    SYSC3303 Group Project Iteration 1
    Section L2 Group 7
    
    Names:
        Austin Mitchell
        Troy Havelock

    ===== Setup Instructions:
        There are 3 subsytems that must be started in a specific order.
        First, start the Scheduler subsystem. This is in the main
        package and can be run as a Java Application. Then start the
        ElevatorManager, which is located in the main.elevator package.
        This too can be run as a Java Application. Finally, start the
        Floor subsystem, which is located in the main package.

        Launching these 3 subsystems will start the process of the floor,
        reading entries from the floor_input.in file, located in the
        /res/ directory. The floor subsystem will then send the input
        to the scheduler in real time, and the scheduler will command
        the elevator based on this input.

    ===== Test Files:
        All important data classes are unit tested. There is a unit test
        suite located in the unittest package. To run it, run the 
        UnitTestSuite as a JUnit test case, and this will run all the
        test cases.

    ===== Breakdown of Responsibilities:
        Troy Havelock:
            - State Machine Diagram & UML Diagram
            - Test Case writing
            - Parts of Elevator Subsystem
            - Parts of messaging
            - Parts of Floor subsystem

        Austin Mitchell:
            - Scheduler subsystem
            - Elevator subsystem
            - Floor subsystem
            - Messaging package
            - Socket Wrapper classes
            
        All Team Members:
            - Worked together continuously at each step of the project
            - Planned the State Machine together
            - Planned the various sequences of the system together