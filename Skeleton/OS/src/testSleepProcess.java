public class testSleepProcess extends UserlandProcess{
    @Override
    public void main() {
        while(true){
            System.out.println(OS.GetPID() + " is running but will sleep soon");
            OS.Sleep(1000);
            //cooperate();
        }
    }
}
