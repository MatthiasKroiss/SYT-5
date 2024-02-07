package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/*
Anleitung:
jar-Generierung (im Projektunterordner out/production/KebaUdp, trägt KebaUdpMockStation in Manifest ein):
jar -e KebaUdpMockStation -c -f kebaUdpMockStation.jar *.class
Aufruf (ruft KebaUdpMockStation-Klasse mit der angegebenen .csv-Datei als Eingabedatei auf):
java -jar kebaUdpMockStation.jar station-19237584.csv
in IntelliJ/...: csv-Datei als Argument an main
 */
public class KebaUdpMockStation extends Thread {

    private String filename;
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[512]; // pay load size until July 2022: about 350 characters

    public KebaUdpMockStation(String filename) throws SocketException, UnknownHostException {
        //InetAddress ipAddresses = getIpV4Address();
        socket = new DatagramSocket(7090);//, ipAddresses);
        this.filename = filename;
    }


    Map<String, String> importSessions(BufferedReader br) throws IOException {
        Map<String, String> sessions = new TreeMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            String[] cell = line.split(";");
            String[] key = cell[1].split(":");
            if ("\"ID\"".equals(key[0])) {
                String sessionId = key[1].substring(2, 5);
                sessions.put(sessionId, line);
            }
        }
        return sessions;
    }

    public void run() {
        String prevLine = ""; // line read previously
        running = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            Map<String, String> sessions = importSessions(br);

            while (running) {
                DatagramPacket packet
                        = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    System.err.println(packet);

                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    String received
                            = new String(packet.getData(), 0, packet.getLength());
                    System.err.println(address.toString() + ";" + port + ";" + received);

                    packet = new DatagramPacket(buf, buf.length, address, port);
                    if (received.startsWith("report 1")) {
                        String text = sessions.get(received.substring(7, 10));
                        if (text != null) {
                            prevLine = text;
                        }
                        // transform csv lines (; separated) back to json lines (<LF> separated)
                        text = prevLine.replace(';', (char) 0x0A);
                        packet.setData(text.getBytes(StandardCharsets.UTF_8));
                        socket.send(packet);
                    } // report 100?
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } // while
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                String filename = args[0];
                KebaUdpMockStation server = new KebaUdpMockStation(filename);
                server.start();
            } else {
                System.err.println("Aufruf: KebaUdpMockStation <Name der Import-Datei>");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected InetAddress getIpV4Address() {
        InetAddress ipAddress = null;
        String ip;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // *EDIT*
                    if (addr instanceof Inet6Address) continue;

                    ip = addr.getHostAddress();
                    System.out.println(iface.getDisplayName() + " " + ip);
                    ipAddress = addr;
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return ipAddress;
    }
}