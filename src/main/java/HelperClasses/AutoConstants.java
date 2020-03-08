package HelperClasses;

/**
 * has a bunch of constants for auto moving
 */
public class AutoConstants {


    /**
     * actual block positions on the field (no changes) - block 1 is closest to the wall
     */
    static double block1X = 10;
    static double block2X = 30;
    static double block3X = 51;
    static double block4X = 71;
    static double block5X = 91;
    static double block6X = 112 - 10; //adjusted so it won't hit the wall


    static double blockY = 126;


    /**
     * greatest y position while the robot passes by the wall
     */
    static double highestY = 85;


    /**
     * how much time is waited after loading to quick reverse
     */
    static double waitToReverse = 0.5;










    //getters
    public static double getBlock1X() { return block1X; }
    public static double getBlock2X() { return block2X; }
    public static double getBlock3X() { return block3X; }
    public static double getBlock4X() { return block4X; }
    public static double getBlock5X() { return block5X; }
    public static double getBlock6X() { return block6X; }
    public static double getBlockY() { return blockY; }
    public static double getHighestY() { return highestY; }
    public static double getWaitToReverse() { return waitToReverse; }





}
