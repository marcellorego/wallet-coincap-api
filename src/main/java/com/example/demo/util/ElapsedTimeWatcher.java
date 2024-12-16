package com.example.demo.util;

import java.util.concurrent.TimeUnit;

/**
 * The type Elapsed time watcher.
 * This class is used to measure the elapsed time between two points in the code.
 * It is used to measure the time taken to execute a task measured in minutes/seconds.
*/

public class ElapsedTimeWatcher {
    
    long starts;

    private ElapsedTimeWatcher() {
        reset();
    }

    public static ElapsedTimeWatcher start() {
        return new ElapsedTimeWatcher();
    }

    public void reset() {
        starts = System.nanoTime();
    }

    public long elapsedTime() {
        long ends = System.nanoTime();
        return ends - starts;
    }

    public long elapsedTime(TimeUnit unit) {
        return unit.convert(elapsedTime(), TimeUnit.NANOSECONDS);
    }

    public String elapsedTimeInSeconds(){
        long seconds = elapsedTime(TimeUnit.SECONDS);
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format("%d min, %d sec", m, s);
    }
}