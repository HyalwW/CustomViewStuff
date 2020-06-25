package com.example.customviewstuff.threadTest;

public class SolutionA extends Solution {

    public SolutionA() {
        a = new A();
        b = new B();
        c = new C(a, b);
    }

    private static class A extends Basic {
        @Override
        public void run() {
            System.out.println(TAG + "-->start(): " + System.currentTimeMillis());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    private static class B extends Basic {
        @Override
        public void run() {
            System.out.println(TAG + "-->start(): " + System.currentTimeMillis());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    private static class C extends Basic {
        private Basic b1, b2;

        public C(Basic b1, Basic b2) {
            this.b1 = b1;
            this.b2 = b2;
        }

        @Override
        public void run() {
            try {
                b1.join();
                b2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(TAG + "-->start(): ");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }
}
