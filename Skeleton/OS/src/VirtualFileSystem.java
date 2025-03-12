public class VirtualFileSystem implements Device{

    Device[] deviceArray = new Device[10];
    int[] idArray = new int[10];

    @Override
    public int Open(String s) {
        String[] arg = s.split(" ");
        Device device = null;
        if(arg[0].equals("random")){
            device = new RandomDevice();
        }
        else if(arg[0].equals("file")){
            device = new FakeFileSystem();
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
    public int Close(int id) {
        deviceArray[id] = null;
        idArray[id] = 0;
        return 0;
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
