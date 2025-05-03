import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kernel extends Process implements Device {
    private Scheduler scheduler;

    public VirtualFileSystem getVfs() {
        return vfs;
    }

    private VirtualFileSystem vfs = new VirtualFileSystem();
    private boolean[] pageIsFree = new boolean[1024];
    private int pageNumber = 0;
    public int swapfile;

    public Kernel() {
        this.scheduler = new Scheduler(this);
        Arrays.fill(pageIsFree, true);
        this.swapfile = vfs.Open("file swapfile.txt");
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
        VirtualToPhysicalMapping pm = scheduler.getCurrentRunning().getMappingArray()[virtualPage];
        //if trying to access memory that was never allocated
        if (pm == null) {
            System.out.println("seg-fault");
            OS.Exit();
            return;
        }
        //if need to map virtual memory to physical memory
        if(pm.physicalPN == -1) {
            for(int i =0; i < pageIsFree.length; i++){
                if(pageIsFree[i]){
                    pageIsFree[i] = false;
                    pm.physicalPN = i;
                    Hardware.updateTLB(virtualPage, pm.physicalPN);
                    if(pm.diskPN != -1){
                        //load old data and populate physical page
                        byte [] buffer = vfs.Read(swapfile, 1024);
                        for(int j =0; j<1024; j++){
                            Hardware.getMemory()[(pm.physicalPN * 1024) + j] = buffer[j];
                        }
                    }
                    else{
                        //populate memory with 0's
                        for(int j =0; j<1024; j++){
                            Hardware.getMemory()[(pm.physicalPN * 1024) + j] = 0;
                        }
                    }
                    return;
                }
            }
            //if no physical page available, do page swap:
            while(true) {
                System.out.println("NO PHYSICAL PAGE AVAILABLE, PAGES SWAPPING");
                PCB victimProcess = scheduler.getRandomProcess();
                for (VirtualToPhysicalMapping m : victimProcess.getMappingArray()) {
                    if (m != null && m.physicalPN != -1) {
                        System.out.println("WRITING VICTIM TO DISK");
                        //Write the victim page to disk,
                        // Read victim's physical page content into byte array
                        byte[] buffer = new byte[1024];
                        byte[] memory = Hardware.getMemory();
                        for (int j = 0; j < 1024; j++) {
                            buffer[j] = memory[(m.physicalPN * 1024) + j];
                        }
//                        // Write page to swap file
                        System.out.println("WRITING VICTIM TO SWAPFILE");
                        vfs.Write(swapfile, buffer);

                        //assign new block of the swap file if none already
                        if (m.diskPN == -1) {
                            m.diskPN = pageNumber++;
                        }
                        //Set victim’s physical page to -1 and currs to old val.
                        pm.physicalPN = m.physicalPN;
                        m.physicalPN = -1;
                        Hardware.updateTLB(virtualPage, pm.physicalPN);
                        if(pm.diskPN != -1){
                            //load old data and populate physical page
                            byte [] buff = vfs.Read(swapfile, 1024);
                            for(int j =0; j<1024; j++){
                                Hardware.getMemory()[(pm.physicalPN * 1024) + j] = buff[j];
                            }
                        }
                        else{
                            //populate memory with 0's
                            for(int j =0; j<1024; j++){
                                Hardware.getMemory()[(pm.physicalPN * 1024) + j] = 0;
                            }
                        }
                        return;
                    }
                }
            }
        }
        //mapping already exists, update TLB
        else{
            Hardware.updateTLB(virtualPage, pm.physicalPN);
        }
    }

    private int AllocateMemory(int size) {
        int numberOfPages = size/1024;
        VirtualToPhysicalMapping[] map = scheduler.currentRunning.getMappingArray();
        int firstVirtualPage = -1;

        for(int i =0; i <= map.length-numberOfPages; i++){
            boolean freeBlock = true;
            for(int j=i; j < i + numberOfPages; j++){
                if(map[j] != null){
                    freeBlock = false;
                    i=j;
                    break;
                }
            }

            if (freeBlock) {
                firstVirtualPage = i;
                for (int k = i; k < i + numberOfPages; k++) {
                    map[k] = new VirtualToPhysicalMapping();
                }
                break;
            }
        }
        return firstVirtualPage;
    }

    private boolean FreeMemory(int pointer, int size) {
        int firstPage = pointer/1024;
        int numPages = size/1024;
        VirtualToPhysicalMapping[] map = scheduler.currentRunning.getMappingArray();

        for(int i =0; i < numPages; i++) {
            int physicalPage = map[i+firstPage].physicalPN;
            if(physicalPage != -1) {
                pageIsFree[physicalPage] = true;
                map[i+ firstPage] = null;
            }
        }
        return true;
    }

    public void FreeAllMemory(PCB currentlyRunning) {
        VirtualToPhysicalMapping[] map = currentlyRunning.getMappingArray();

        for(int i =0; i < map.length; i++) {
            if(map[i] != null) {
                int physicalPage = map[i].physicalPN;
                if (physicalPage != -1) {
                    pageIsFree[map[i].physicalPN] = true;
                    map[i] = null;
                }
            }
        }
    }

}