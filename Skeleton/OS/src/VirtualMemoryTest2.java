public class VirtualMemoryTest2 extends UserlandProcess{
    @Override
    public void main() {
        System.out.println("ALLOCATE PHYSICAL MEMORY FROM DIFFERENT PROCESS");
        int x= OS.AllocateMemory(1024*1024);
        if (x != -1) {
            System.out.println("memory allocated successfully");
        }
        else{
            System.out.println("memory not allocated");
        }
        System.out.println("TRYING TO ACCESS USED AT PAGE 1 ADDRESS 0, MEMORY PAGESWAPPING MUST OCCUR");
        byte read = Hardware.Read(0);
        System.out.println("reading from address 0, which was had previous value 1: "+read);
        OS.Exit();
    }
}
