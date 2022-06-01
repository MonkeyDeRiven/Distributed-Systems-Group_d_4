package IoTGateway;



import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;


//TimeUnit.SECONDS.sleep(1);
class IoT
{
   static InetAddress serverAdr;

    static {
        try {
            serverAdr = InetAddress.getByName("server");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    static Socket gatewaySocket;

    static {
        try {
            gatewaySocket = new Socket(serverAdr, 1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static InetAddress[] allSensorIps;
    static int sensorCount = Integer.parseInt(System.getenv("numberOfSensors"));

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
    static final String GatewayIPAdr;
    static int messageIDForTCP = 0;
    static String sentence ="" ;


    static {
        try {
            GatewayIPAdr = InetAddress.getByName("iotgateway").toString().split("/")[1];
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    IoT() throws UnknownHostException {
    }
    public static void requestAllIpsFromSensor() throws SocketException, UnknownHostException {
       /* byte[] sendData = new byte[128];
        byte[] receiveData = new byte[128];
        clientSocket = new DatagramSocket(6969);

        InetAddress hostIp = InetAddress.getLocalHost();
        System.out.println(hostIp + "\n\n\n\n\n");*/
    }

    private static void sendDataToServer(DatagramPacket receivedData) throws IOException {


        DataOutputStream outToServer = new DataOutputStream(gatewaySocket.getOutputStream());
        outToServer.write(receivedData.getData());

    }
    public static void main(String args[]) throws Exception {
        clientSocket = new DatagramSocket(6969);
        try {
            int whichPortsNow = 0;
            while(true) {
                for(int i = 0; i<sensorCount; i++) {
                    sendDataToSensors(InetAddress.getByName("sensor" + i));
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

        String sentence =  GatewayIPAdr + "," + dstIPAdr + "," + "6969" + "," + String.valueOf(messageId++) + "," + messageTypeForSensor + ",";

        sendData = sentence.getBytes();
        InetAddress sensorIP = InetAddress.getByName(GatewayIPAdr);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dstIPAdr, sensorPort);


        clientSocket.send(sendPacket);
        System.out.println("Packet was send to Sensor: " + dstIPAdr);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.setSoTimeout(1000);
        clientSocket.receive(receivePacket);

        String modifiedSentence = new String(receivePacket.getData());
        String[] modifiedSentenceArr = modifiedSentence.split(",");
        modifiedSentence = "";

        for(int i = 0; i<modifiedSentenceArr.length-1; i++)
            modifiedSentence = modifiedSentence + modifiedSentenceArr[i] + ",";

       System.out.println("Data from Sensor:" + modifiedSentence);


        String receivePacketString = new String(receivePacket.getData());
        String[] messageArray = receivePacketString.split(",");
        String completeMessage = "";

        for(int i = 4; i<messageArray.length-1; i++){
            completeMessage += messageArray[i] + ",";
        }

        completeMessage = getTCPHeader() + "," + completeMessage;
        sendPacket = new DatagramPacket(completeMessage.getBytes(), completeMessage.getBytes().length);

        long timeStartTrip = System.currentTimeMillis();
        sendDataToServer(sendPacket);
        receiveACK();
        long timeEndTrip = System.currentTimeMillis();

        long RTT = timeEndTrip - timeStartTrip;
        System.out.println("Round Trip Time: " + RTT + "ms\n");
    }

    private static void receiveACK() throws IOException {

        byte[] buffer = new byte[1];

        while (true) {
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(gatewaySocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(gatewaySocket.getOutputStream());

            sentence += inputFromClient.readLine();
            if (sentence.length() < 2) {
                continue;
            }
            String sizeOfSentenceString = "" + sentence.charAt(0) + sentence.charAt(1);

            buffer = sizeOfSentenceString.getBytes();
            ByteBuffer wrapped = ByteBuffer.wrap(buffer);
            short sizeOfMessage = wrapped.getShort();

            if (sentence.length() > 2 + sizeOfMessage) {
                sentence = sentence.substring(2);
                String[] sentenceArray = sentence.substring(0, sizeOfMessage).split(",");
                String comepleteMessage = "";
                String messageID = "";
                for(int i = 4; i<sentenceArray.length; i++){
                    comepleteMessage += sentenceArray[i];
                    if(i == 3)
                        messageID = sentenceArray[i];
                }
                sentence = sentence.substring(sizeOfMessage);
            }
        }




    }

    private static String getTCPHeader() throws UnknownHostException {
        String sourceIP = InetAddress.getByName("iotgateway").toString().split("/")[1];
        String destinationIP = InetAddress.getByName("server").toString().split("/")[1];
        String port = "1337";
        String messageID = String.valueOf(messageIDForTCP++);

        String header = destinationIP + "," + sourceIP + "," +  port + "," + messageID + "," + "HTTP";
        return header;
    }




}

/*
    Thread serverThread = new Thread(){
        //Code for TCP Client
        public void run(){
            try {
                sendDataToServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

        serverThread.run();*/
