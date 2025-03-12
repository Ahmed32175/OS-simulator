import java.util.Arrays;

public class PCB { // Process Control Block
    private static int nextPid = 1;
    public int pid;
    private OS.PriorityType priority;
    private UserlandProcess up;
    private long wakeUpTime;
    private int timeoutCount;
    private final int[] deviceArray = new int[10];

    PCB(UserlandProcess up, OS.PriorityType priority) {
        pid = nextPid++;
        this.up = up;
        this.priority = priority;
        this.timeoutCount = 0;
        Arrays.fill(deviceArray, -1);
    }

    public int[] getDeviceArray() {
        return deviceArray;
    }

    public int getTimeoutCount() {
        return timeoutCount;
    }

    public void setTimeoutCount(int increment) {
        timeoutCount = increment;
    }

    public long getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(long wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
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
