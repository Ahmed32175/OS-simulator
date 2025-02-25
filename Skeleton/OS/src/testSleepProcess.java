public class testSleepProcess extends UserlandProcess{
    @Override
    public void main() {
        while(true){
//            System.out.println("Sleeping for 10 seconds");
            OS.Sleep(50);
            //cooperate();
        }
    }
}
