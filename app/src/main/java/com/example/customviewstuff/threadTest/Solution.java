package com.example.customviewstuff.threadTest;

public abstract class Solution extends Thread {
    protected Basic a, b, c;

    public Solution() {
        System.out.println(getClass().getSimpleName());
    }


    @Override
    public void run() {
        super.run();
        a.start();
        b.start();
        c.start();
    }


}
