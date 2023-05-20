package Channel;

import Packet.Segment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Channel {
    private static final Channel instance;

    static {
        try {
            instance = new Channel();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private double latency;
    private double smallCongestion;
    private double bigCongestion;
    private char operation;
    private int operationCount;
    private Queue<Character> operations = new LinkedList<>();
    private Queue<Integer> operationCounts = new LinkedList<>();
    private List<Integer> received = new ArrayList<>();

    public static Channel getInstance() {
        return instance;
    }

    private Channel() throws FileNotFoundException {
        init();
    }
    private void init() throws FileNotFoundException {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
            Scanner scanner = new Scanner(new File(properties.getProperty("channel_scenario_file")));
            parse(scanner.next());
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.latency = Double.parseDouble(properties.getProperty("channel_latency"));
        this.smallCongestion = Double.parseDouble(properties.getProperty("channel_small_congestion_delay"));
        this.bigCongestion = Double.parseDouble(properties.getProperty("channel_big_congestion_delay"));
    }
    private void parse(String operationScenario) {
        int tmp = 0;
        for(char i:operationScenario.toCharArray()) {
            if (Character.isLetter(i) && Arrays.asList('N','L', 'c', 'C').contains(i)) {
                if (tmp != 0) {
                    operationCounts.offer(tmp);
                    tmp = 0;
                }
                operations.offer(i);
            } else if (i == '*') {
                operationCounts.offer(-1);
                break;
            } else if (Character.isDigit(i)) {
                if (tmp == 0) {
                    tmp = Character.getNumericValue(i);
                }
                else {
                    tmp *= 10;
                    tmp += Character.getNumericValue(i);
                }
            }
        }
        if (tmp != 0) {
            operationCounts.offer(tmp);
        }
        this.operation = operations.remove();
        this.operationCount = operationCounts.remove();
        operations.offer(operation);
        operationCounts.offer(operationCount);
    }
    public void input(Segment segment) throws InterruptedException {
        System.out.println("channel이 받은 segment의 길이는 " + segment.getLength());
        System.out.println("");
        received.add(segment.getLength());
        System.out.println("received = " + received);

        // get operation to execute
        getOperation();

        System.out.println(operation);
        System.out.println(operationCount);
        if (operation == 'N') {
            System.out.println("NoError");
            Thread.sleep((long) (latency*1000));
            // TODO: pass segment to Receiver
        } else if (operation == 'L') {
            System.out.println("Loss");
        } else if (operation == 'c') {
            System.out.println("smallCongestion");
            Thread.sleep((long) (latency*1000 + smallCongestion*1000));
        } else if (operation == 'C') {
            System.out.println("bigCongestion");
            Thread.sleep((long) (latency*1000 + bigCongestion*1000));
        } else {
            System.out.println("something went wrong");
        }
    }
    public void getOperation() {
        if (operationCount == 0) {
            // 새로운 operation  꺼내 사용.
            operation = operations.remove();
            operationCount = operationCounts.remove();
            operations.offer(operation);
            operationCounts.offer(operationCount);
        } else if (operationCount == -1) {
            return;
        }
        else {
            this.operationCount -= 1;
            System.out.println("decrease operationCount");
        }
    }
}
