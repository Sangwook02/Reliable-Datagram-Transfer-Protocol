import Channel.Channel;
import Receiver.*;
import Sender.SenderTransport;
import Sender.SenderUpperApplication;

import java.io.FileNotFoundException;

public class Control {
    private static final SenderUpperApplication senderUpperApplication = SenderUpperApplication.getInstance();
    private static final SenderTransport senderTransport = SenderTransport.getInstance();
    private static final ReceiverTransport receiverTransport = ReceiverTransport.getInstance();
    private static final Channel channel = Channel.getInstance();

    public void run() throws FileNotFoundException {
        Thread senderWrite = new Thread() {
            @Override
            public void run() {
                receiverTransport.setChannel(channel);
                while(true){
                    if (senderTransport.getReceiver() != null){
                        try {
                            senderTransport.checkTimeOut();
                            senderTransport.writeProcess();
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
        String isDone = senderUpperApplication.write(receiverTransport);
        /*
         isDone will be set "close" when the scenario file ends.
         Then, interrupt the thread to stop the write action and finish the program.
         */
        if (isDone == "close") {
            senderWrite.interrupt();
        }
    }
}
