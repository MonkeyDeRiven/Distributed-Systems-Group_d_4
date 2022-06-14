package IoTGateway;



import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;


//TimeUnit.SECONDS.sleep(1);
class IoT {
    int serverPort = 1337;

    static int rttCounter = 0;
    static InetAddress serverAdr;

    static {
        try {
            serverAdr = InetAddress.getByName("server");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    static int sensorCount = Integer.parseInt(System.getenv("numberOfSensors"));


    static int messageId = 0;
    static DatagramSocket clientSocket = null;
    static String messageTypeForSensor = "post";
    static final int sensorPort = 4242;
    static final String GatewayIPAdr;
    static int messageIDForTCP = 0;
    static String sentence = "";


    static {
        try {
            GatewayIPAdr = InetAddress.getByName("iotgateway").toString().split("/")[1];
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    IoT() throws UnknownHostException {
    }


    public static void incrRTT() {
        rttCounter = rttCounter + 1;
    }

    public static void main(String args[]) throws Exception {
        int serverPort = 1337;
        ServerSocket serverSocket = new ServerSocket(serverPort);
        serverSocket.setSoTimeout(10000);

        clientSocket = new DatagramSocket(6969);
        try {
            int whichPortsNow = 0;
            while (true) {
                for (int i = 0; i < sensorCount; i++) {
                    sendDataToSensors(InetAddress.getByName("sensor" + i), rttCounter, serverSocket);
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

    private static synchronized void sendDataToSensors(InetAddress dstIPAdr, int rttCounter, ServerSocket serverSocket) throws IOException, InterruptedException {
        byte[] sendData = new byte[512];
        byte[] receiveData = new byte[512];

        String sentence = GatewayIPAdr + "," + dstIPAdr + "," + "6969" + "," + String.valueOf(messageId++) + "," + messageTypeForSensor + ",";

        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dstIPAdr, sensorPort);


        clientSocket.send(sendPacket);
        System.out.println("Packet was send to Sensor: " + dstIPAdr);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.setSoTimeout(1000);
        clientSocket.receive(receivePacket);

        String modifiedSentence = new String(receivePacket.getData());
        String[] modifiedSentenceArr = modifiedSentence.split(",");
        modifiedSentence = "";

        for (int i = 0; i < modifiedSentenceArr.length - 1; i++)
            modifiedSentence = modifiedSentence + modifiedSentenceArr[i] + ",";

        System.out.println("Data from Sensor:" + modifiedSentence);


        String receivePacketString = new String(receivePacket.getData());
        String[] messageArray = receivePacketString.split(",");
        String completeMessage = "";

        for (int i = 4; i < messageArray.length - 1; i++) {
            completeMessage += messageArray[i] + ",";
        }



            try {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

                Socket server = serverSocket.accept();
                System.out.println("Just connected to " + server.getRemoteSocketAddress());

                PrintWriter toClient =
                        new PrintWriter(server.getOutputStream(), true);
                BufferedReader fromClient =
                        new BufferedReader(
                                new InputStreamReader(server.getInputStream()));


                String l1 = fromClient.readLine();
                String l2 = fromClient.readLine();
                String l3 = fromClient.readLine();
                String l4 = fromClient.readLine();
                String l5 = fromClient.readLine();
                String l6 = fromClient.readLine();
                String l7 = fromClient.readLine();
                fromClient.readLine();
                String l9 = fromClient.readLine();

                if( true/*l1 == "POST sensordaten HTTP/1.1" && l9 == "Sende+Sensordaten"*/){

                        System.out.println("POST Request Acknowledged: sending Sensor Data in Style " + l1);
                    toClient.println("<html><head><title>Sende+Sensordaten</title></head><body>" + completeMessage + "</body></html>");

                }else{
                    System.out.println("request not understood");
                    toClient.println("request not understood");
                }
            }
            catch(Exception e) {
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