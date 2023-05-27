import Channel.Channel;
import Receiver.ReceiverMachine;
import Sender.SenderMachine;

import java.io.FileNotFoundException;

public class Control {
    private static final SenderMachine senderMachine = SenderMachine.getInstance();
    private static final ReceiverMachine receiverMachine = ReceiverMachine.getInstance();
    private static final Channel channel = Channel.getInstance();

    public void run() throws FileNotFoundException {
        receiverMachine.setChannel(channel);
        senderMachine.setChannel(channel);
        senderMachine.write(receiverMachine);
    }
}
