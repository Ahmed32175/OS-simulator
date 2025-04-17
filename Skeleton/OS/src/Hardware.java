import java.util.Random;

public class Hardware {

    private static int[][] TLB = new int[2][2];
    private static byte[] memory = new byte[1024*1024];

    public static byte Read(int address){
        int pageNumber = address/1024;
        int TLBmapping = searchTLB(pageNumber);
        int physicalAddress;
        if(TLBmapping != -1){
            physicalAddress = (TLBmapping*1024) + (address%1024);
            return memory[physicalAddress];
        }
        else{
            OS.GetMapping(pageNumber);
            physicalAddress = (searchTLB(pageNumber)*1024) + (address%1024);
            return memory[physicalAddress];
        }
    }

    public static void Write(int address, byte value){
        int pageNumber = address/1024;
        int TLBmapping = searchTLB(pageNumber);
        int physicalAddress;
        if(TLBmapping != -1){
            physicalAddress = (TLBmapping*1024) + (address%1024);
            memory[physicalAddress] = value;
        }
        else{
            OS.GetMapping(pageNumber);
            physicalAddress = (searchTLB(pageNumber)*1024) + (address%1024);
            memory[physicalAddress] = value;
        }

    }


    private static int searchTLB(int pn){
        for(int i=0; i<2; i++){
            if(TLB[i][0] == pn){
                return TLB[i][1];
            }
        }
        return -1;
    }

    public static void updateTLB(int vm, int pm){
        Random random = new Random();
        int n = random.nextInt(2);
        TLB[n] = new int[]{vm, pm};
    }

    public static void clearTLB(){
        for(int i=0; i<2; i++){
            TLB[i] = new int[]{-1, -1};
        }
    }



}
