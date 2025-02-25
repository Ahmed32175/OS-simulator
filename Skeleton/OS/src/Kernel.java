public class Kernel extends Process  {
    public Kernel() {
    }

    private Scheduler scheduler = new Scheduler();

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
                    /*
                    // Devices
                    case Open ->
                    case Close ->
                    case Read ->
                    case Seek ->
                    case Write ->
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
        return 0; // change this
    }

    private void Close(int id) {
    }

    private byte[] Read(int id, int size) {
        return null; // change this
    }

    private void Seek(int id, int to) {
    }

    private int Write(int id, byte[] data) {
        return 0; // change this
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