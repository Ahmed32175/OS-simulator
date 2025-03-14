import java.util.Arrays;
import java.util.Random;

public class RandomDevice implements Device {

    private final Random[] deviceArray = new Random[10];

    @Override
    public int Open(String s) {
        for(int i =0; i<deviceArray.length; i++) {
            if (deviceArray[i] == null) {
                Random rand = new Random();
                if(s != null && !s.isEmpty()){
                    int seed = Integer.parseInt(s);
                    rand.setSeed(seed);
                }
                deviceArray[i] = rand;
                return i;
            }
        }
        return -1;
    }

    @Override
    public void Close(int id) {
        deviceArray[id] = null;
    }

    @Override
    public byte[] Read(int id, int size) {
        byte[] temp = new byte[size];
        for(int i =0; i<size; i++) {
            temp[i] = (byte) deviceArray[id].nextInt();
        }
        return temp;
    }

    @Override
    public void Seek(int id, int to) {
        for (int i = 0; i < to; i++) {
            deviceArray[id].nextInt();
        }
    }

    @Override
    public int Write(int id, byte[] data) {
        return 0;
    }
}
