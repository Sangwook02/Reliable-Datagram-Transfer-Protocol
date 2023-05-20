package Sender;

import Receiver.Receiver;

import java.io.*;
import java.util.*;

public class SenderUpperApplication {
    private  static final SenderUpperApplication instance = new SenderUpperApplication();

    public static SenderUpperApplication getInstance() {
        return instance;
    }

    private static final Sender sender = Sender.getInstance();
    public String write(Receiver receiver) throws FileNotFoundException {
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
        sender.connectionSetup(receiver);
        // read scenario file
        Scanner scanner = new Scanner(new File(properties.getProperty("sender_scenario_file")));
        // execute scenario file
        while (scanner.hasNext()) {
            try {
                int read1 = Integer.parseInt(scanner.next());
                while (!sender.getData(read1)){
                    System.out.println("sender window does not have enough space");
                }
                int read2 = Integer.parseInt(scanner.next());

                // Thread sleep for idle
                System.out.print("idling for ");
                System.out.print(read2);
                System.out.println(" second(s)");
                System.out.println("");
                Thread.sleep(read2*1000);
            } catch (Exception e) {
                System.out.println("endOfFIle");
                System.out.println("연결 종료합니다.");
            }
        }
        return sender.connectionClose();
    }

    private SenderUpperApplication() {
    }
}
