public class KernelMessage {

    int senderPid;
    int targetPid;
    int messageType;
    byte[] data;

    public KernelMessage(int senderPid, int targetPid, int messageType, byte[] data) {
        this.senderPid = senderPid;
        this.targetPid = targetPid;
        this.messageType = messageType;
        this.data = data;
    }

    public KernelMessage(KernelMessage km) {
        this.senderPid = km.senderPid;
        this.targetPid = km.targetPid;
        this.messageType = km.messageType;
        this.data = km.data;
    }

    @Override
    public String toString() {
        return new String(data);
    }
}
