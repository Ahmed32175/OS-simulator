public class Init extends UserlandProcess {

    @Override
    public void main() {

//        OS.CreateProcess(new GoodbyeWorld(), OS.PriorityType.interactive);
//        OS.CreateProcess(new testProcess(), OS.PriorityType.realtime);
//        OS.CreateProcess(new HelloWorld(), OS.PriorityType.realtime);
//        OS.CreateProcess((new testSleepProcess()), OS.PriorityType.realtime);
          OS.CreateProcess((new Ping()), OS.PriorityType.realtime);
          OS.CreateProcess((new Pong()), OS.PriorityType.realtime);

        OS.Exit();

     }
    }

