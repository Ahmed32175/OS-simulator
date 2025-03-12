import java.util.Random;

public class RandomDevice implements Device {

    private final Random[] deviceArray = new Random[10];

    @Override
    public int Open(String s) {
        Random rand = new Random();
        for(int i =0; i<deviceArray.length; i++) {
            if (deviceArray[i] != null) {
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
    public int Close(int id) {
        deviceArray[id] = null;
        return 0;
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
