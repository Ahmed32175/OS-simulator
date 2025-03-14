import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class testProcess extends UserlandProcess {
    @Override
    public void main() {
        int id = OS.Open("random 100");
        System.out.println("A random device was created with ID: "+id);
        OS.Seek(id, 100);

        int id1 = OS.Open("random 100");
        System.out.println("A random device was created with ID: "+id1);
        OS.Seek(id1, 100);

        int id3 = OS.Open("file doc.txt");
        System.out.println("A file device was created with ID: "+id3);
        OS.Seek(id3, 0);
        byte[] b1 = OS.Read(id3, 32);
        byte[] b2 = ("I wrote hello world to this file").getBytes();
        OS.Seek(id3, 0);
        OS.Write(id3, b2);
        System.out.println("Writing \"I wrote hello world to this file\" to file with ID: "+id3);
        System.out.println("Calling Read on file with ID:"+id3);
        String s = new String(b1, StandardCharsets.UTF_8);
        System.out.println(s+"\n");


        System.out.println("Closing device with ID: "+id);
        OS.Close(id);
        System.out.println("Closing device with ID: "+id1);
        OS.Close(id1);
        System.out.println("Closing device with ID: "+id3);
        OS.Close(id3);
        System.out.println("All devices with this process closed\n");
        while(true){
            cooperate();
            try {
                Thread.sleep(100); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println(OS.GetPID() + " has priority");

        }
    }
}
