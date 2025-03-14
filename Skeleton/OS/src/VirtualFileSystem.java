import java.util.Arrays;
import java.util.Random;

public class VirtualFileSystem implements Device{

    Device[] deviceArray = new Device[10];
    int[] idArray = new int[10];
    RandomDevice rand = new RandomDevice();
    FakeFileSystem fs = new FakeFileSystem();

    @Override
    public int Open(String s) {
        String[] arg = s.split(" ");
        Device device;
        if(arg[0].equals("random")){
            device = rand;
        }
        else if(arg[0].equals("file")){
            device = fs;
        }
        else{
            throw new RuntimeException();
        }
        for(int i = 0; i < 10; i++){
            if(deviceArray[i] == null){
                deviceArray[i] = device;
                idArray[i] =  device.Open(arg[1]);
                return i;
            }
        }
        return -1;
    }

    @Override
    public void Close(int id) {
        deviceArray[id] = null;
        idArray[id] = 0;
    }

    @Override
    public byte[] Read(int id, int size) {
        return deviceArray[id].Read(idArray[id],size);
    }

    @Override
    public void Seek(int id, int to) {
        deviceArray[id].Seek(idArray[id], to);
    }

    @Override
    public int Write(int id, byte[] data) {
        return deviceArray[id].Write(idArray[id],data);
    }
}
