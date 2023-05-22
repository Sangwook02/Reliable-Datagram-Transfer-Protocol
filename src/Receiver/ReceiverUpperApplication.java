package Receiver;

import Packet.Segment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ReceiverUpperApplication {
    private int lastByteRead = -1;
    private boolean connection;
    private List<Integer> dataLength = new ArrayList<>();
    private ReceiverBuffer receiverBuffer = ReceiverBuffer.getInstance();
    private Queue<Double> idleList = new LinkedList<>();
    private static final ReceiverUpperApplication instance;

    static {
        try {
            instance = new ReceiverUpperApplication();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static ReceiverUpperApplication getInstance() {
        return instance;
    }

    private ReceiverUpperApplication() throws FileNotFoundException, InterruptedException {
    }

    public void setConnection(boolean connection) {
        this.connection = connection;
    }

    public List<Integer> getDataLength() {
        return dataLength;
    }

    public void readScenarioFile() throws FileNotFoundException {
        // bring scenario filePath from config.
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        // read scenario file
        Scanner scanner = new Scanner(new File(properties.getProperty("receiver_scenario_file")));
        // execute scenario file
        while (scanner.hasNext()) {
            try {
                double read1 = Double.parseDouble(scanner.next());
                idleList.offer(read1);
                String read2 = scanner.next();
            } catch (Exception e) {
                System.out.println("endOfFIle");
                System.out.println("연결 종료합니다2.");
            }
        }
    }

    public void windowToApplication(ArrayList<Segment> segments, Receiver receiver) {
        // TODO: in-order인 segment를 읽고 sliding.
        if (receiverBuffer.getWindow() == null) {
            return;
        }
        if (receiverBuffer.bring() == null) {
            return;
        }
        if (lastByteRead + 1 == receiverBuffer.bring().getSequenceNumber()) {
            System.out.println("if문 안으로 진입함");
            Segment segment = receiverBuffer.bring();
            dataLength.add(segment.getLength());
            this.lastByteRead += segment.getLength();
            receiverBuffer.setRcvBase(segment.getLength());
            return;
        }
    }

    public void read(Receiver receiver) throws InterruptedException {
        while (connection) {
            double idleTime = idleList.remove();
            idleList.offer(idleTime);
            Thread.sleep((long) idleTime);
            if (receiverBuffer.getWindow().size() != 0) {
                windowToApplication(receiverBuffer.getWindow(), receiver);
            }
        }
        System.out.println("connection = " + connection);
    }
}
