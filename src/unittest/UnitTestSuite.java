package unittest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ElevatorActionRequestTest.class,
        ElevatorActionResponseTest.class,
        ElevatorButtonPushEventTest.class,
        ElevatorButtonTest.class,
        ElevatorContinueRequestTest.class,
        ElevatorContinueResponseTest.class,
        ElevatorDoorTest.class, ElevatorLampTest.class,
        ElevatorMotorTest.class, FloorInputEntryTest.class,
        MessageTest.class, MessageTypeTest.class,
        SchedulerDestinationRequestTest.class,
        TimeStampTest.class })
public class UnitTestSuite {

}
