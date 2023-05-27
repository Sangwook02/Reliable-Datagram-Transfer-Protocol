import Channel.Channel;
import Receiver.*;
import Sender.SenderMachine;
import Sender.SenderTransport;
import Sender.SenderUpperApplication;

import java.io.FileNotFoundException;

public class Control {
    private static final SenderMachine senderMachine = SenderMachine.getInstance();
    private static final ReceiverTransport receiverTransport = ReceiverTransport.getInstance();
    private static final Channel channel = Channel.getInstance();


    public void run() throws FileNotFoundException {
        // setReceiver's channel
        receiverTransport.setChannel(channel);
        senderMachine.setChannel(channel);
        senderMachine.write(receiverTransport);
    }
}
