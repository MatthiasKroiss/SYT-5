package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Client extends Thread {

    private DatagramSocket socket;
    private InetAddress address;

    private byte[] sendBuffer = new byte[512];
    private byte[] receiveBuffer = new byte[512];

    private Map<String, Double> tag_kwh = new HashMap<String, Double>();

    public void run() {
        try {
            int counter = 100;
            socket = new DatagramSocket();
            address = InetAddress.getByName("localhost");
            while (counter <= 130) {
                JsonObject received = sendCommand("report " + counter);
                System.out.println(received.get("RFID tag"));
                String rfidTag = received.get("RFID tag").getAsString();
                double eStart = received.get("E start").getAsDouble();
                double eEnd = received.get("E pres").getAsDouble() / 10000;
                double dif = eEnd - eStart;
                if (tag_kwh.containsKey(rfidTag)) {
                    // If it exists, add up the eEnd value
                    double currentKW = tag_kwh.get(rfidTag);
                    tag_kwh.put(rfidTag, (double) Math.round((currentKW + eEnd)*100)/100);
                } else {
                    // If it doesn't exist, add it to the map with the corresponding eEnd value
                    tag_kwh.put(rfidTag, (double) Math.round(eEnd*100)/100);
                }

                counter++;
            }
            socket.close();

            try (OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream("verbrauch.csv"));
            ) {
                // Write Headline
                fout.write("RFID_TAG;kWh\n");
                for (Map.Entry<String, Double> entry : tag_kwh.entrySet()) {
                    String rfidTag = entry.getKey();
                    double eStart = entry.getValue();
                    fout.write(rfidTag + ";" + eStart + "\n");
                    System.out.println("RFID_Tag: " + rfidTag + ", kWh: " + eStart + "\n");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send the Command to the Server
     * @param msg - Command 100-130
     * @return JsonObject
     * @throws IOException
     */
    public JsonObject sendCommand(String msg) throws IOException {
        sendBuffer = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(sendBuffer, sendBuffer.length, address, 7090);
        socket.send(packet);
        packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        JsonObject jsonObject = JsonParser.parseString(received)
                .getAsJsonObject();
        return jsonObject;
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");

        Client kebaClient = new Client();
        kebaClient.start();
    }
}

