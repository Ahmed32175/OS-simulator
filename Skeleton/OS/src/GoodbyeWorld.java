public class GoodbyeWorld extends UserlandProcess{
    @Override
    public void main() {
        while(true){
            try {
                Thread.sleep(50); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Goodbye World");
            cooperate();
        }
    }
}
