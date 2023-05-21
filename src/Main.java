import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        /*
        * sequence number의 가용범위는 2^32.
        * sender의 window size는 1024bytes
        * receiver의 window size는 512bytes
        * */

        // TODO: ReceiverUpperApplication을 Thread로 실행하여 read 동작이 계속해서 일어나도록 할 것.
        Control control = new Control();
        control.run();
    }
}