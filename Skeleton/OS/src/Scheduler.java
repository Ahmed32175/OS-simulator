import java.util.*;
import java.util.Timer;

public class Scheduler  {

    private LinkedList<PCB> processes;
    private Timer timer;
    public static PCB currentPCB;

    public Scheduler() {
        this.processes = new LinkedList<>();
        this.timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                if (currentPCB != null) {
                    currentPCB.requestStop();
                }
                //SwitchProcess();
            }
        }, 0, 250);
    }

    public int CreateProcess(UserlandProcess up, OS.PriorityType p){
        PCB userLandPCB = new PCB(up, p);
        processes.add(userLandPCB);
        if(currentPCB != null){
            SwitchProcess();
        }

        return currentPCB.pid;
    }
    public void SwitchProcess(){
        if(!processes.isEmpty() && !currentPCB.isDone()){
            processes.addLast(currentPCB);
        }
        currentPCB = processes.pollFirst();
    }

}
