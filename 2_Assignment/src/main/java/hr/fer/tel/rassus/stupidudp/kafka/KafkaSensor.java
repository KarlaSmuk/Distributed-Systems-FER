package hr.fer.tel.rassus.stupidudp.kafka;

import hr.fer.tel.rassus.stupidudp.model.Reading;
import hr.fer.tel.rassus.stupidudp.model.Sensor;
import hr.fer.tel.rassus.stupidudp.network.EmulatedSystemClock;

import java.util.ArrayList;
import java.util.List;

// Producer -> sends register message on topic "Register"
// Consumer -> receiving messages from topic "Command" and "Register"
public class KafkaSensor {

    public static String TOPIC_REGISTER = "Register";
    public static String TOPIC_COMMAND = "Command";
    public static Boolean start = false;
    public static Boolean stop = false;

    public static final EmulatedSystemClock emulatedSystemClock = new EmulatedSystemClock();

    public static Long sensorStartTime = 0L;
    public static Sensor sensor = new Sensor();
    public static List<Reading> readings = new ArrayList<Reading>();
}
