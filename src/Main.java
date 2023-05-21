import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        /*
        * sequence number의 가용범위는 2^32.
        * sender의 window size는 1024bytes
        * receiver의 window size는 512bytes
        * */

        Control control = new Control();
        control.run();
    }
}