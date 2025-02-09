import java.util.concurrent.Semaphore;

public abstract class Process implements Runnable{

    boolean quantamIsExpired;
    Thread thread = new Thread(this);
    Semaphore semaphore = new Semaphore(0);

    public Process() {
        quantamIsExpired = false;
        thread.start();
    }

    public void requestStop() {
        quantamIsExpired = true;
    }

    public abstract void main();

    public boolean isStopped() {
        return semaphore.availablePermits() == 0;
    }

    public boolean isDone() {
        return !thread.isAlive();
    }

    public void start() {
        semaphore.release();
    }

    public void stop() throws InterruptedException {
        semaphore.acquire();
    }

    public void run() {
        semaphore.acquireUninterruptibly();
        main();
        // This is called by the Thread - NEVER CALL THIS!!!
    }

    public void cooperate() {
        if(quantamIsExpired) {
            quantamIsExpired = false;
            OS.switchProcess();
        }
    }
}
