import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device{

    java.io.RandomAccessFile[] deviceArray = new java.io.RandomAccessFile[10];

    @Override
    public int Open(String s) {
        if(s == null || s.isEmpty()){
            throw new RuntimeException();
        }
        for(int i =0; i<deviceArray.length; i++) {
            if(deviceArray[i] == null){
                try {
                    deviceArray[i] =  new RandomAccessFile(s, "rw");
                    return i;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return -1;
    }

    @Override
    public int Close(int id) {
        try {
            deviceArray[id].close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        deviceArray[id] = null;
        return 0;
    }

    @Override
    public byte[] Read(int id, int size) {
        byte[] b = new byte[size];
        try {
            deviceArray[id].read(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return b;
    }

    @Override
    public void Seek(int id, int to) {
        try {
            deviceArray[id].seek(to);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int Write(int id, byte[] data) {
        try {
            deviceArray[id].write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data.length;
    }
}
