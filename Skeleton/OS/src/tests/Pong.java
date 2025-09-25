public class Pong extends UserlandProcess {

    @Override
    public void main() {
        int pingPid = OS.GetPidByName("Ping");
        System.out.println("I am Pong, ping= "+ pingPid);
        byte[] message = "PING".getBytes();
        KernelMessage pongSend = new KernelMessage(OS.GetPID(), pingPid, 1, message);

        while(true){
            try {
                Thread.sleep(250); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
            KernelMessage km = OS.WaitForMessage();
            OS.SendMessage(pongSend);
            System.out.println(km.toString() + " from "+ km.senderPid + " to " + km.targetPid);
            cooperate();

        }
    }
}
