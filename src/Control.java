import Channel.Channel;
import Receiver.*;
import Sender.Sender;
import Sender.SenderUpperApplication;

import java.io.FileNotFoundException;

public class Control {
    private static final SenderUpperApplication senderUpperApplication = SenderUpperApplication.getInstance();
    private static final Sender sender = Sender.getInstance();
    private static final Receiver receiver = Receiver.getInstance();
    private static final Channel channel = Channel.getInstance();


    public void run() throws FileNotFoundException, InterruptedException {
        Thread senderWrite = new Thread() {
            @Override
            public void run() {
                while(true){
                    if (sender.getReceiver() != null){
                        try {
                            sender.writeProcess();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // to execute 'write' action every 0.5 second
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println("close");
                        break;
                    }
                }
            }
        };

        // start to periodically write data in window.
        senderWrite.start();
        // setup connection and start passing the data from scenario file to window.
        String isDone = senderUpperApplication.write(receiver);

        /*
         isDone will be set "close" when the scenario file ends.
         Then, interrupt the thread to stop the write action and finish the program.
         */
        if (isDone == "close") {
            senderWrite.interrupt();
        }
    }
}
