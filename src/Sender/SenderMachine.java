package Sender;

import Channel.Channel;
import Receiver.ReceiverMachine;
import Receiver.ReceiverTransport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SenderMachine {
    private String ipAddress;
    private SenderUpperApplication senderUpperApplication = SenderUpperApplication.getInstance();
    private SenderTransport senderTransport = SenderTransport.getInstance();
    private Channel channel;
    private static final SenderMachine instance = new SenderMachine();

    public static SenderMachine getInstance() {
        return instance;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    private SenderMachine() {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.ipAddress = properties.getProperty("sender_ip_addr");
    }

    public void write(ReceiverMachine receiver) throws FileNotFoundException {
        Thread senderWrite = new Thread(() -> {
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
        });
        // start to periodically write data in window.
        senderWrite.start();
        // setup connection and start passing the data from scenario file to window.
        String isDone = senderUpperApplication.write(receiver.getReceiverTransport());
        /*
         isDone will be set "close" when the scenario file ends.
         Then, interrupt the thread to stop the write action and finish the program.
         */
        if (isDone == "close") {
            senderWrite.interrupt();
        }
    }
}
