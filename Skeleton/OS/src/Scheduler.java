import java.time.Clock;
import java.time.ZoneId;
import java.util.*;
import java.util.Timer;
import java.util.Random;

public class Scheduler  {

    private LinkedList<PCB> realtimeProcesses = new LinkedList<>();
    private LinkedList<PCB> interactiveProcesses =  new LinkedList<>();
    private LinkedList<PCB> backgroundProcesses =  new LinkedList<>();
    private LinkedList<PCB> sleepingProcesses = new LinkedList<>();

    private Timer timer = new Timer();
    public PCB currentRunning;
    Random rand = new Random();
    private Clock clock = Clock.tickMillis(ZoneId.systemDefault());

    private Kernel kernel;

    //every 250 ms, if there is process running call request stop on it
    public Scheduler(Kernel kernel) {
        this.kernel = kernel;
        timer.schedule(new TimerTask() {
            public void run() {
                if (currentRunning != null) {
                    currentRunning.requestStop();
                }
            }
        }, 0, 250);
    }

    public PCB getCurrentRunning(){
        return currentRunning;
    }

    //Create a user land process and add it to list of processes, if there is no processes
    //currently running call switch on it.
    public int CreateProcess(UserlandProcess up, OS.PriorityType p){
        PCB userLandPCB = new PCB(up, p);
        addProcess(userLandPCB);
        if(currentRunning == null){
            SwitchProcess();
        }
        return userLandPCB.pid;
    }
    //If a process is currently running and it is not done add it to end of queue then run next in queue
    public void SwitchProcess(){
//        if(currentRunning != null){System.out.println("PID: " + currentRunning.pid + "    PRIORITY: "+ currentRunning.getPriority());}
//        System.out.println("Sleeping queue size: " + sleepingProcesses.size());

        //increment processes timeout count
        if(currentRunning != null) {
            currentRunning.setTimeoutCount(currentRunning.getTimeoutCount() + 1);
            if (currentRunning.getTimeoutCount() > 5) {//if process has timed out more than 5 times, demote priority
                demotePriority();
            }
        }
        //stop process, if it's not done add it to correct queue.
        if(currentRunning != null && !currentRunning.isDone()){
            addProcess(currentRunning);
        }
        //waking up processes that were sleeping
        Iterator<PCB> iter = sleepingProcesses.iterator();
        while(iter.hasNext()){
            PCB process = iter.next();
            if(process.getWakeUpTime() <= clock.millis()){
                iter.remove();
                addProcess(process);
//                System.out.println("PID: " + process.pid + " has woken up");
            }
        }
        runFromProperQueue();
    }

    public void Sleep(int millis){
        currentRunning.setWakeUpTime(clock.millis() + millis);
        sleepingProcesses.addLast(currentRunning);
//        System.out.println("PID: " + currentRunning.pid +" just went to sleep. Priority is " + currentRunning.getPriority());
        currentRunning.setTimeoutCount(0);
        currentRunning = null; //we make the current process null so that it does not get added into a priority queue when switchProcess() gets called.
        SwitchProcess(); // change current running process
    }

    public void Exit(){
        if(currentRunning.getPriority() == OS.PriorityType.realtime){
            realtimeProcesses.remove(currentRunning);
        }
        else if(currentRunning.getPriority() == OS.PriorityType.interactive){
            interactiveProcesses.remove(currentRunning);
        }
        else{ backgroundProcesses.remove(currentRunning);}
        //close all process devices
        for(int i =0; i<currentRunning.getDeviceArray().length; i++){
            if(currentRunning.getDeviceArray()[i] != -1){
                kernel.Close(i);
            }
        }
        //make sure process never runs again and choose new process to run
        currentRunning = null;
        SwitchProcess();
    }

    private void addProcess(PCB process){
        if(process.getPriority() == OS.PriorityType.realtime){
            realtimeProcesses.add(process);
        }
        else if(process.getPriority() == OS.PriorityType.interactive){
            interactiveProcesses.add(process);
        }
        else{ backgroundProcesses.add(process);}
    }

    private void runFromProperQueue(){
        int prob = rand.nextInt(10);// probability of choosing queue
        //if there is a realtime process 6/10 times we will run it, 3/10 times we run an interactive process,
        // and 1/10 a bg process
        if(!realtimeProcesses.isEmpty()){
            if(prob < 6) {
                currentRunning = realtimeProcesses.pollFirst();
            }
            else if(!interactiveProcesses.isEmpty() && prob < 9){
                currentRunning = interactiveProcesses.pollFirst();
            }
            else {
                currentRunning = backgroundProcesses.pollFirst();
            }
        } else if (!interactiveProcesses.isEmpty()) {
            //otherwise if there is interactive processes 3/4 times we run it, 1/4 we run the bg process
            prob = rand.nextInt(4);
            if(prob < 3){
                currentRunning = interactiveProcesses.pollFirst();
            }
            else if (!backgroundProcesses.isEmpty()){
                currentRunning = backgroundProcesses.pollFirst();
            }
        }
        else {//run bg process if no other types
            currentRunning = backgroundProcesses.pollFirst();
        }

    }

    private void demotePriority(){
//        if(currentRunning.getPriority() != OS.PriorityType.background){System.out.println("PID: " + currentRunning.pid + " WAS DEMOTED!");}
        if(currentRunning.getPriority() == OS.PriorityType.realtime){
            currentRunning.setPriority(OS.PriorityType.interactive);
        }
        else if(currentRunning.getPriority() == OS.PriorityType.interactive){
            currentRunning.setPriority(OS.PriorityType.background);
        }
        currentRunning.setTimeoutCount(0);
    }



}
