import java.util.Arrays;
import java.util.LinkedList;

public class PCB { // Process Control Block
    private static int nextPid = 1;
    public int pid;
    private OS.PriorityType priority;
    private UserlandProcess up;
    private long wakeUpTime;
    private int timeoutCount;
    private final int[] deviceArray = new int[10];
    private String proccessName = "";
    private boolean isWaiting = false;
    private LinkedList<KernelMessage> messagesQueue = new LinkedList<>();
    private VirtualToPhysicalMapping[] mappingArray;

    public VirtualToPhysicalMapping[] getMappingArray() {
        return mappingArray;
    }



    PCB(UserlandProcess up, OS.PriorityType priority) {
        pid = nextPid++;
        this.up = up;
        this.priority = priority;
        this.timeoutCount = 0;
        Arrays.fill(deviceArray, -1);
        this.proccessName = up.getClass().getSimpleName();
        this.mappingArray = new VirtualToPhysicalMapping[1024];
        Arrays.fill(mappingArray, null);
    }

    public LinkedList<KernelMessage> getMessagesQueue() {
        return messagesQueue;
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
        return proccessName;
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

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }
}
