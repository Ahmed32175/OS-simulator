import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kernel extends Process implements Device {
    private Scheduler scheduler;
    private VirtualFileSystem vfs = new VirtualFileSystem();
    private boolean[] pageIsFree = new boolean[1000];
    private int pageIndex = 0;

    public Kernel() {
        this.scheduler = new Scheduler(this);
        Arrays.fill(pageIsFree, true);
    }


    @Override
    public void main() {
            while (true) { // Warning on infinite loop is OK...
                switch (OS.currentCall) { // get a job from OS, do it
                    case CreateProcess ->  // Note how we get parameters from OS and set the return value
                            OS.retVal = CreateProcess((UserlandProcess) OS.parameters.get(0), (OS.PriorityType) OS.parameters.get(1));
                    case SwitchProcess -> SwitchProcess();
                    // Priority Schduler
                    case Sleep -> Sleep((int) OS.parameters.get(0));
                    case GetPID -> OS.retVal = GetPid();
                    case Exit -> Exit();
                    // Devices
                    case Open -> OS.retVal = Open((String) OS.parameters.get(0));
                    case Close -> Close((int) OS.parameters.get(0));
                    case Read -> OS.retVal = Read((int) OS.parameters.get(0), (int) OS.parameters.get(1));
                    case Seek ->  Seek((int) OS.parameters.get(0), (int) OS.parameters.get(1));
                    case Write -> OS.retVal = Write((int) OS.parameters.get(0), (byte[]) OS.parameters.get(1));
                    // Messages
                    case GetPIDByName -> OS.retVal = GetPidByName((String) OS.parameters.get(0));
                    case SendMessage -> SendMessage((KernelMessage) OS.parameters.get(0));
                    case WaitForMessage -> OS.retVal = WaitForMessage();

                    // Memory
                    case GetMapping -> GetMapping((int)OS.parameters.get(0));
                    case AllocateMemory -> OS.retVal = AllocateMemory((int) OS.parameters.get(0));
                    case FreeMemory -> OS.retVal = FreeMemory((int) OS.parameters.get(0), (int) OS.parameters.get(1));
                }
                // TODO: Now that we have done the work asked of us, start some process then go to sleep.
                if(scheduler.currentRunning != null) {
                    scheduler.currentRunning.start();
                }
                this.stop();


            }
    }

    //Accessor for Scheduler used in OS
    public Scheduler getScheduler() {
        return scheduler;
    }

    //Call Schedulers SwitchProcess()
    private void SwitchProcess() {
        scheduler.SwitchProcess();
    }

    // For assignment 1, you can ignore the priority. We will use that in assignment 2
    private int CreateProcess(UserlandProcess up, OS.PriorityType priority) {
        //calls Schedulers create process and returns pid
        int pid = scheduler.CreateProcess(up, priority);
        return pid;
    }

    private void Sleep(int mills) {
        scheduler.Sleep(mills);
    }

    private void Exit() {
        scheduler.Exit();
    }

    private int GetPid() {
        return scheduler.currentRunning.pid;
    }

    public int Open(String s) {
        for(int i =0; i < 10; i++){
            if(scheduler.getCurrentRunning().getDeviceArray()[i] == -1){//if array spot is empty
                int id = vfs.Open(s);
                if(id == -1) {//if open return -1 there was an error
                    return id;
                }
                else{//place id in device array
                    scheduler.getCurrentRunning().getDeviceArray()[i] = id;
                    return i;
                }
            }
        }
        return -1;
    }

    public void Close(int id) {
        vfs.Close(scheduler.getCurrentRunning().getDeviceArray()[id]);
        scheduler.getCurrentRunning().getDeviceArray()[id] = -1;
        //for testing
        System.out.println("Device Array: "+Arrays.toString(scheduler.getCurrentRunning().getDeviceArray())+"\n");
    }

    public byte[] Read(int id, int size) {
        return vfs.Read(scheduler.getCurrentRunning().getDeviceArray()[id], size);
    }

    public void Seek(int id, int to) {
        vfs.Seek(scheduler.getCurrentRunning().getDeviceArray()[id], to);
        //for testing
        System.out.println("Device Array: "+Arrays.toString(scheduler.getCurrentRunning().getDeviceArray())+"\n");
    }

    public int Write(int id, byte[] data) {
        return vfs.Write(scheduler.getCurrentRunning().getDeviceArray()[id], data);
    }

    private void SendMessage(KernelMessage km) {
        scheduler.SendMessage(km);
    }

    private KernelMessage WaitForMessage() {
        return scheduler.WaitForMessage();
    }

    private int GetPidByName(String name) {
        return scheduler.GetPidByName(name);
    }

    private void GetMapping(int virtualPage) {
        //CHANGE TO UPDATE RANDOMLY
        int pm = scheduler.getCurrentRunning().getMappingArray()[virtualPage];
        if(pm != -1) {
            Hardware.updateTLB(virtualPage, pm);
        }
        else{
            System.out.println("seg-fault");
            OS.Exit();
        }
    }

    private int AllocateMemory(int size) {
        int numberOfPages = size/1024;

        List<Integer> freePages = new ArrayList<>();
        for (int i = 0; i < pageIsFree.length && freePages.size() < numberOfPages; i++) {
            if (pageIsFree[i]) {
                freePages.add(i);
            }
        }

        if (freePages.size() < numberOfPages) {
            return -1; // Not enough memory
        }

        int[] map = scheduler.currentRunning.getMappingArray();
        int pagesMapped = 0;
        int firstVirtualPage = -1;

        int i=0;
        while (i <= map.length) {
            boolean memoryFound = true;
                for (int j = i; j < i+ numberOfPages && j < map.length; j++) {
                    if (map[j] != -1) {
                        memoryFound = false;
                        i = j+1;
                        break;
                    }
                }
                if (!memoryFound) {
                    continue;
                }
                System.out.println("memory found at virtual address: " + i);
                for(int k = 0; k < numberOfPages; k++) {
                    int physicalPage = freePages.get(pagesMapped);
                    map[i+k] = physicalPage;
                    System.out.println("memory mapped: " +(i+k) +" to "+ physicalPage);
                    pageIsFree[physicalPage] = false;

                    if (firstVirtualPage == -1) {
                        firstVirtualPage = i;
                    }
                    pagesMapped++;
                }
                break;
        }
        return firstVirtualPage * 1024;
    }

    private boolean FreeMemory(int pointer, int size) {
        int firstPage = pointer/1024;
        int numPages = size/1024;
        int[] map = scheduler.currentRunning.getMappingArray();

        for(int i =0; i < numPages; i++) {
            int physicalPage = map[i+firstPage];
            if(physicalPage != -1) {
                pageIsFree[physicalPage] = true;
                map[i + firstPage] = -1;
            }
        }
        return true;
    }

    public void FreeAllMemory(PCB currentlyRunning) {
        int[] map = currentlyRunning.getMappingArray();
        for(int i =0; i < map.length; i++) {
            int physicalPage = map[i];
            if(physicalPage != -1) {
                pageIsFree[map[i]] = true;
                map[i] = -1;
            }
        }
    }

}