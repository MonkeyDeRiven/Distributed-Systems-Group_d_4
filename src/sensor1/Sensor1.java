package sensor1;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Sensor1{

     static DatagramSocket socket;
     static byte[] rcvData = new byte[512];
     static byte[] sendData = new byte[512];
     static InetAddress ipAdr;
     static String metaData;
     static String dstAdr;
     static String srcAdr;
     static String messageID;
     static String messageType;
     static String humidity;

     static String timeStamp;
     static String port;


     public static void openSocket() throws SocketException {
         System.out.println("socket will be opened");
         socket = new DatagramSocket(4242);
         System.out.println("socket was opened");
     }

     public void closeSocket(){
         socket.close();
     }

     public static void receiveDataFunc() throws IOException {
         System.out.println("data will be recieved");
         DatagramPacket receivedPacket =  new DatagramPacket(rcvData, rcvData.length);
         socket.setSoTimeout(1000);
         socket.receive(receivedPacket);
         ipAdr = receivedPacket.getAddress();
         metaData = new String(receivedPacket.getData());

         String tmp;
         String[] metaDataList = metaData.split(",");
         srcAdr = metaDataList[0]; //Ip Adress from Sensor
         dstAdr = metaDataList[1]; // IP Adress from Gateway
         port = metaDataList[2]; //port from Gateway
         messageID = metaDataList[3];
         messageType = metaDataList[4];

        Random r1 = new Random();
        humidity = String.valueOf(r1.nextInt(91) + 10);
        System.out.println("Current Humidity: " + humidity);
         System.out.println("data was recieved");
     }
    public static void sendDataFunc() throws IOException {
        System.out.println("data will be send");
         String sentence = srcAdr.toUpperCase() + "," + dstAdr.toUpperCase() + "," + String.valueOf(port) + "," +
                 String.valueOf(messageID) + "," + humidity;
         sendData = sentence.getBytes();

         DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdr, Integer.parseInt(port));
         socket.send(sendPacket);
        System.out.println("data was send");
    }

    public static void generateData(){

    }

     public static void main(String args[]){
        try{
            openSocket();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        while(true){
            try{
                receiveDataFunc();
                sendDataFunc();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
     }
}