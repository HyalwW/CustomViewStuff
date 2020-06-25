package com.example.customviewstuff.threadTest;

import java.util.concurrent.CountDownLatch;

public class SolutionC extends Solution {
    private CountDownLatch latch;

    public SolutionC() {
        latch = new CountDownLatch(2);
        a = new A(latch);
        b = new B(latch);
        c = new C(latch);
    }

    private static class A extends Basic {
        private CountDownLatch latch;

        public A(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            System.out.println(TAG + "-->start(): " + System.currentTimeMillis());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            super.run();
        }
    }

    private static class B extends Basic {
        private CountDownLatch latch;

        public B(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            System.out.println(TAG + "-->start(): " + System.currentTimeMillis());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            super.run();
        }
    }

    private static class C extends Basic {
        private CountDownLatch latch;

        public C(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                latch.await();
                System.out.println(TAG + "-->start(): ");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
