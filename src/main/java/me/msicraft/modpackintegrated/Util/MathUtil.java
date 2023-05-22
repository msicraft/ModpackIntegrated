package me.msicraft.modpackintegrated.Util;

public class MathUtil {

    public static double getRandomValueDouble(double max, double min) {
        double randomValue = (Math.random() * (max - min)) + min;
        return (Math.floor(randomValue * 100) / 100.0);
    }

    public static int getRandomValueInt(int max, int min) {
        return (int) ((Math.random() * (max - min) + 1) + min);
    }

}
