public class Ping extends UserlandProcess{

    @Override
    public void main() {
        int pongPid = OS.GetPidByName("Pong");
        System.out.println("I am Ping, pong= "+pongPid);
        byte[] message = "PONG".getBytes();
        KernelMessage pingSend = new KernelMessage(OS.GetPID(), pongPid, 0, message);

        while(true){

            try {
                Thread.sleep(250); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
            OS.SendMessage(pingSend);
            KernelMessage km = OS.WaitForMessage();
            System.out.println(km.toString() + " from "+ km.senderPid + " to " + km.targetPid);
            cooperate();
        }
    }
}
