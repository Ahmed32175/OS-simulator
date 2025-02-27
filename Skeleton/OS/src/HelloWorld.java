public class HelloWorld extends UserlandProcess{
    @Override
    public void main() {
        while(true){
            cooperate();
            try {
                Thread.sleep(50); // sleep for 50 ms
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println("Hello World");

        }
    }
}
