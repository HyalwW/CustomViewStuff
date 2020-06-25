package com.example.customviewstuff.threadTest;

public class SolutionB extends Solution{
    private static final Object lock = new Object();

    public SolutionB() {
        a = new A(lock);
        b = new B(lock);
        c = new C(lock);
    }

    private static class A extends Basic {
        private final Object lock;

        public A(Object o) {
            this.lock = o;
        }

        @Override
        public void run() {
            synchronized (lock) {
                System.out.println(TAG + "-->start(): " + System.currentTimeMillis());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.notify();
                super.run();
            }
        }
    }

    private static class B extends Basic {

        private final Object lock;

        public B(Object o) {
            this.lock = o;
        }

        @Override
        public void run() {
            synchronized (lock) {
                System.out.println(TAG + "-->start(): " + System.currentTimeMillis());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.notify();
                super.run();
            }
        }
    }

    private static class C extends Basic {
        private final Object lock;

        public C(Object o) {
            this.lock = o;
        }

        @Override
        public void run() {
            synchronized (lock) {
                try {
                    lock.wait();
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
}
