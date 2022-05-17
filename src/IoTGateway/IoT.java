package IoTGateway;

import java.io.*;
import java.net.*;

import static java.lang.Thread.sleep;


//TimeUnit.SECONDS.sleep(1);
class IoT
{
    public static void main(String args[]) throws Exception {

        int messageId = 0;
        DatagramSocket clientSocket = null;
        String messageTypeForSensor = "post";
        try {
            //meinsocket
            clientSocket = new DatagramSocket(6969);
            byte[] sendData = new byte[512];
            byte[] receiveData = new byte[512];
            //Sensorports manuell
            int[] sensorports = {4242}; // following ports for next sensors: 9276, 9376, 9476
            //meine Adresse
            InetAddress IPAddress = InetAddress.getByName("localhost");
            int whichPortsNow = 0;
            while(true) {
                //Anfrage bezueglich der Informationen ... mein adress und mein Port
                String sentence =  "172.17.0.2" + "," + IPAddress + "," + "6969" + "," + String.valueOf(messageId++) + "," + messageTypeForSensor;

                //zeregung der Nachricht
                sendData = sentence.getBytes();
                InetAddress sensorIP = InetAddress.getByName("172.17.0.2");

                System.out.println(sensorIP);

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sensorIP, sensorports[0]);
                if(whichPortsNow>0){
                    whichPortsNow = 0;
                }

                clientSocket.send(sendPacket);
                System.out.println("Packet was send to: " + sensorIP);
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String modifiedSentence = new String(receivePacket.getData());
                System.out.println("FROM SERVER:" + modifiedSentence);

                Thread.sleep(10000);
            }

        } catch (SocketException e) {

        } catch (IOException a) {

        }
        clientSocket.close();
    }
}
