=========================================
    
    SYSC3303 Group Project Iteration 5
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
		
		Log files for how long certain processes took to complete get
		outputted to the /bin directory. The time is in nanoseconds.
		
		The GUI is started when the Floor subsystem establishes connection
		with the Scheduler. It will run as long as the Floor runs, and
		exiting the window will close down the Floor system.

    ===== Test Files:
        All important data classes are unit tested. There is a unit test
        suite located in the unittest package. To run it, run the 
        UnitTestSuite as a JUnit test case, and this will run all the
        test cases.
        
    ===== Input file format:
        All files now have a digit at the beginning that differentiate
        the type of input. 0 is a floor entry and 1 is an error entry.
        Error entry format is as such:
            <Timestamp> <error type> <elevator number>
        Error types are as follows:
            0: Elevator motor stuck
            1: Door stuck open
            2: Door stuck closed
        

    ===== Breakdown of Responsibilities:
        Troy Havelock:
            - Handled rate analysis
            - Set up messaging system between the floor and scheduler for elevator states
            - Refactored the floor system code

        Austin Mitchell:
			- Created the GUI

        All Team Members:
            - Worked together continuously at each step of the project
            - Contributed a review document for all other members, placed under docs/