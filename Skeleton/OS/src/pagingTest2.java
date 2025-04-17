public class pagingTest2 extends UserlandProcess{

    @Override
    public void main() {
        System.out.println("switching process");
        int x= OS.AllocateMemory(2048);
        if (x != -1) {
            System.out.println("memory allocated at virtual address: "+x);
        }
        else{
            System.out.println("memory not allocated");
        }
        int y = OS.AllocateMemory(4096);
        if (y != -1) {
            System.out.println("memory allocated at virtual address: "+y);
        }
        else{
            System.out.println("memory not allocated");
        }

        byte read = Hardware.Read(0);
        System.out.println("reading from address 0: "+read);
        byte val = 26;
        Hardware.Write(0,val);
        System.out.println("writing to address 0: "+val);
        System.out.println("Read after writing: "+ Hardware.Read(0));

        boolean f = OS.FreeMemory(0, 2048);
        System.out.println("free memory at address 0 ");
        if (f) {
            System.out.println("memory freed successfully");
        }
        else{
            System.out.println("memory not freed");
        }

        System.out.println("Allocating memory at previously freed address 0");
        int z= OS.AllocateMemory(2048);
        if (z != -1) {
            System.out.println("memory allocated at virtual address: "+z);
        }
        else{
            System.out.println("memory not allocated");
        }

        System.out.println("Trying to write read from unmmaped memory");
        OS.GetMapping(25);
        cooperate();
    }
}
