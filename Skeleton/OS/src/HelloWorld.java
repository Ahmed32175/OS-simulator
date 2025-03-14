import java.util.Arrays;

public class HelloWorld extends UserlandProcess{
    @Override
    public void main() {
        int id = OS.Open("random 10");
        System.out.println("Another random device was created by a different process with ID: "+id);
        OS.Seek(id, 10);
        byte[] b = OS.Read(id, 10);
        System.out.println("calling Read on device "+ id);
        System.out.println(Arrays.toString(b));
        System.out.println("Closing device with ID: "+id);
        OS.Close(id);
        System.out.println("All devices with this process closed"+"\n");
        while(true) {
            cooperate();
            try {
                Thread.sleep(50); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            //System.out.println("Hello World");


    }
    }

