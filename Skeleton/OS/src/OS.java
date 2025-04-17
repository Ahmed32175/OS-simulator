import java.util.ArrayList;
import java.util.List;

public class OS {
    private static Kernel ki; // The one and only one instance of the kernel.

    public static List<Object> parameters = new ArrayList<>();
    public static Object retVal;

    public enum CallType {SwitchProcess,SendMessage, Open, Close, Read, Seek, Write, GetMapping, CreateProcess, Sleep, GetPID, AllocateMemory, FreeMemory, GetPIDByName, WaitForMessage, Exit}
    public static CallType currentCall;

    //Start the Kernel and if there is a process currently running call stop on it.
    //Sleep until return value is not null for startup.
    private static void startTheKernel() {
        ki.start();
        if(ki.getScheduler().currentRunning != null) {
            (ki.getScheduler().currentRunning).stop();
         }
        while (retVal == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void switchProcess() {
        parameters.clear();
        currentCall = CallType.SwitchProcess;
        startTheKernel();
    }

    public static void Startup(UserlandProcess init) {
            ki = new Kernel();
            CreateProcess(init, PriorityType.interactive);
            CreateProcess(new IdleProcess(), PriorityType.background);
    }

    public enum PriorityType {realtime, interactive, background}
    public static int CreateProcess(UserlandProcess up) {
        return  CreateProcess(up,PriorityType.interactive);
    }

    // For assignment 1, you can ignore the priority. We will use that in assignment 2
    public static int CreateProcess(UserlandProcess up, PriorityType priority) {
        parameters.clear();
        parameters.add(up);
        parameters.add(priority);
        currentCall = CallType.CreateProcess;
        startTheKernel();
        return (int) retVal;
    }

    public static int GetPID() {
        parameters.clear();
        currentCall = CallType.GetPID;
        startTheKernel();
        return (int) retVal;
    }

    public static void Exit() {
        parameters.clear();
        currentCall = CallType.Exit;
        startTheKernel();
    }

    public static void Sleep(int mills) {
        parameters.clear();
        parameters.add(mills);
        currentCall = CallType.Sleep;
        startTheKernel();
    }

    // Devices
    public static int Open(String s) {
        parameters.clear();
        parameters.add(s);
        currentCall = CallType.Open;
        startTheKernel();
        return (int) retVal;
    }

    public static void Close(int id) {
        parameters.clear();
        parameters.add(id);
        currentCall = CallType.Close;
        startTheKernel();
    }

    public static byte[] Read(int id, int size) {
        parameters.clear();
        parameters.add(id);
        parameters.add(size);
        currentCall = CallType.Read;
        startTheKernel();
        return (byte[]) retVal;
    }

    public static void Seek(int id, int to) {
        parameters.clear();
        parameters.add(id);
        parameters.add(to);
        currentCall = CallType.Seek;
        startTheKernel();
    }

    public static int Write(int id, byte[] data) {
        parameters.clear();
        parameters.add(id);
        parameters.add(data);
        currentCall = CallType.Write;
        startTheKernel();
        return (int) retVal;
    }

    // Messages
    public static void SendMessage(KernelMessage km) {
        parameters.clear();
        parameters.add(km);
        currentCall = CallType.SendMessage;
        startTheKernel();
    }

    public static KernelMessage WaitForMessage() {
        parameters.clear();
        currentCall = CallType.WaitForMessage;
        startTheKernel();
        return (KernelMessage) retVal;
    }

    public static int GetPidByName(String name) {
        parameters.clear();
        parameters.add(name);
        currentCall = CallType.GetPIDByName;
        startTheKernel();
        return (int) retVal;
    }

    // Memory
    public static void GetMapping(int virtualPage) {
        parameters.clear();
        parameters.add(virtualPage);
        currentCall = CallType.GetMapping;
        startTheKernel();
    }

    public static int AllocateMemory(int size) {
        if(size%1024 != 0 ){
            return -1;
        }
        parameters.clear();
        parameters.add(size);
        currentCall = CallType.AllocateMemory;
        startTheKernel();
        return (int) retVal;
    }

    public static boolean FreeMemory(int pointer, int size) {
        if(size%1024 != 0 && pointer%1024 != 0){
            return false;
        }
        parameters.clear();
        parameters.add(pointer);
        parameters.add(size);
        currentCall = CallType.FreeMemory;
        startTheKernel();
        return (boolean) retVal;
    }
}
