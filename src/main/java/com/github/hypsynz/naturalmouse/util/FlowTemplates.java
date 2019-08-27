package com.github.hypsynz.naturalmouse.util;

import java.util.Arrays;
import java.util.Random;

public class FlowTemplates {
    public static double[] variatingFlow() {
        return new double[]{
                11, 14, 15, 18, 15, 12, 13, 21, 55, 87, 96, 96, 67, 52, 50, 66, 91, 95, 87, 96, 98,
                88, 70, 62, 57, 65, 79, 93, 98, 97, 100, 61, 51, 64, 107, 53, 44, 30, 21, 26, 21,
                17, 16, 12, 8, 1, 6, 9, 6, 3, 5, 12, 11, 15, 11, 9, 9, 7, 5, 4, 1, 2, 3, 2, 2, 11,
        };
    }

    public static double[] interruptedFlow() {
        return new double[]{
                12, 10, 10, 20, 24, 19, 26, 15, 9, 9, 10, 24, 26, 30, 24, 49, 72, 60, 81, 113, 82,
                99, 67, 11, 7, 7, 7, 10, 8, 7, 9, 0, 6, 7, 10, 11, 12, 8, 7, 3, 5, 2,
                40, 36, 58, 68, 59, 62, 51, 87, 71, 86, 66, 115, 97, 80, 65, 50, 66, 57, 24, 11, 11,
        };
    }

    public static double[] randomFlowLow(){

        double[] s = new double[45];
        for(int i = 0; i < 45; i++) {

            s[i] = randomDouble(1, 50);
        }

        return s;
    }


    public static double[] randomFlowLowest(){

        double[] s = new double[45];
        for(int i = 0; i < 45; i++) {

            s[i] = randomDouble(1, 25);
        }

        return s;
    }

    public static double[] randomFlowHigh(){

        double[] s = new double[45];
        for(int i = 0; i < 45; i++) {
            s[i] = randomDouble(50, 90);
        }

        return s;
    }

    public static double[] randomFlowWide(){

        double[] s = new double[45];
        for(int i = 0; i < 45; i++) {
            s[i] = randomDouble(1, 110);
        }

        return s;
    }

    static double randomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public static double[] interruptedFlow2() {
        return new double[]{
                12, 11, 10, 20, 24, 19, 26, 15, 9, 9, 10, 24, 26, 30, 24, 49, 72, 60, 81, 113, 82,
                99, 67, 10, 12, 8, 11, 15, 16, 17, 17, 12, 16, 37, 10, 25, 12, 11, 41, 10, 12, 11,
                40, 36, 52, 61, 60, 64, 51, 82, 71, 81, 66, 105, 92, 59, 65, 51, 66, 54, 21, 21, 12,
        };
    }


    public static double[] slowStartupFlow() {
        return new double[]{
                8, 5, 2, 2, 2, 3, 3, 3, 4, 4, 5, 9, 9, 10, 11, 11, 11, 11, 12, 12, 13,
                15, 14, 13, 15, 15, 17, 17, 18, 18, 20, 19, 20, 20, 19, 20, 19, 20, 21, 22, 20, 17,
                19, 17, 21, 21, 17, 19, 18, 20, 18, 19, 42, 42, 38, 40, 40, 37, 36, 42, 86, 70, 52, 31, 19,
        };
    }


    public static double[] slowStartup2Flow() {
        return new double[]{
                7, 2, 1, 2, 2, 3, 5, 9, 10, 10, 11, 13, 13, 10, 4, 1, 1, 2, 3, 4, 6,
                9, 11, 11, 10, 14, 11, 9, 2, 1, 2, 2, 3, 4, 8, 9, 10, 11, 11, 13, 13, 15,
                21, 18, 19, 25, 37, 37, 48, 81, 108, 91, 88, 74, 46, 19, 46, 84, 35, 14,
        };
    }

    public static double[] jaggedFlow() {
        return new double[]{
                69, 5, 118, 4, 70, 106, 110, 122, 86, 64, 101, 46, 27, 124, 19, 31, 96, 22, 35, 76,
                113, 55, 67, 18, 32, 85, 62, 61, 57, 8, 73, 41, 84, 112, 95, 15, 40, 6, 2, 12, 7,
                36, 9, 56, 102, 1, 13, 89, 97, 100, 34, 63, 93, 117, 108, 59, 72, 79, 88, 77, 28,
        };
    }

    public static double[] stoppingFlow() {
        return new double[]{
                8, 20, 39, 48, 66, 71, 79, 57, 29, 5, 2, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 6, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                35, 55, 95, 102, 97, 82, 86, 72, 65, 50, 57, 52, 61, 46, 38, 16,
        };
    }

    public static double[] random() {
        double[] result = new double[45];
        for (int i = 0; i < result.length; i++) {
            result[i] = (int) (Math.random() * 100);
        }
        return result;
    }

    public static double[] constantSpeed() {
        double[] flowBuckets = new double[10];
        Arrays.fill(flowBuckets, 100);
        return flowBuckets;
    }
}
