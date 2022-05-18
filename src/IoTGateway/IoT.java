package IoTGateway;

import java.io.*;
import java.net.*;

import static java.lang.Thread.sleep;


//TimeUnit.SECONDS.sleep(1);
class IoT
{
    static InetAddress[] allSensorIps;

    static {
        try {
            allSensorIps = new InetAddress[]{InetAddress.getByName("172.20.0.2"),InetAddress.getByName("172.20.0.3"),InetAddress.getByName("172.20.0.4"),InetAddress.getByName("172.20.0.5")};
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    static int messageId = 0;
    static DatagramSocket clientSocket = null;
    static String messageTypeForSensor = "post";
    static final int sensorPort = 4242;
    static final String GatewayIPAdr = "172.20.0.15";
    IoT() throws UnknownHostException {
    }
    public static void requestAllIpsFromSensor() throws SocketException, UnknownHostException {
       /* byte[] sendData = new byte[128];
        byte[] receiveData = new byte[128];
        clientSocket = new DatagramSocket(6969);

        InetAddress hostIp = InetAddress.getLocalHost();
        System.out.println(hostIp + "\n\n\n\n\n");*/
    }
    public static void main(String args[]) throws Exception {
        clientSocket = new DatagramSocket(6969);
        try {
            int whichPortsNow = 0;
            while(true) {
                for(int i = 0; i<4; i++) {
                    sendDataToSensors(allSensorIps[i]);
                }
                Thread.sleep(10000);
            }

        } catch (SocketException e) {
            System.out.println(e.getMessage());

        } catch (IOException a) {
            System.out.println(a.getMessage());
        }
        clientSocket.close();
    }

    private static void sendDataToSensors(InetAddress dstIPAdr) throws IOException, InterruptedException {
        byte[] sendData = new byte[512];
        byte[] receiveData = new byte[512];

        String sentence =  GatewayIPAdr + "," + dstIPAdr + "," + "6969" + "," + String.valueOf(messageId++) + "," + messageTypeForSensor;

        sendData = sentence.getBytes();
        InetAddress sensorIP = InetAddress.getByName(GatewayIPAdr);

        System.out.println(sensorIP);

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dstIPAdr, sensorPort);

        clientSocket.send(sendPacket);
        System.out.println("Packet was send to Sensor: " + dstIPAdr);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("Data from Sensor:" + modifiedSentence);
    }
}
