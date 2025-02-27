import java.util.concurrent.Semaphore;

public abstract class Process implements Runnable{

     boolean quantamIsExpired;
     private Thread thread = new Thread(this);
     private Semaphore semaphore = new Semaphore(0, true);

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

    public void stop()  {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
