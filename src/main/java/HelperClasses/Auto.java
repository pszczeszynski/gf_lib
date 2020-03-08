package HelperClasses;

import android.os.SystemClock;

import java.util.ArrayList;

import Debugging.ComputerDebugging;
import ReturnTypes.FloatPoint;
import RobotUtilities.MovementEssentials;
import RobotUtilities.MyPosition;

import static RobotUtilities.MyPosition.worldAngle_rad;
import static RobotUtilities.MyPosition.worldXPosition;
import static RobotUtilities.MyPosition.worldYPosition;

/**
 * Auto is used by autonomous opmodes
 */
public class Auto extends Robot {
    public boolean stageFinished = true;
    public long stateStartTime = 0;

    public long programStartTime = 0;//time the program starts


    public static int programStage = 0;



    /** BLOCK STARTING VARIABLES */
    public double blockStartingX = 0;
    public double blockStartingY = 0;
    //This is in radians
    public double blockStartingAngle_rad = 0;
    ///////////////////////////////

    /** Debugging Stuff */
    public ArrayList<Double> markedXLocations = new ArrayList<Double>();
    public ArrayList<Double> markedYLocations = new ArrayList<Double>();
    public ArrayList<Double> markedAngleLocations = new ArrayList<Double>();
    public ArrayList<Integer> markedStateIndexes = new ArrayList<Integer>();


    //this will make the robot stop after every move, until the user presses the a button
    public static boolean debugging = false;

    public void startDebugging(){
        debugging = true;
    }


    private boolean inDebugState = false;

    boolean gamepad1_x_last = false;


    //holds the stage we are going to next
    int nextStage = 0;
    public void nextStage(int ordinal) {
        nextStage = ordinal;
        //waits for a if on debug mode
        if(!debugging){
            incrementStage();
            inDebugState = false;
        }

        //go into debug mode
        if(debugging){
            inDebugState = true;
        }
    }

    /** Increments the programStage */
    public void nextStage() {
        nextStage(programStage + 1);

    }
    private void incrementStage() {
        programStage  = nextStage;
        stageFinished = true;
    }

    private void displayMarkedPoints() {
        for(int i = 0; i < markedXLocations.size(); i ++){
            telemetry.addLine("State " + markedStateIndexes.get(i) + " saved: \n" +
                    "xPos: " + df.format(markedXLocations.get(i)) +
                    " yPos: " + df.format(markedYLocations.get(i)) +
                    " angle (deg): " + df.format(markedAngleLocations.get(i)) + "\n\n\n");
        }
    }

    private void savePoint() {
        markedXLocations.add(getXPos());
        markedYLocations.add(getYPos());
        markedAngleLocations.add(getAngle_deg());
        markedStateIndexes.add(programStage);
    }

    /** called during the init of any stage */
    public void initializeStateVariables() {
        stageFinished = false;
        blockStartingX = worldXPosition;
        blockStartingY = worldYPosition;
        blockStartingAngle_rad = worldAngle_rad;
        stateStartTime = SystemClock.uptimeMillis();
        MovementEssentials.initCurve();
    }


    private double startingPos_x = 0;
    private double startingPos_y = 0;
    private double startingPos_angle_rad = 0;

    public void setStartingPosition(double x, double y, double angle_rad){
        startingPos_x = x;
        startingPos_y = y;
        startingPos_angle_rad = angle_rad;
    }

    /**
     * Mostly makes sure we start in 18 inches
     */
    @Override
    public void init(){
        super.init();//also need to call the super method
    }


    /**
     * Set's our position to the starting postition although this is dumb
     * because who even uses this anyway, we'll reset1 when we get down
     */
    @Override
    public void init_loop(){
        super.init_loop();
        //Now we can set our position
        MyPosition.setPosition(startingPos_x,startingPos_y,startingPos_angle_rad);
    }
    @Override
    public void start(){
        super.start();
        //Now we can set our position
        MyPosition.setPosition(startingPos_x,startingPos_y,startingPos_angle_rad);

        stageFinished = true;//need to call initialize state variables
        programStage = 0;//start on the first state
        programStartTime = SystemClock.uptimeMillis();//record the start time of the program
    }
    @Override
    public void loop(){
        super.loop();
        DebugController();

        //logs where we are for the debugger
        ComputerDebugging.sendLogPoint(new FloatPoint(getXPos(),getYPos()));
    }

    /**
     * allows debugging the autonomous
     */
    private void DebugController() {
        //if we are in a debug state, we take control and don't call the child's MainStateMachine
        if(inDebugState){
            if(gamepad1.x && !gamepad1_x_last){
                savePoint();
            }
            gamepad1_x_last = gamepad1.x;

            ControlMovement(false);
            displayMarkedPoints();

            telemetry.addLine("in debug state");
            if(gamepad1.a){
                incrementStage();
                inDebugState = false;
            }
        }else{
            //business as usual, go to MainStateMachine
            MainStateMachine();



            //if we are debugging only move as fast as the user presses the RIGHT trigger
            //this way it is safer
            if(debugging){
                carefulDebuggingMode();
            }
        }
    }



    //Override me
    //this really should be an abstract class
    public void MainStateMachine() {

    }


    /**
     * This returns true if we have completed a time out
     * @param milliseconds -> time
     * @return
     */
    public boolean isTimedOut(int milliseconds){
        return (currTimeMillis - stateStartTime > milliseconds && !debugging);
    }





    //scale down all motor powers with this
    public static double masterMotorScale = 1.0;
    /**
     * This allows for careful debugging by having all powers scalable by the gamepad1.left_trigger
     */
    private void carefulDebuggingMode() {
        masterMotorScale = gamepad1.left_trigger;
    }



    public double timeSinceStateStart() {
        return (currTimeMillis - stateStartTime)/1000.0;
    }




}
