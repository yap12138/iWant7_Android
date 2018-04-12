package com.yaphets.iwant7.service;

/**
 * Created by Yaphets on 2017/11/3.
 */
public class GameThread extends Thread {
    private static int gameThreadCount = 0;

    public GameThread(Runnable target) {
        super(target);
        this.setName("gameThread" + gameThreadCount++);
        //System.out.println("Game Thread Priority: "+this.getPriority());
    }

    public GameThread(int Priority, Runnable target) {
        this(target);
        this.setPriority(Priority);
    }

}
