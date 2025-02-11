public class PCB { // Process Control Block
    private static int nextPid = 1;
    public int pid;
    private OS.PriorityType priority;

    UserlandProcess up;

    PCB(UserlandProcess up, OS.PriorityType priority) {
        pid = nextPid++;
        this.up = up;
    }

    public String getName() {
        return null;
    }

    OS.PriorityType getPriority() {
        return priority;
    }

    public void requestStop() {
        up.requestStop();
    }

    public void stop() { /* calls userlandprocess’ stop. Loops with Thread.sleep() until
//ulp.isStopped() is true.  */
        up.stop();
//        while(up.isStopped()){
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
    }

    public boolean isDone() { /* calls userlandprocess’ isDone() */
        return up.isDone(); // Change
    }

    void start() { /* calls userlandprocess’ start() */
        up.start();
    }

    public void setPriority(OS.PriorityType newPriority) {
        priority = newPriority;
    }
}
