public class VirtualMemoryTest extends UserlandProcess{
    @Override
    public void main() {
        int x= OS.AllocateMemory(1024*1024);
        if (x != -1) {
            System.out.println("memory allocated successfully");
        }
        else{
            System.out.println("memory not allocated");
        }
        byte read = Hardware.Read(0);
        System.out.println("reading from address 0: "+read);
        byte val = 1;
        for(int i =0; i < 1024; i++){
            Hardware.Write(i*1024,val);
        }
        System.out.println("writing 1 to all beginning of all pages");
        System.out.println("reading from address 0: "+ Hardware.Read(0));

        while(true) {
            cooperate();
            try {
                Thread.sleep(250); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
