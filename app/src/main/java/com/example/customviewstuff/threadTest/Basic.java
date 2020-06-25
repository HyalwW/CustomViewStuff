package com.example.customviewstuff.threadTest;

public class Basic extends Thread {
    protected String TAG = getClass().getSimpleName();

    @Override
    public void run() {
        super.run();
        System.out.println(TAG + "-->end(): " + System.currentTimeMillis());
    }
}
