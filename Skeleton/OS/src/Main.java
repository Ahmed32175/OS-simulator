public class Main {
    public static void main(String[] args) {
        UserlandProcess init = new UserlandProcess() {
            @Override
            public void main() {
                OS.CreateProcess(new HelloWorld());
                OS.CreateProcess(new GoodbyeWorld());
                while (true) {
                    try {
                        cooperate();
                        Thread.sleep(50);
                    } catch (Exception e) { }
                }
            }
        };
        OS.Startup(init);
    }
}
