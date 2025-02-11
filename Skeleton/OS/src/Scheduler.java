import java.util.*;
import java.util.Timer;

public class Scheduler  {

    private LinkedList<PCB> processes;
    private Timer timer;
    public PCB currentRunning;

    //every 250 ms, if there is process running call request stop on it
    public Scheduler() {
        this.processes = new LinkedList<>();
        this.timer = new Timer();
        this.currentRunning = null;

        timer.schedule(new TimerTask() {
            public void run() {
                if (currentRunning != null) {
                    currentRunning.requestStop();
                }
            }
        }, 0, 250);
    }

    //Create a user land process and add it to list of processes, if there is no processes
    //currently running call switch on it.
    public int CreateProcess(UserlandProcess up, OS.PriorityType p){
        PCB userLandPCB = new PCB(up, p);
        processes.add(userLandPCB);
        if(currentRunning == null){
            SwitchProcess();
        }
        return userLandPCB.pid;
    }
    //If a process is currently running and it is not done add it to end of queue then run next in queue
    public void SwitchProcess(){
        if(currentRunning != null && !currentRunning.isDone()){
            processes.addLast(currentRunning);
        }
        currentRunning = processes.pollFirst();
    }


}
