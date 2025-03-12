public class Kernel extends Process   {
    public Kernel() {
    }

    private Scheduler scheduler = new Scheduler();
    private VirtualFileSystem vfs = new VirtualFileSystem();

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
                        /*
                    // Messages
                    case GetPIDByName ->
                    case SendMessage ->
                    case WaitForMessage ->
                    // Memory
                    case GetMapping ->
                    case AllocateMemory ->
                    case FreeMemory ->
                     */
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

    private int Open(String s) {
        for(int i =0; i < 10; i++){
            if(scheduler.getCurrentRunning().getDeviceArray()[i] == -1){
                int id = vfs.Open(s);
                if(id == -1) {
                    return id;
                }
                else{
                    scheduler.getCurrentRunning().getDeviceArray()[i] = id;
                    return i;
                }
            }
        }
        return -1; // change this
    }

    private void Close(int id) {
        vfs.Close(scheduler.getCurrentRunning().getDeviceArray()[id]);
        scheduler.getCurrentRunning().getDeviceArray()[id] = -1;
    }

    private byte[] Read(int id, int size) {
        return vfs.Read(scheduler.getCurrentRunning().getDeviceArray()[id], size);
    }

    private void Seek(int id, int to) {
        vfs.Seek(scheduler.getCurrentRunning().getDeviceArray()[id], to);
    }

    private int Write(int id, byte[] data) {
        return vfs.Write(scheduler.getCurrentRunning().getDeviceArray()[id], data);
    }

    private void SendMessage(/*KernelMessage km*/) {
    }

    private KernelMessage WaitForMessage() {
        return null;
    }

    private int GetPidByName(String name) {
        return 0; // change this
    }

    private void GetMapping(int virtualPage) {
    }

    private int AllocateMemory(int size) {
        return 0; // change this
    }

    private boolean FreeMemory(int pointer, int size) {
        return true;
    }

    private void FreeAllMemory(PCB currentlyRunning) {
    }

}