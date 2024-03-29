package Sender;

import Receiver.ReceiverTransport;

import java.io.*;
import java.util.*;

public class SenderUpperApplication {
    private static final SenderUpperApplication instance = new SenderUpperApplication();

    public static SenderUpperApplication getInstance() {
        return instance;
    }

    private static final SenderTransport senderTransport = SenderTransport.getInstance();

    public String write(ReceiverTransport receiverTransport) throws FileNotFoundException {
        // bring scenario filePath from config.
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        // connection Setup
        senderTransport.connectionSetup(receiverTransport);
        // read scenario file
        Scanner scanner = new Scanner(new File(properties.getProperty("sender_scenario_file")));
        // execute scenario file
        while (scanner.hasNext()) {
            try {
                int read1 = Integer.parseInt(scanner.next());
                while (!senderTransport.getData(read1)){
                    System.out.println("sender window does not have enough space");
                }
                double read2 = Double.parseDouble(scanner.next());
                read2 *= 1000;
                // Thread sleep for idle
                System.out.println("senderUpperApplication is idling");
                System.out.println("");
                Thread.sleep((long) read2);
            } catch (Exception e) {
                System.out.println("endOfFIle");
                System.out.println("연결 종료합니다.");
            }
        }
        while (true) {
            if (senderTransport.getSenderBuffer().getWindow().get(senderTransport.getSenderBuffer().getWindow().size()-1).isAcked() == true) {
                break;
            }
        }
        return senderTransport.connectionClose();
    }

    private SenderUpperApplication() {
    }
}
