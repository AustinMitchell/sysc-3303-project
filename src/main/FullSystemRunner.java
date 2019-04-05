package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import main.elevator.ElevatorManager;
import utils.WorkerThread;

public class FullSystemRunner {

    public static void main(String[] args) {
        WorkerThread<Void, Void> schedulerThread = new WorkerThread<>(new WorkerThread.Job<Void, Void>() {
            @Override
            public Void execute(List<Void> inputData) {
                try {
                    Scheduler.setLogFile();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    new Scheduler().run();
                } catch (SocketException | UnknownHostException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return null;
            }
        });
        schedulerThread.run();

        WorkerThread<Void, Void> floorThread = new WorkerThread<>(new WorkerThread.Job<Void, Void>() {
            @Override
            public Void execute(List<Void> inputData) {
                try {
                    Floor.setLogFile();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    new Floor().run();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                return null;
            }
        });
        floorThread.run();

        WorkerThread<Void, Void> elevatorManagerThread = new WorkerThread<>(new WorkerThread.Job<Void, Void>() {
            @Override
            public Void execute(List<Void> inputData) {
                try {
                    ElevatorManager.setLogFile();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                new ElevatorManager().loop();
                return null;
            }
        });
        elevatorManagerThread.run();

        schedulerThread.waitForJob();
        floorThread.waitForJob();
        elevatorManagerThread.waitForJob();
    }

}
